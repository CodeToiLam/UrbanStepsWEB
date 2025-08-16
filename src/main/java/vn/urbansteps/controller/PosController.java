package vn.urbansteps.controller;

import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.POSOrderRequest;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.SanPhamService;
import vn.urbansteps.service.SanPhamChiTietService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pos")
public class PosController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    // Tìm sản phẩm theo tên hoặc mã
    @GetMapping("/products")
    public List<Map<String, Object>> searchProducts(@RequestParam(required = false) String keyword) {
        // Trả về danh sách biến thể (SanPhamChiTiet) mặc định để có thể thêm vào giỏ
        List<SanPham> products = sanPhamService.timKiemTheoTen(keyword);
    return products.stream()
                .flatMap(sp -> {
                    List<SanPhamChiTiet> variants = sanPhamChiTietService.getBySanPhamId(sp.getId());
                    return variants.stream()
                            .filter(v -> v != null && Boolean.TRUE.equals(v.getTrangThai()) && v.getSoLuong() != null && v.getSoLuong() > 0)
                            .limit(1) // lấy 1 biến thể mặc định
                            .map(v -> {
                                Map<String, Object> m = new java.util.HashMap<>();
                                m.put("id", v.getId()); // dùng id biến thể để thêm vào giỏ
                                m.put("tenSanPham", sp.getTenSanPham());
                                m.put("giaBan", v.getGiaBanThucTe() != null ? v.getGiaBanThucTe() : sp.getGiaBan());
                m.put("brand", sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : null);
                m.put("image", sp.getIdHinhAnhDaiDien() != null && sp.getIdHinhAnhDaiDien().getDuongDan() != null
                    ? sp.getIdHinhAnhDaiDien().getDuongDan() : null);
                                return m;
                            });
                })
                .collect(Collectors.toList());
    }

    // Tạo đơn hàng tại quầy
    @PostMapping("/order")
    public HoaDon createOrder(@RequestBody POSOrderRequest request) {
        List<GioHangItem> items = request.toGioHangItems();
        return hoaDonService.createOrderPOS(
                request.getHoTen(),
                request.getSdt(),
                request.getGhiChu(),
                items,
                request.getTienMat(),
                request.getTienChuyenKhoan(),
                request.getPhuongThucThanhToan(),
                request.getVoucherCode(),
                request.getTongThanhToan()
        );
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/order/{id}")
    public HoaDon getOrder(@PathVariable Integer id) {
        return hoaDonService.getOrderById(id);
    }
}