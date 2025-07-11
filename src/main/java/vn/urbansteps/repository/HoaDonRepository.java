package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.HoaDon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    
    // Tìm theo mã hóa đơn
    Optional<HoaDon> findByMaHoaDon(String maHoaDon);
    
    // Tìm theo khách hàng
    List<HoaDon> findByKhachHang_IdOrderByCreateAtDesc(Integer khachHangId);
    
    // Tìm theo trạng thái
    List<HoaDon> findByTrangThaiOrderByCreateAtDesc(Integer trangThai);
    
    // Tìm theo phương thức thanh toán
    List<HoaDon> findByPhuongThucThanhToanOrderByCreateAtDesc(Integer phuongThucThanhToan);
    
    // Tìm hóa đơn trong khoảng thời gian
    @Query("SELECT hd FROM HoaDon hd WHERE hd.createAt BETWEEN :startDate AND :endDate ORDER BY hd.createAt DESC")
    List<HoaDon> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Tìm hóa đơn theo khoảng giá
    List<HoaDon> findByTongThanhToanBetweenOrderByCreateAtDesc(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Thống kê doanh thu theo ngày
    @Query("SELECT DATE(hd.createAt) as date, SUM(hd.tongThanhToan) as revenue, COUNT(hd) as orderCount " +
           "FROM HoaDon hd WHERE hd.trangThai IN (3, 5) " + // Hoàn thành hoặc đã thanh toán
           "AND hd.createAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(hd.createAt) ORDER BY DATE(hd.createAt)")
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Thống kê doanh thu theo tháng
    @Query("SELECT YEAR(hd.createAt) as year, MONTH(hd.createAt) as month, " +
           "SUM(hd.tongThanhToan) as revenue, COUNT(hd) as orderCount " +
           "FROM HoaDon hd WHERE hd.trangThai IN (3, 5) " +
           "AND YEAR(hd.createAt) = :year " +
           "GROUP BY YEAR(hd.createAt), MONTH(hd.createAt) ORDER BY MONTH(hd.createAt)")
    List<Object[]> getMonthlyRevenue(@Param("year") Integer year);
    
    // Top khách hàng theo doanh thu
    @Query("SELECT hd.khachHang, SUM(hd.tongThanhToan) as totalSpent, COUNT(hd) as orderCount " +
           "FROM HoaDon hd WHERE hd.trangThai IN (3, 5) " +
           "GROUP BY hd.khachHang ORDER BY SUM(hd.tongThanhToan) DESC")
    List<Object[]> getTopCustomersByRevenue();
    
    // Đếm hóa đơn theo trạng thái
    long countByTrangThai(Integer trangThai);
    
    // Tổng doanh thu
    @Query("SELECT SUM(hd.tongThanhToan) FROM HoaDon hd WHERE hd.trangThai IN (3, 5)")
    BigDecimal getTotalRevenue();
    
    // Doanh thu theo khoảng thời gian
    @Query("SELECT SUM(hd.tongThanhToan) FROM HoaDon hd WHERE hd.trangThai IN (3, 5) " +
           "AND hd.createAt BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Hóa đơn cần xử lý (pending)
    @Query("SELECT hd FROM HoaDon hd WHERE hd.trangThai = 0 ORDER BY hd.createAt ASC")
    List<HoaDon> findPendingOrders();
    
    // Hóa đơn có thể hủy
    @Query("SELECT hd FROM HoaDon hd WHERE hd.trangThai IN (0, 1) ORDER BY hd.createAt DESC")
    List<HoaDon> findCancellableOrders();
    
    // Tìm kiếm nâng cao
    @Query("SELECT hd FROM HoaDon hd WHERE " +
           "(:maHoaDon IS NULL OR hd.maHoaDon LIKE %:maHoaDon%) " +
           "AND (:khachHangId IS NULL OR hd.khachHang.id = :khachHangId) " +
           "AND (:trangThai IS NULL OR hd.trangThai = :trangThai) " +
           "AND (:startDate IS NULL OR hd.createAt >= :startDate) " +
           "AND (:endDate IS NULL OR hd.createAt <= :endDate) " +
           "ORDER BY hd.createAt DESC")
    List<HoaDon> findWithFilters(@Param("maHoaDon") String maHoaDon,
                                @Param("khachHangId") Integer khachHangId,
                                @Param("trangThai") Integer trangThai,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
}
