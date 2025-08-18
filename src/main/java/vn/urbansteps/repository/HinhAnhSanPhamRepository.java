package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.urbansteps.model.HinhAnhSanPham;

import java.util.List;

public interface HinhAnhSanPhamRepository extends JpaRepository<HinhAnhSanPham, Integer> {
    List<HinhAnhSanPham> findBySanPham_Id(Integer sanPhamId);

    // Lấy gallery theo thứ tự để hiển thị và xử lý kéo-thả
    List<HinhAnhSanPham> findBySanPham_IdOrderByThuTuAsc(Integer sanPhamId);
}
