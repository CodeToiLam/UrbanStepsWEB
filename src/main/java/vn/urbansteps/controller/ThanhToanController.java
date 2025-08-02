package vn.urbansteps.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.PhieuGiamGia;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.GioHangService;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.PhieuGiamGiaService;
import vn.urbansteps.service.TaiKhoanService;
import vn.urbansteps.service.EmailService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class ThanhToanController {

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String showCheckoutPage(
            @RequestParam(required = false) Boolean buyNow,
            @RequestParam(required = false) Integer buyNowItemId,
            @RequestParam(required = false) Integer buyNowQuantity,
            Model model,
            HttpSession session) {
        String username = (String) session.getAttribute("username");
        GioHang gioHang = null;
        String defaultAddress = null;

        if (username != null) {
            TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
            if (taiKhoan != null) {
                gioHang = gioHangService.getGioHangWithItemsByUserId(taiKhoan.getId());
                // defaultAddress = ... (cập nhật nếu cần dùng DiaChiGiaoHang)
            }
        } else {
            String sessionId = session.getId();
            gioHang = gioHangService.getGioHangWithItemsBySessionId(sessionId);
        }

        if (Boolean.TRUE.equals(buyNow) && buyNowItemId != null) {
            GioHang tempCart = new GioHang();
            GioHangItem selectedItem = gioHang.getItems().stream()
                    .filter(item -> item.getId().equals(buyNowItemId))
                    .findFirst()
                    .orElse(null);

            if (selectedItem != null) {
                if (buyNowQuantity != null && buyNowQuantity > 0) {
                    selectedItem.setSoLuong(buyNowQuantity);
                }
                tempCart.getItems().add(selectedItem);
                gioHang = tempCart;
            }
            model.addAttribute("buyNow", true);
            model.addAttribute("buyNowItemId", buyNowItemId);
            model.addAttribute("buyNowQuantity", buyNowQuantity);
        }

        if (gioHang == null || gioHang.getItems().isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống.");
            return "gio-hang";
        }

        List<PhieuGiamGia> publicVouchers = phieuGiamGiaService.findPublicVouchers();
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("publicVouchers", publicVouchers);
        model.addAttribute("discountedTotal", session.getAttribute("discountedTotal") != null ? session.getAttribute("discountedTotal") : gioHang.getTongTien());
        if (defaultAddress != null && !defaultAddress.isEmpty()) {
            model.addAttribute("defaultAddress", defaultAddress);
        }
        return "thanh-toan";
    }

    @GetMapping("/api/public-vouchers")
    @ResponseBody
    public ResponseEntity<List<PhieuGiamGia>> getPublicVouchers() {
        List<PhieuGiamGia> publicVouchers = phieuGiamGiaService.findPublicVouchers();
        return ResponseEntity.ok(publicVouchers);
    }

    @PostMapping("/api/apply-voucher")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyVoucher(@RequestParam String voucherCode, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            GioHang gioHang = null;
            String username = (String) session.getAttribute("username");
            if (username != null) {
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangByUserId(taiKhoan.getId());
                }
            } else {
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangBySessionId(sessionId);
            }

            if (gioHang == null) {
                response.put("success", false);
                response.put("message", "Giỏ hàng không tồn tại");
                return ResponseEntity.badRequest().body(response);
            }

            BigDecimal totalAmount = gioHang.getTongTien();
            BigDecimal discountedAmount = phieuGiamGiaService.applyVoucher(voucherCode, totalAmount);
            if (discountedAmount.compareTo(totalAmount) < 0) {
                session.setAttribute("appliedVoucherCode", voucherCode);
                session.setAttribute("discountedTotal", discountedAmount);
                response.put("success", true);
                response.put("message", "Áp dụng mã khuyến mãi thành công");
                response.put("discountedTotal", discountedAmount);
            } else {
                session.removeAttribute("appliedVoucherCode");
                session.removeAttribute("discountedTotal");
                response.put("success", false);
                response.put("message", "Mã khuyến mãi không hợp lệ hoặc không áp dụng được");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi áp dụng mã khuyến mãi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/apply-voucher-json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> applyVoucherJson(@RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String voucherCode = request.get("voucherCode");
        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Mã khuyến mãi không được để trống");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            GioHang gioHang = null;
            String username = (String) session.getAttribute("username");
            if (username != null) {
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangByUserId(taiKhoan.getId());
                }
            } else {
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangBySessionId(sessionId);
            }

            if (gioHang == null) {
                response.put("success", false);
                response.put("message", "Giỏ hàng không tồn tại");
                return ResponseEntity.badRequest().body(response);
            }

            BigDecimal totalAmount = gioHang.getTongTien();
            BigDecimal discountedAmount = phieuGiamGiaService.applyVoucher(voucherCode, totalAmount);
            if (discountedAmount.compareTo(totalAmount) < 0) {
                session.setAttribute("appliedVoucherCode", voucherCode);
                session.setAttribute("discountedTotal", discountedAmount);
                response.put("success", true);
                response.put("message", "Áp dụng mã khuyến mãi thành công");
                response.put("totalAmount", totalAmount);
                response.put("discountedTotal", discountedAmount);
            } else {
                session.removeAttribute("appliedVoucherCode");
                session.removeAttribute("discountedTotal");
                response.put("success", false);
                response.put("message", "Mã khuyến mãi không hợp lệ hoặc không áp dụng được");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi áp dụng mã khuyến mãi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam String hoTen,
            @RequestParam String sdt,
            @RequestParam(required = false) String email,
            @RequestParam String diaChiGiaoHang,
            @RequestParam int phuongThucThanhToan,
            @RequestParam(required = false) String ghiChu,
            @RequestParam(required = false) Boolean buyNow,
            @RequestParam(required = false) Integer buyNowItemId,
            @RequestParam(required = false) Integer buyNowQuantity,
            HttpSession session,
            Model model) {

        String username = (String) session.getAttribute("username");
        Integer taiKhoanId = null;
        boolean laKhachVangLai = true;

        if (username != null) {
            TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
            if (taiKhoan != null) {
                taiKhoanId = taiKhoan.getId();
                laKhachVangLai = false;
            }
        }

        GioHang gioHang = null;
        List<GioHangItem> itemsToProcess = null;
        if (!laKhachVangLai) {
            gioHang = gioHangService.getGioHangWithItemsByUserId(taiKhoanId);
        } else {
            gioHang = gioHangService.getGioHangWithItemsBySessionId(session.getId());
        }

        if (gioHang == null || gioHang.getItems().isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống.");
            return "thanh-toan";
        }

        if (Boolean.TRUE.equals(buyNow) && buyNowItemId != null) {
            GioHangItem selectedItem = gioHang.getItems().stream()
                    .filter(item -> item.getId().equals(buyNowItemId))
                    .findFirst()
                    .orElse(null);

            if (selectedItem == null) {
                model.addAttribute("error", "Không tìm thấy sản phẩm được chọn.");
                return "thanh-toan";
            }

            if (buyNowQuantity != null && buyNowQuantity > 0) {
                selectedItem.setSoLuong(buyNowQuantity);
            }
            itemsToProcess = List.of(selectedItem);
        } else {
            itemsToProcess = gioHang.getItems();
        }

        try {
            BigDecimal tongThanhToan = (BigDecimal) session.getAttribute("discountedTotal");
            if (tongThanhToan == null) {
                tongThanhToan = gioHang.getTongTien();
            }
            String appliedVoucherCode = (String) session.getAttribute("appliedVoucherCode");

            HoaDon hoaDon = hoaDonService.taoHoaDon(hoTen, sdt, email, diaChiGiaoHang,
                    phuongThucThanhToan, ghiChu, itemsToProcess, taiKhoanId, laKhachVangLai,
                    appliedVoucherCode, tongThanhToan);

            if (!Boolean.TRUE.equals(buyNow) && !laKhachVangLai) {
                gioHangService.clearGioHangByUserId(taiKhoanId);
            } else if (Boolean.TRUE.equals(buyNow)) {
                gioHangService.removeFromCart(buyNowItemId);
            }

            if (email != null && !email.isEmpty()) {
                String subject = "Xác nhận đơn hàng UrbanSteps";
                String text = "Cảm ơn bạn đã đặt hàng tại UrbanSteps! Mã đơn hàng: " + hoaDon.getMaHoaDon()
                        + "\nTổng tiền: " + hoaDon.getTongThanhToan() + " VNĐ\n";
                if (appliedVoucherCode != null) {
                    text += "Mã khuyến mãi: " + appliedVoucherCode + "\n";
                }
                text += "Chúng tôi sẽ liên hệ và giao hàng sớm nhất!";
                emailService.sendOrderConfirmation(email, subject, text);
            }

            session.removeAttribute("appliedVoucherCode");
            session.removeAttribute("discountedTotal");
            return "redirect:/checkout/success";
        } catch (Exception e) {
            model.addAttribute("error", "Đặt hàng thất bại: " + e.getMessage());
            model.addAttribute("gioHang", gioHang);
            return "thanh-toan";
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "thanh-toan-thanh-cong";
    }
}