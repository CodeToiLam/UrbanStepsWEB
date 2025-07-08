package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.HoaDonChiTiet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    
    // Tìm chi tiết theo hóa đơn
    List<HoaDonChiTiet> findByHoaDon_Id(Integer hoaDonId);
    
    // Tìm theo mã chi tiết hóa đơn
    Optional<HoaDonChiTiet> findByMaHoaDonChiTiet(String maHoaDonChiTiet);
    
    // Tìm chi tiết theo sản phẩm
    List<HoaDonChiTiet> findBySanPhamChiTiet_SanPham_Id(Integer sanPhamId);
    
    // Thống kê sản phẩm bán chạy
    @Query("SELECT hdct.sanPhamChiTiet.sanPham, SUM(hdct.soLuong) as totalSold, COUNT(hdct) as orderCount " +
           "FROM HoaDonChiTiet hdct " +
           "WHERE hdct.hoaDon.trangThai IN (3, 5) " + // Hoàn thành hoặc đã thanh toán
           "GROUP BY hdct.sanPhamChiTiet.sanPham ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> getBestSellingProducts();
    
    // Thống kê sản phẩm bán chạy trong khoảng thời gian
    @Query("SELECT hdct.sanPhamChiTiet.sanPham, SUM(hdct.soLuong) as totalSold, COUNT(hdct) as orderCount " +
           "FROM HoaDonChiTiet hdct " +
           "WHERE hdct.hoaDon.trangThai IN (3, 5) " +
           "AND hdct.createAt BETWEEN :startDate AND :endDate " +
           "GROUP BY hdct.sanPhamChiTiet.sanPham ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> getBestSellingProductsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    // Thống kê doanh thu theo sản phẩm
    @Query("SELECT hdct.sanPhamChiTiet.sanPham, SUM(hdct.thanhTien) as revenue, SUM(hdct.soLuong) as totalSold " +
           "FROM HoaDonChiTiet hdct " +
           "WHERE hdct.hoaDon.trangThai IN (3, 5) " +
           "GROUP BY hdct.sanPhamChiTiet.sanPham ORDER BY SUM(hdct.thanhTien) DESC")
    List<Object[]> getProductRevenue();
    
    // Thống kê lợi nhuận theo sản phẩm
    @Query("SELECT hdct.sanPhamChiTiet.sanPham, " +
           "SUM((hdct.giaBan - hdct.giaNhap) * hdct.soLuong) as totalProfit, " +
           "SUM(hdct.soLuong) as totalSold " +
           "FROM HoaDonChiTiet hdct " +
           "WHERE hdct.hoaDon.trangThai IN (3, 5) " +
           "GROUP BY hdct.sanPhamChiTiet.sanPham ORDER BY SUM((hdct.giaBan - hdct.giaNhap) * hdct.soLuong) DESC")
    List<Object[]> getProductProfit();
    
    // Tìm chi tiết hóa đơn của khách hàng cụ thể
    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.khachHang.id = :khachHangId")
    List<HoaDonChiTiet> findByCustomerId(@Param("khachHangId") Integer khachHangId);
    
    // Đếm số lượng sản phẩm đã bán
    @Query("SELECT SUM(hdct.soLuong) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.trangThai IN (3, 5)")
    Long getTotalProductsSold();
    
    // Đếm số lượng sản phẩm đã bán theo thời gian
    @Query("SELECT SUM(hdct.soLuong) FROM HoaDonChiTiet hdct " +
           "WHERE hdct.hoaDon.trangThai IN (3, 5) " +
           "AND hdct.createAt BETWEEN :startDate AND :endDate")
    Long getTotalProductsSoldByDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    // Thống kê chi tiết theo size và màu
    @Query("SELECT hdct.sanPhamChiTiet.kichCo, hdct.sanPhamChiTiet.mauSac, " +
           "SUM(hdct.soLuong) as totalSold, COUNT(hdct) as orderCount " +
           "FROM HoaDonChiTiet hdct " +
           "WHERE hdct.hoaDon.trangThai IN (3, 5) " +
           "AND hdct.sanPhamChiTiet.sanPham.id = :sanPhamId " +
           "GROUP BY hdct.sanPhamChiTiet.kichCo, hdct.sanPhamChiTiet.mauSac " +
           "ORDER BY SUM(hdct.soLuong) DESC")
    List<Object[]> getVariantStatistics(@Param("sanPhamId") Integer sanPhamId);
}
