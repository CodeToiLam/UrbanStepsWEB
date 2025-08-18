package vn.urbansteps.service;

import org.springframework.stereotype.Service;
import vn.urbansteps.model.HinhAnh;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.repository.HinhAnhRepository;
import vn.urbansteps.repository.HinhAnhSanPhamRepository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class HinhAnhService {
    private final HinhAnhRepository hinhAnhRepository;
    private final HinhAnhSanPhamRepository hinhAnhSanPhamRepository;
    
    public HinhAnhService(HinhAnhRepository hinhAnhRepository,
                          HinhAnhSanPhamRepository hinhAnhSanPhamRepository) {
        this.hinhAnhRepository = hinhAnhRepository;
        this.hinhAnhSanPhamRepository = hinhAnhSanPhamRepository;
    }
    
    @PostConstruct
    public void init() {
        System.out.println("HinhAnhService initialized");
        System.out.println("hinhAnhRepository is null: " + (hinhAnhRepository == null));
    }

    /**
     * Lấy tất cả hình ảnh của sản phẩm theo ID
     */
    public List<HinhAnh> getAllImagesBySanPhamId(Integer sanPhamId) {
        try {
            if (hinhAnhRepository == null) {
                System.err.println("ERROR: hinhAnhRepository is null!");
                return new ArrayList<>();
            }
            // Ưu tiên lấy gallery theo bảng liên kết cấp sản phẩm
            if (hinhAnhSanPhamRepository != null) {
                var links = hinhAnhSanPhamRepository.findBySanPham_IdOrderByThuTuAsc(sanPhamId);
                if (links != null && !links.isEmpty()) {
                    List<HinhAnh> list = new ArrayList<>();
                    for (var l : links) {
                        if (l.getHinhAnh() != null) list.add(l.getHinhAnh());
                    }
                    return list;
                }
            }
            // Fallback: lấy ảnh theo biến thể (bảng HinhAnh_SanPhamChiTiet)
            return hinhAnhRepository.findBySanPhamIdOrderByThuTuAsc(sanPhamId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy hình ảnh sản phẩm: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Chuyển đổi đường dẫn ảnh từ database thành đường dẫn sử dụng được trong HTML
     */
    public String convertImagePath(String dbPath) {
        if (dbPath == null || dbPath.trim().isEmpty()) {
            return "/images/no-image.jpg";
        }

        // Loại bỏ khoảng trắng
        dbPath = dbPath.trim();

        // Nếu đã là đường dẫn đầy đủ thì trả về
    if (dbPath.startsWith("/images/") || dbPath.startsWith("/uploads/")) {
            return dbPath;
        }

        // Nếu bắt đầu bằng "images/" thì thêm "/" vào đầu
    if (dbPath.startsWith("images/") || dbPath.startsWith("uploads/")) {
            return "/" + dbPath;
        }

        // Ngược lại thì thêm "/images/" vào đầu
        return "/images/" + dbPath;
    }

    /**
     * Lấy tất cả ảnh thuộc về một sản phẩm và xử lý đường dẫn
     */
    public List<HinhAnh> getProcessedImagesBySanPhamId(Integer sanPhamId) {
        List<HinhAnh> images = getAllImagesBySanPhamId(sanPhamId);
        
        System.out.println("=== DEBUG HINH ANH SERVICE ===");
        System.out.println("San pham ID: " + sanPhamId);
        System.out.println("So luong anh goc: " + images.size());
        
        // Xử lý đường dẫn cho từng ảnh
        for (HinhAnh img : images) {
            String originalPath = img.getDuongDan();
            String processedPath = convertImagePath(originalPath);
            System.out.println("Anh " + img.getId() + " - Goc: '" + originalPath + "' -> Xu ly: '" + processedPath + "'");
            img.setDuongDan(processedPath);
        }
        
        System.out.println("=== END DEBUG ===");
        return images;
    }

    /**
     * Lấy ảnh gallery cho sản phẩm - trước tiên thử lấy từ DB, nếu không có thì dùng ảnh đại diện
     */
    public List<HinhAnh> getGalleryImagesBySanPhamId(Integer sanPhamId, SanPham sanPham) {
        List<HinhAnh> images = new ArrayList<>();
        
        try {
            // Thử lấy ảnh từ database trước
            List<HinhAnh> dbImages = getAllImagesBySanPhamId(sanPhamId);
            
            if (!dbImages.isEmpty()) {
                // Có ảnh trong DB - xử lý đường dẫn
                for (HinhAnh img : dbImages) {
                    String originalPath = img.getDuongDan();
                    String processedPath = convertImagePath(originalPath);
                    img.setDuongDan(processedPath);
                    images.add(img);
                }
                System.out.println("Gallery - Lấy " + images.size() + " ảnh từ database");
            } else {
                // Không có ảnh trong DB - dùng ảnh đại diện
                if (sanPham != null && sanPham.getIdHinhAnhDaiDien() != null) {
                    HinhAnh mainImage = sanPham.getIdHinhAnhDaiDien();
                    String processedPath = convertImagePath(mainImage.getDuongDan());
                    
                    HinhAnh galleryImage = new HinhAnh();
                    galleryImage.setId(mainImage.getId());
                    galleryImage.setDuongDan(processedPath);
                    galleryImage.setMoTa(mainImage.getMoTa());
                    galleryImage.setThuTu(1);
                    
                    images.add(galleryImage);
                    System.out.println("Gallery - Dùng ảnh đại diện: " + processedPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy gallery: " + e.getMessage());
            // Fallback - dùng ảnh đại diện
            if (sanPham != null && sanPham.getIdHinhAnhDaiDien() != null) {
                HinhAnh mainImage = sanPham.getIdHinhAnhDaiDien();
                String processedPath = convertImagePath(mainImage.getDuongDan());
                
                HinhAnh galleryImage = new HinhAnh();
                galleryImage.setId(mainImage.getId());
                galleryImage.setDuongDan(processedPath);
                galleryImage.setMoTa(mainImage.getMoTa());
                galleryImage.setThuTu(1);
                
                images.add(galleryImage);
            }
        }
        
        return images;
    }
}
