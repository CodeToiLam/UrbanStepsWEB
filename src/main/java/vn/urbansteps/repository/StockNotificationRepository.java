package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.urbansteps.model.StockNotification;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.model.TaiKhoan;

import java.util.List;
import java.util.Optional;

public interface StockNotificationRepository extends JpaRepository<StockNotification, Integer> {
    List<StockNotification> findBySanPhamChiTietAndNotifiedFalse(SanPhamChiTiet sanPhamChiTiet);
    
    Optional<StockNotification> findByEmailAndSanPhamChiTietAndNotifiedFalse(String email, SanPhamChiTiet sanPhamChiTiet);
    
    Optional<StockNotification> findByTaiKhoanAndSanPhamChiTietAndNotifiedFalse(TaiKhoan taiKhoan, SanPhamChiTiet sanPhamChiTiet);
    
    @Query("SELECT COUNT(s) FROM StockNotification s WHERE s.notified = false")
    long countPendingNotifications();
}
