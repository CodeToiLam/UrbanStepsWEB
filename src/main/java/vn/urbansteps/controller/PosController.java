package vn.urbansteps.controller;

import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.POSOrderRequest;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.HoaDonServiceImpl;
import vn.urbansteps.service.SanPhamService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/pos")
public class PosController {
// ...existing code...
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
        // Nếu dùng interface, cần ép kiểu sang HoaDonServiceImpl để gọi hàm đặc thù POS
        if (hoaDonService instanceof HoaDonServiceImpl) {
            return ((HoaDonServiceImpl) hoaDonService).createOrderPOS(
                request.getHoTen(),
                request.getSdt(),
                request.getGhiChu(),
                items,
                request.getTienMat(),
                request.getTienChuyenKhoan(),
                request.getPhuongThucThanhToan()
            );
        }
        throw new RuntimeException("POS order not supported");
    }

    // DTO cho request tạo đơn POS

    // Xem chi tiết đơn hàng
    @GetMapping("/order/{id}")
    public HoaDon getOrder(@PathVariable Integer id) {
        return hoaDonService.getOrderById(id);
    }
}
