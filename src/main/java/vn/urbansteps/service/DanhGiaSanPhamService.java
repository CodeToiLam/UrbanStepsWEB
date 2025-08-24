package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.repository.DanhGiaSanPhamRepository;
import vn.urbansteps.model.DanhGiaSanPham;
import java.util.List;

@Service
public class DanhGiaSanPhamService {
    @Autowired
    private DanhGiaSanPhamRepository repo;

    public boolean canReview(Integer taiKhoanId, Integer hoaDonChiTietId) {
        // Kiểm tra đã đánh giá chưa
        return !repo.existsByTaiKhoan_IdAndHoaDonChiTiet_Id(taiKhoanId, hoaDonChiTietId);
    }

    public void addReview(DanhGiaSanPham review) {
        repo.save(review);
    }

    public List<DanhGiaSanPham> getReviews(Integer sanPhamId) {
        var list = repo.findBySanPham_IdAndTrangThaiOrderByCreateAtDesc(sanPhamId, true);
        if (list == null || list.isEmpty()) {
            // fallback: return reviews regardless of trangThai (covers legacy rows with null/false)
            return repo.findBySanPham_IdOrderByCreateAtDesc(sanPhamId);
        }
        return list;
    }
}
