package vn.urbansteps.controller;

import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.POSOrderRequest;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.SanPhamService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pos")
public class PosController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private HoaDonService hoaDonService;

    // Tìm sản phẩm theo tên hoặc mã
    @GetMapping("/products")
    public List<SanPham> searchProducts(@RequestParam(required = false) String keyword) {
        return sanPhamService.timKiemTheoTen(keyword);
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