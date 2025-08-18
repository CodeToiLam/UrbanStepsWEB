package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.HinhAnh;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.HinhAnhSanPham;
import vn.urbansteps.repository.HinhAnhSanPhamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service mới để xử lý tất cả logic ảnh một cách đơn giản và nhất quán
 */
@Service
public class ImageService {

    @Autowired
    private HinhAnhSanPhamRepository hinhAnhSanPhamRepository;
    
    /**
     * Chuẩn hóa đường dẫn ảnh - tất cả ảnh đều có format: /images/brand/product/main.ext
     */
    public String normalizeImagePath(String rawPath) {
        if (rawPath == null || rawPath.trim().isEmpty()) {
            return "/images/no-image.jpg";
        }
        
        rawPath = rawPath.trim();
        
        // Pass-through for known static roots
        if (rawPath.startsWith("/images/") || rawPath.startsWith("/uploads/")) {
            return rawPath;
        }
        
        // Allow relative roots for uploads/images
        if (rawPath.startsWith("images/") || rawPath.startsWith("uploads/")) {
            return "/" + rawPath;
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
            // 1) Ưu tiên lấy gallery từ DB link HinhAnhSanPham (đúng với flow upload hiện tại)
            List<HinhAnhSanPham> links = hinhAnhSanPhamRepository.findBySanPham_IdOrderByThuTuAsc(sanPham.getId());
            if (links != null && !links.isEmpty()) {
                gallery = links.stream()
                        .map(HinhAnhSanPham::getHinhAnh)
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList());
            }

            // 2) Fallback: nếu chưa có ảnh gallery thì dùng ảnh đại diện (nếu có)
            if (gallery.isEmpty() && sanPham.getIdHinhAnhDaiDien() != null) {
                HinhAnh fallback = sanPham.getIdHinhAnhDaiDien();
                // Bảo đảm đường dẫn hiển thị đúng
                fallback.setDuongDan(normalizeImagePath(fallback.getDuongDan()));
                gallery.add(fallback);
            }
        } catch (Exception ignored) {}

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
                    // Nếu là uploads => bỏ qua logic này (không suy luận từ filesystem)
                    if ("uploads".equalsIgnoreCase(pathParts[1])) {
                        return gallery; // trả về rỗng để fallback dùng ảnh đại diện
                    }
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
