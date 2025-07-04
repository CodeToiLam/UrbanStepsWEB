package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.SanPhamChiTiet;

import java.util.Optional;

@Repository
public interface GioHangItemRepository extends JpaRepository<GioHangItem, Integer> {
    
    Optional<GioHangItem> findByGioHangAndSanPhamChiTiet(GioHang gioHang, SanPhamChiTiet sanPhamChiTiet);
    
    @Query("SELECT gi FROM GioHangItem gi LEFT JOIN FETCH gi.sanPhamChiTiet spc LEFT JOIN FETCH spc.sanPham sp LEFT JOIN FETCH spc.kichCo kc LEFT JOIN FETCH spc.mauSac ms WHERE gi.gioHang = :gioHang")
    java.util.List<GioHangItem> findByGioHangWithDetails(@Param("gioHang") GioHang gioHang);
    
    void deleteByGioHang(GioHang gioHang);
}
