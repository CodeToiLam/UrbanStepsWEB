package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.HinhAnh;

import java.util.List;
@Repository
public interface HinhAnhRepository extends JpaRepository<HinhAnh, Integer> {

    /**
     * Tìm tất cả hình ảnh của sản phẩm theo ID sản phẩm, sắp xếp theo thứ tự
     * Sửa lại query để lấy đúng ảnh từ bảng HinhAnh_SanPhamChiTiet
     */
    @Query(value = "SELECT DISTINCT h.* FROM HinhAnh h " +
            "JOIN HinhAnh_SanPhamChiTiet hspct ON h.id = hspct.id_hinh_anh " +
            "JOIN SanPhamChiTiet spct ON hspct.id_san_pham_chi_tiet = spct.id " +
            "WHERE spct.id_san_pham = :sanPhamId " +
            "ORDER BY h.thu_tu ASC", nativeQuery = true)
    List<HinhAnh> findBySanPhamIdOrderByThuTuAsc(@Param("sanPhamId") Integer sanPhamId);
}