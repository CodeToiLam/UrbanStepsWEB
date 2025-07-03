package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.SanPhamChiTiet;

import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

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