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
        if (rawPath.startsWith("/images/")) {
            return rawPath;
        }
        
        // Loại bỏ "images/" ở đầu nếu có
        if (rawPath.startsWith("images/")) {
            rawPath = rawPath.substring(7);
        }
        
        // Thêm /images/ vào đầu
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
            System.out.println("Creating gallery for product: " + sanPham.getTenSanPham() + " (ID: " + sanPham.getId() + ")");
            
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
                
                System.out.println("Using fallback main image: " + mainImagePath);
            }
            
            System.out.println("Created gallery with " + gallery.size() + " images for product " + sanPham.getTenSanPham());
            
        } catch (Exception e) {
            System.err.println("Error creating gallery for product " + sanPham.getTenSanPham() + ": " + e.getMessage());
            e.printStackTrace();
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
                    
                    System.out.println("Created gallery from filesystem with " + gallery.size() + 
                                     " images for product " + sanPham.getTenSanPham() + " (extension: " + extension + ")");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error creating gallery from filesystem: " + e.getMessage());
        }
        
        return gallery;
    }
    
    /**
     * Xử lý ảnh cho danh sách sản phẩm
     */
    public void processProductListImages(List<SanPham> products) {
        if (products == null) return;
        
        System.out.println("=== IMAGE SERVICE - PROCESSING " + products.size() + " PRODUCTS ===");
        
        for (SanPham product : products) {
            if (product.getIdHinhAnhDaiDien() != null) {
                String originalPath = product.getIdHinhAnhDaiDien().getDuongDan();
                String normalizedPath = normalizeImagePath(originalPath);
                
                // Giữ nguyên đường dẫn gốc, chỉ normalize
                product.getIdHinhAnhDaiDien().setDuongDan(normalizedPath);
                
                System.out.println("Product: " + product.getTenSanPham() + 
                                 " | Brand: " + (product.getThuongHieu() != null ? product.getThuongHieu().getTenThuongHieu() : "N/A") +
                                 " | Original: " + originalPath + " | Normalized: " + normalizedPath);
            } else {
                System.out.println("Product: " + product.getTenSanPham() + " | NO IMAGE");
            }
        }
        
        System.out.println("=== IMAGE PROCESSING COMPLETED ===");
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
            
            System.out.println("Processed single product image: " + product.getTenSanPham() + 
                             " | Path: " + normalizedPath);
        }
    }
}
