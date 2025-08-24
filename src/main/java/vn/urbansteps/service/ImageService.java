package vn.urbansteps.service;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.HinhAnh;
import vn.urbansteps.model.SanPham;
// import vn.urbansteps.repository.HinhAnhRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service mới để xử lý tất cả logic ảnh một cách đơn giản và nhất quán
 */
@Service
public class ImageService {
    
    // @Autowired
    // private HinhAnhRepository hinhAnhRepository;
    
    /**
     * Chuẩn hóa đường dẫn ảnh - tất cả ảnh đều có format: /images/brand/product/main.ext
     */
    public String normalizeImagePath(String rawPath) {
        if (rawPath == null || rawPath.trim().isEmpty()) {
            return "/images/no-image.jpg";
        }
        
        rawPath = rawPath.trim();
        
        // Nếu đã đúng format thì return
        // If already absolute under /images/ or /uploads/ return as-is (but try to map uploads->images when possible)
        if (rawPath.startsWith("/images/") || rawPath.startsWith("/uploads/")) {
            // If path is under /uploads/, try to see if the same file exists under any known images location and prefer that
            try {
                if (rawPath.startsWith("/uploads/")) {
                    String rest = rawPath.substring("/uploads/".length());

                    // candidate locations to look for the file
                    java.nio.file.Path projectRoot = java.nio.file.Paths.get(System.getProperty("user.dir"));
                    java.nio.file.Path[] candidates = new java.nio.file.Path[] {
                            projectRoot.resolve("src/main/resources/static/images").resolve(rest.replace('/', java.io.File.separatorChar)),
                            projectRoot.resolve("target/classes/static/images").resolve(rest.replace('/', java.io.File.separatorChar)),
                            // also check if uploads folder itself contains the file (in case web path should remain /uploads/)
                            projectRoot.resolve("uploads").resolve(rest.replace('/', java.io.File.separatorChar))
                    };

                    for (java.nio.file.Path p : candidates) {
                        if (java.nio.file.Files.exists(p)) {
                            // prefer /images/ when we found a copy under images
                            if (p.toString().contains(java.io.File.separator + "images" + java.io.File.separator) || p.toString().contains("/images/")) {
                                return "/images/" + rest;
                            }
                            // otherwise return original uploads path if that's the one that exists
                            return rawPath;
                        }
                    }
                }
            } catch (Exception ignored) {}

            return rawPath;
        }

        // If starts with images/ or uploads/ (without leading slash) add slash and return
        if (rawPath.startsWith("images/") || rawPath.startsWith("uploads/")) {
            String candidate = "/" + rawPath;
            // same uploads -> images mapping for non-leading-slash case
            if (candidate.startsWith("/uploads/")) {
                try {
                    String rest = candidate.substring("/uploads/".length());
                    java.nio.file.Path projectRoot = java.nio.file.Paths.get(System.getProperty("user.dir"));
                    java.nio.file.Path[] candidates = new java.nio.file.Path[] {
                            projectRoot.resolve("src/main/resources/static/images").resolve(rest.replace('/', java.io.File.separatorChar)),
                            projectRoot.resolve("target/classes/static/images").resolve(rest.replace('/', java.io.File.separatorChar)),
                            projectRoot.resolve("uploads").resolve(rest.replace('/', java.io.File.separatorChar))
                    };
                    for (java.nio.file.Path p : candidates) {
                        if (java.nio.file.Files.exists(p)) {
                            if (p.toString().contains(java.io.File.separator + "images" + java.io.File.separator) || p.toString().contains("/images/")) {
                                return "/images/" + rest;
                            }
                            return candidate; // prefer uploads path if uploads folder contains it
                        }
                    }
                } catch (Exception ignored) {}
            }
            return candidate;
        }

        // Otherwise default to images/ namespace (legacy data)
        return "/images/" + rawPath;
    }
    
    /**
     * Lấy ảnh đại diện của sản phẩm
     */
    public String getProductMainImage(SanPham sanPham) {
        if (sanPham == null || sanPham.getIdHinhAnhDaiDien() == null) {
            return "/images/no-image.jpg";
        }
        
        return normalizeImagePath(sanPham.getIdHinhAnhDaiDien().getDuongDan());
    }
    
