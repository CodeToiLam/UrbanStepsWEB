package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.TaiKhoan;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    
    Optional<GioHang> findByTaiKhoan(TaiKhoan taiKhoan);
    
    Optional<GioHang> findBySessionId(String sessionId);
    
    @Query("SELECT g FROM GioHang g LEFT JOIN FETCH g.items i LEFT JOIN FETCH i.sanPhamChiTiet spc LEFT JOIN FETCH spc.sanPham sp LEFT JOIN FETCH spc.kichCo kc LEFT JOIN FETCH spc.mauSac ms WHERE g.taiKhoan = :taiKhoan")
    Optional<GioHang> findByTaiKhoanWithItems(@Param("taiKhoan") TaiKhoan taiKhoan);
    
    @Query("SELECT g FROM GioHang g LEFT JOIN FETCH g.items i LEFT JOIN FETCH i.sanPhamChiTiet spc LEFT JOIN FETCH spc.sanPham sp LEFT JOIN FETCH spc.kichCo kc LEFT JOIN FETCH spc.mauSac ms WHERE g.sessionId = :sessionId")
    Optional<GioHang> findBySessionIdWithItems(@Param("sessionId") String sessionId);
}
