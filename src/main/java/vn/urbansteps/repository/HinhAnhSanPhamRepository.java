package vn.urbansteps.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.urbansteps.model.HinhAnhSanPham;
import java.util.List;

public interface HinhAnhSanPhamRepository extends JpaRepository<HinhAnhSanPham, Integer> {

    @Query("SELECT hasp FROM HinhAnhSanPham hasp JOIN FETCH hasp.hinhAnh WHERE hasp.sanPham.id = :sanPhamId")
    List<HinhAnhSanPham> findBySanPham_Id(@Param("sanPhamId") Integer sanPhamId);

    // Lấy gallery theo thứ tự để hiển thị và xử lý kéo-thả
    @Query("SELECT hasp FROM HinhAnhSanPham hasp JOIN FETCH hasp.hinhAnh WHERE hasp.sanPham.id = :sanPhamId ORDER BY hasp.thuTu ASC")
    List<HinhAnhSanPham> findBySanPham_IdOrderByThuTuAsc(@Param("sanPhamId") Integer sanPhamId);
}