    /**
     * Tạo gallery ảnh thực tế từ DB hoặc file system
     */
    public List<HinhAnh> createSimpleGallery(SanPham sanPham) {
        List<HinhAnh> gallery = new ArrayList<>();
        
        if (sanPham == null || sanPham.getId() == null) {
            return gallery;
        }
        
    try {
            
            // Vì dữ liệu DB mapping sai, tạm thời sử dụng file system
            gallery = createGalleryFromFileSystem(sanPham);
            
            // Nếu vẫn không có ảnh, sử dụng ảnh đại diện
            if (gallery.isEmpty() && sanPham.getIdHinhAnhDaiDien() != null) {
                String mainImagePath = getProductMainImage(sanPham);
                
                HinhAnh fallbackImage = new HinhAnh();
                fallbackImage.setId(sanPham.getIdHinhAnhDaiDien().getId());
                fallbackImage.setDuongDan(mainImagePath);
                fallbackImage.setMoTa("Main image");
                fallbackImage.setThuTu(1);
                fallbackImage.setLaAnhChinh(true);
                gallery.add(fallbackImage);
                
                // fallback main image used
            }
            
            // gallery created
            
        } catch (Exception e) {
            // ignore errors to avoid noisy logs
        }
        
        return gallery;
    }
    
    /**
     * Tạo gallery ảnh từ file system dựa trên cấu trúc thư mục
     */
    private List<HinhAnh> createGalleryFromFileSystem(SanPham sanPham) {
        List<HinhAnh> gallery = new ArrayList<>();
        
        if (sanPham == null || sanPham.getIdHinhAnhDaiDien() == null) {
            return gallery;
        }
        
        try {
            String mainImagePath = sanPham.getIdHinhAnhDaiDien().getDuongDan();
            String normalizedPath = normalizeImagePath(mainImagePath);
            
            // Phân tích đường dẫn để tìm thư mục sản phẩm
            // Ví dụ: /images/adidas/adifom-supernova-triple-black/main.jpg
            if (normalizedPath.contains("/")) {
                String[] pathParts = normalizedPath.split("/");
                if (pathParts.length >= 4) { // ["", "images", "brand", "product", "image.jpg"]
                    String brand = pathParts[2];
                    String productFolder = pathParts[3];
                    String basePath = "/images/" + brand + "/" + productFolder + "/";
                    
                    // Xác định extension từ ảnh main
                    String mainImageName = pathParts[pathParts.length - 1]; // main.jpg hoặc main.webp
                    String extension = ".jpg"; // default
                    if (mainImageName.contains(".")) {
                        extension = mainImageName.substring(mainImageName.lastIndexOf("."));
                    }
                    
                    // Thêm ảnh main trước
                    HinhAnh mainImage = new HinhAnh();
                    mainImage.setId(1);
                    mainImage.setDuongDan(basePath + "main" + extension);
                    mainImage.setMoTa("Main product image");
                    mainImage.setThuTu(1);
                    mainImage.setLaAnhChinh(true);
                    gallery.add(mainImage);
                    
                    // Tạo danh sách ảnh phụ động từ 2 đến 10 (tối đa 9 ảnh phụ)
                    int order = 2;
                    for (int i = 2; i <= 10; i++) {
                        String imageFile = i + extension;
                        HinhAnh image = new HinhAnh();
                        image.setId(order);
                        image.setDuongDan(basePath + imageFile);
                        image.setMoTa("Product image " + order);
                        image.setThuTu(order);
                        image.setLaAnhChinh(false);
                        gallery.add(image);
                        order++;
                    }
                    
                    // gallery from filesystem created
                }
            }
            
    } catch (Exception e) { /* ignore */ }
        
        return gallery;
    }
    
    /**
     * Xử lý ảnh cho danh sách sản phẩm
     */
    public void processProductListImages(List<SanPham> products) {
        if (products == null) return;
        
    // normalize list
        
        for (SanPham product : products) {
            if (product.getIdHinhAnhDaiDien() != null) {
                String originalPath = product.getIdHinhAnhDaiDien().getDuongDan();
                String normalizedPath = normalizeImagePath(originalPath);
                
                // Giữ nguyên đường dẫn gốc, chỉ normalize
                product.getIdHinhAnhDaiDien().setDuongDan(normalizedPath);
                
                // Avoid triggering lazy loading of brand outside session
                try {
                    // Don't call lazy properties; just ensure image path is normalized.
                } catch (Exception ignore) {}
            }
        }
        
    // done
    }
    
    /**
     * Xử lý ảnh cho một sản phẩm
     */
    public void processProductImage(SanPham product) {
        if (product != null && product.getIdHinhAnhDaiDien() != null) {
            String originalPath = product.getIdHinhAnhDaiDien().getDuongDan();
            String normalizedPath = normalizeImagePath(originalPath);
            
            // Giữ nguyên đường dẫn gốc, chỉ normalize
            product.getIdHinhAnhDaiDien().setDuongDan(normalizedPath);
            
            // processed
        }
    }
}
