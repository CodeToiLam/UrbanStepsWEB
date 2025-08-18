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
        // Trả về danh sách biến thể (SanPhamChiTiet). Luôn hiển thị cả biến thể chưa có tồn kho để admin dễ tìm sp mới.
        List<SanPham> products = sanPhamService.timKiemTheoTen(keyword);
        return products.stream()
                .flatMap(sp -> {
                    List<SanPhamChiTiet> variants = sanPhamChiTietService.getBySanPhamId(sp.getId());
                    return variants.stream()
                            .filter(v -> v != null && Boolean.TRUE.equals(v.getTrangThai()))
                            .map(v -> {
                                Map<String, Object> m = new java.util.HashMap<>();
                                m.put("id", v.getId()); // id biến thể để thêm vào giỏ
                                String size = (v.getKichCo() != null ? v.getKichCo().getTenKichCo() : null);
                                String color = (v.getMauSac() != null ? v.getMauSac().getTenMauSac() : null);
                                String name = sp.getTenSanPham();
                                if (size != null || color != null) {
                                    name = name + " (" + (size != null ? size : "?") + ", " + (color != null ? color : "?") + ")";
                                }
                                m.put("tenSanPham", name);
                                m.put("giaBan", v.getGiaBanThucTe() != null ? v.getGiaBanThucTe() : sp.getGiaBan());
                                m.put("brand", sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : null);
                                m.put("image", sp.getIdHinhAnhDaiDien() != null && sp.getIdHinhAnhDaiDien().getDuongDan() != null
                                        ? sp.getIdHinhAnhDaiDien().getDuongDan() : null);
                                Integer soLuong = v.getSoLuong();
                                m.put("inStock", soLuong != null && soLuong > 0);
                                m.put("soLuong", soLuong == null ? 0 : soLuong);
                                return m;
                            });
                })
                // giới hạn một lượng vừa phải cho dropdown/list
                .limit(100)
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