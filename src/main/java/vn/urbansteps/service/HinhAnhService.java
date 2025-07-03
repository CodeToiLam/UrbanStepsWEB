package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.urbansteps.model.HinhAnh;
import vn.urbansteps.repository.HinhAnhRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class HinhAnhService {
    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    /**
     * Trả về đường dẫn đầy đủ của ảnh từ đường dẫn lưu trong DB
     */
    public String getFullImagePath(String duongDan) {
        if (duongDan == null) return "/images/no-image.jpg";

        // Xử lý đường dẫn ảnh
        if (duongDan.startsWith("/")) {
            duongDan = duongDan.substring(1);
        }

        if (duongDan.startsWith("images/")) {
            return "/" + duongDan;
        } else {
            return "/images/" + duongDan;
        }
    }

    /**
     * Kiểm tra xem ảnh có tồn tại không
     */
    public boolean imageExists(String duongDan) {
        // Triển khai logic kiểm tra ảnh tồn tại
        // (có thể dùng Resource, File, etc.)
        return true; // Mặc định trả về true
    }

    /**
     * Lấy tất cả hình ảnh của sản phẩm theo ID
     */
    public List<HinhAnh> getAllImagesBySanPhamId(Integer sanPhamId) {
        try {
            return hinhAnhRepository.findBySanPhamIdOrderByThuTuAsc(sanPhamId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy hình ảnh sản phẩm: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Chuyển đổi đường dẫn ảnh từ database thành đường dẫn sử dụng được trong HTML
     */


}
