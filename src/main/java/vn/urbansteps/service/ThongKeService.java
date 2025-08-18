package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.model.HoaDon;
// import vn.urbansteps.repository.SanPhamRepository;
// import vn.urbansteps.repository.KhachHangRepository;
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

    // Removed unused repositories to avoid lint warnings

    @Autowired
    private ImageService imageService;

    public Map<String, Object> thongKeTongQuan() {
        Map<String, Object> result = new HashMap<>();

        // Tổng số sản phẩm
        Query q1 = entityManager.createNativeQuery("SELECT COUNT(*) FROM SanPham");
        result.put("tongSanPham", ((Number)q1.getSingleResult()).intValue());

    // Tổng số đơn hàng
    result.put("tongDonHang", (int) hoaDonRepository.count());

    // Tổng doanh thu (đơn hoàn thành)
    BigDecimal tongDoanhThu = hoaDonRepository.getTotalRevenueCompleted();
        result.put("tongDoanhThu", tongDoanhThu != null ? tongDoanhThu.doubleValue() : 0.0);

        // Số lượng khách hàng
        Query q4 = entityManager.createNativeQuery("SELECT COUNT(*) FROM KhachHang");
        result.put("tongKhachHang", ((Number)q4.getSingleResult()).intValue());

    // Top 5 sản phẩm bán chạy (kèm ảnh và doanh thu) - chỉ tính đơn đã hoàn thành
    Query q5 = entityManager.createNativeQuery(
        "SELECT sp.id, sp.ten_san_pham, COALESCE(SUM(hdct.so_luong), 0) AS so_luong_ban, " +
            "COALESCE(SUM(hdct.thanh_tien), 0) AS doanh_thu, ha.duong_dan AS image_path " +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON spct.id_san_pham = sp.id " +
            "LEFT JOIN HoaDonChiTiet hdct ON hdct.id_san_pham_chi_tiet = spct.id " +
            "AND hdct.id_hoa_don IN (SELECT id FROM HoaDon WHERE trang_thai IN (3)) " +
            "LEFT JOIN HinhAnh ha ON sp.id_hinh_anh_dai_dien = ha.id " +
            "GROUP BY sp.id, sp.ten_san_pham, ha.duong_dan " +
            "ORDER BY so_luong_ban DESC");
    @SuppressWarnings("unchecked")
    List<Object[]> topSP = (List<Object[]>) q5.setMaxResults(5).getResultList();
        List<Map<String, Object>> topSanPham = new ArrayList<>();
        for (Object[] row : topSP) {
            Map<String, Object> sp = new HashMap<>();
        // row[0]=sp.id, row[1]=ten_san_pham, row[2]=so_luong_ban, row[3]=doanh_thu, row[4]=image_path
        sp.put("tenSanPham", row[1]);
        sp.put("soLuongBan", row[2]);
        sp.put("doanhThu", row[3] instanceof BigDecimal ? ((BigDecimal) row[3]).doubleValue() : (row[3] != null ? ((Number) row[3]).doubleValue() : 0.0));
        String rawPath = row[4] != null ? row[4].toString() : null;
        String normalized = imageService != null ? imageService.normalizeImagePath(rawPath) : (rawPath != null ? rawPath : "/images/no-image.jpg");
        sp.put("imageUrl", normalized);
            topSanPham.add(sp);
        }
        result.put("topSanPhamBanChay", topSanPham);

        // Đơn hàng gần đây (10 đơn mới nhất)
        List<HoaDon> recent = hoaDonRepository.findTop10ByOrderByCreateAtDesc();
        List<Map<String, Object>> donHangGanDay = new ArrayList<>();
        for (HoaDon hd : recent) {
            Map<String, Object> m = new HashMap<>();
            m.put("maHoaDon", hd.getMaHoaDon());
            m.put("tenKhachHang", hd.getKhachHang() != null ? hd.getKhachHang().getHoTenKhachHang() : "Khách vãng lai");
            m.put("tongTien", hd.getTongThanhToan() != null ? hd.getTongThanhToan().doubleValue() : 0.0);
            m.put("trangThai", hd.getTrangThai());
            result.putIfAbsent("latestOrderTime", hd.getCreateAt());
            donHangGanDay.add(m);
        }
        result.put("donHangGanDay", donHangGanDay);

        // Phân bố trạng thái đơn hàng (nhóm chính như các nền tảng lớn)
        Map<String, Long> orderStatus = new LinkedHashMap<>();
        orderStatus.put("PENDING", countStatus((byte)0));
        orderStatus.put("CONFIRMED", countStatus((byte)1));
        orderStatus.put("SHIPPING", countStatus((byte)2));
        orderStatus.put("COMPLETED", countStatus((byte)3));
        orderStatus.put("CANCELLED", countStatus((byte)4));
        // optionally paid/return groups
        orderStatus.put("PAID", countStatus((byte)5));
        orderStatus.put("RETURNED", countStatus((byte)6));
        result.put("orderStatus", orderStatus);

        // Doanh thu 7 ngày qua (ngày và tổng doanh thu theo ngày), mặc định 0 cho ngày không có đơn
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime start = today.minusDays(6).atStartOfDay();
        java.time.LocalDateTime end = today.atTime(23,59,59);
        List<Object[]> revenueRows = hoaDonRepository.getDailyRevenue(start, end);
        Map<String, Double> revenue7d = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate d = today.minusDays(i);
            revenue7d.put(d.toString(), 0.0);
        }
        if (revenueRows != null) {
            for (Object[] row : revenueRows) {
                Object dateObj = row[0];
                Object revObj = row[1];
                String key = dateObj != null ? dateObj.toString() : null;
                if (key != null) {
                    double v = 0.0;
                    if (revObj instanceof BigDecimal) v = ((BigDecimal) revObj).doubleValue();
                    else if (revObj instanceof Number) v = ((Number) revObj).doubleValue();
                    // Normalize key to ISO date if needed
                    if (key.length() > 10) key = key.substring(0,10);
                    revenue7d.put(key, v);
                }
            }
        }
        result.put("revenue7d", revenue7d);

        return result;
    }

    private long countStatus(byte s) {
        try { return hoaDonRepository.countByTrangThai(s); } catch (Exception e) { return 0L; }
    }
}