package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.repository.SanPhamRepository;
import vn.urbansteps.repository.KhachHangRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ThongKeService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    public Map<String, Object> thongKeTongQuan() {
        Map<String, Object> result = new HashMap<>();

        // Tổng số sản phẩm
        Query q1 = entityManager.createNativeQuery("SELECT COUNT(*) FROM SanPham");
        result.put("tongSanPham", ((Number)q1.getSingleResult()).intValue());

        // Tổng số đơn hàng
        result.put("tongDonHang", (int) hoaDonRepository.count());

        // Tổng doanh thu
        BigDecimal tongDoanhThu = hoaDonRepository.getTotalRevenue();
        result.put("tongDoanhThu", tongDoanhThu != null ? tongDoanhThu.doubleValue() : 0.0);

        // Số lượng khách hàng
        Query q4 = entityManager.createNativeQuery("SELECT COUNT(*) FROM KhachHang");
        result.put("tongKhachHang", ((Number)q4.getSingleResult()).intValue());

        // Top 5 sản phẩm bán chạy
        Query q5 = entityManager.createNativeQuery(
                "SELECT sp.ten_san_pham, SUM(hdct.so_luong) AS so_luong_ban " +
                        "FROM HoaDonChiTiet hdct " +
                        "JOIN SanPhamChiTiet spct ON hdct.id_san_pham_chi_tiet=spct.id " +
                        "JOIN SanPham sp ON spct.id_san_pham=sp.id " +
                        "WHERE hdct.id_hoa_don IN (SELECT id FROM HoaDon WHERE trang_thai IN (3, 5)) " +
                        "GROUP BY sp.ten_san_pham ORDER BY so_luong_ban DESC");
        List<Object[]> topSP = q5.setMaxResults(5).getResultList();
        List<Map<String, Object>> topSanPham = new ArrayList<>();
        for (Object[] row : topSP) {
            Map<String, Object> sp = new HashMap<>();
            sp.put("tenSanPham", row[0]);
            sp.put("soLuongBan", row[1]);
            topSanPham.add(sp);
        }
        result.put("topSanPhamBanChay", topSanPham);

        return result;
    }
}