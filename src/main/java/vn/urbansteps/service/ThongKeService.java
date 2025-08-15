package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.repository.HoaDonRepository;
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

        // Tổng doanh thu
        BigDecimal tongDoanhThu = hoaDonRepository.getTotalRevenue();
        result.put("tongDoanhThu", tongDoanhThu != null ? tongDoanhThu.doubleValue() : 0.0);

        // Số lượng khách hàng
        Query q4 = entityManager.createNativeQuery("SELECT COUNT(*) FROM KhachHang");
        result.put("tongKhachHang", ((Number)q4.getSingleResult()).intValue());

    // Top 5 sản phẩm bán chạy (kèm ảnh và doanh thu)
    Query q5 = entityManager.createNativeQuery(
        "SELECT sp.id, sp.ten_san_pham, COALESCE(SUM(hdct.so_luong), 0) AS so_luong_ban, " +
            "COALESCE(SUM(hdct.thanh_tien), 0) AS doanh_thu, ha.duong_dan AS image_path " +
            "FROM SanPham sp " +
            "LEFT JOIN SanPhamChiTiet spct ON spct.id_san_pham = sp.id " +
            "LEFT JOIN HoaDonChiTiet hdct ON hdct.id_san_pham_chi_tiet = spct.id " +
            "AND hdct.id_hoa_don IN (SELECT id FROM HoaDon WHERE trang_thai IN (3, 5)) " +
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

        return result;
    }
}