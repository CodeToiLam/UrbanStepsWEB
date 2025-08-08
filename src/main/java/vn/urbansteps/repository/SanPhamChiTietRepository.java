package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.SanPhamChiTiet;

import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {
    
    // Đếm sản phẩm còn hàng
    @Query("SELECT COUNT(s) FROM SanPhamChiTiet s WHERE s.soLuong > 0 AND s.trangThai = true AND s.deleteAt IS NULL")
    long countInStockProducts();
    
    // Lấy danh sách sản phẩm hết hàng
    @Query("SELECT s FROM SanPhamChiTiet s WHERE s.soLuong <= 0 AND s.trangThai = true AND s.deleteAt IS NULL")
    List<SanPhamChiTiet> findOutOfStockProducts();
    
    // Lấy danh sách sản phẩm sắp hết hàng
    @Query("SELECT s FROM SanPhamChiTiet s WHERE s.soLuong <= :threshold AND s.soLuong > 0 AND s.trangThai = true AND s.deleteAt IS NULL")
    List<SanPhamChiTiet> findLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "JOIN FETCH spct.kichCo " +
            "JOIN FETCH spct.mauSac " +
            "WHERE spct.sanPham.id = :sanPhamId AND spct.trangThai = true")
    List<SanPhamChiTiet> findBySanPhamIdAndTrangThaiTrue(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT DISTINCT spct.kichCo.tenKichCo FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.id = :sanPhamId AND spct.trangThai = true " +
            "ORDER BY spct.kichCo.tenKichCo")
    List<String> findDistinctKichCoBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT DISTINCT spct.mauSac.tenMauSac FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.id = :sanPhamId AND spct.trangThai = true " +
            "ORDER BY spct.mauSac.tenMauSac")
    List<String> findDistinctMauSacBySanPhamId(@Param("sanPhamId") Integer sanPhamId);

    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE spct.sanPham.id = :sanPhamId " +
            "AND spct.kichCo.tenKichCo = :kichCo " +
            "AND spct.mauSac.tenMauSac = :mauSac " +
            "AND spct.trangThai = true")
    SanPhamChiTiet findBySanPhamIdAndKichCoAndMauSac(
            @Param("sanPhamId") Integer sanPhamId,
            @Param("kichCo") String kichCo,
            @Param("mauSac") String mauSac);
}