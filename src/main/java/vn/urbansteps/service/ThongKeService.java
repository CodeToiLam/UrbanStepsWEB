package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.*;

@Service
public class ThongKeService {
    @Autowired
    private EntityManager entityManager;

    public Map<String, Object> thongKeTongQuan() {
        Map<String, Object> result = new HashMap<>();
        
        // Tổng số loại sản phẩm
        Query q1 = entityManager.createNativeQuery("SELECT COUNT(*) FROM SanPham");
        Object tongSanPham = q1.getSingleResult();
        result.put("tongSanPham", tongSanPham != null ? ((Number)tongSanPham).intValue() : 0);
        
        // Tổng số đơn hàng
        Query q2 = entityManager.createNativeQuery("SELECT COUNT(*) FROM HoaDon");
        result.put("tongDonHang", ((Number)q2.getSingleResult()).intValue());
        
        // Tổng doanh thu (chỉ đơn hàng đã hoàn thành)
        Query q3 = entityManager.createNativeQuery("SELECT SUM(tong_thanh_toan) FROM HoaDon WHERE trang_thai='DELIVERED'");
        Object doanhThu = q3.getSingleResult();
        result.put("tongDoanhThu", doanhThu != null ? ((Number)doanhThu).doubleValue() : 0.0);
        
        // Số lượng khách hàng
        Query q4 = entityManager.createNativeQuery("SELECT COUNT(*) FROM KhachHang");
        result.put("tongKhachHang", ((Number)q4.getSingleResult()).intValue());
        
        // Top 5 sản phẩm bán chạy
        Query q5 = entityManager.createNativeQuery("SELECT TOP 5 sp.ten_san_pham, SUM(hdct.so_luong) AS so_luong_ban FROM HoaDonChiTiet hdct JOIN SanPhamChiTiet spct ON hdct.id_san_pham_chi_tiet=spct.id JOIN SanPham sp ON spct.id_san_pham=sp.id GROUP BY sp.ten_san_pham ORDER BY so_luong_ban DESC");
        @SuppressWarnings("unchecked")
        List<Object[]> topSP = q5.getResultList();
        List<Map<String, Object>> topSanPham = new ArrayList<>();
        for(Object[] row : topSP) {
            Map<String, Object> sp = new HashMap<>();
            sp.put("tenSanPham", row[0]);
            sp.put("soLuongBan", row[1]);
            topSanPham.add(sp);
        }
        result.put("topSanPhamBanChay", topSanPham);
        
        return result;
    }
}
