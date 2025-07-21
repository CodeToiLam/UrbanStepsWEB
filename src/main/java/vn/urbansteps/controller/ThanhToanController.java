package vn.urbansteps.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.GioHangService;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.TaiKhoanService;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class ThanhToanController {

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private HoaDonService hoaDonService;

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
                // defaultAddress = ... (update to use DiaChiGiaoHang if needed)
            }
        } else {
            String sessionId = session.getId();
            gioHang = gioHangService.getGioHangWithItemsBySessionId(sessionId);
        }

        if (Boolean.TRUE.equals(buyNow) && buyNowItemId != null) {
            // Create a temporary cart with only the selected item
            // This could be a new cart object with just the one item
            // or filtering the existing cart to only include the selected item
            GioHang tempCart = new GioHang();

            // Find the specific item in the regular cart
            GioHangItem selectedItem = gioHang.getItems().stream()
                    .filter(item -> item.getId().equals(buyNowItemId))
                    .findFirst()
                    .orElse(null);

            if (selectedItem != null) {
                // If a quantity was specified, use it; otherwise use the item's current quantity
                if (buyNowQuantity != null && buyNowQuantity > 0) {
                    selectedItem.setSoLuong(buyNowQuantity);
                }

                // Add only this item to the temporary cart
                tempCart.getItems().add(selectedItem);

                // Use this temporary cart for checkout
                gioHang = tempCart;
            }

            // Pass the "Buy Now" parameters to the view
            model.addAttribute("buyNow", true);
            model.addAttribute("buyNowItemId", buyNowItemId);
            model.addAttribute("buyNowQuantity", buyNowQuantity);
        }
        if (gioHang == null || gioHang.getItems().isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống.");
            return "gio-hang";
        }

        model.addAttribute("gioHang", gioHang);
        if (defaultAddress != null && !defaultAddress.isEmpty()) {
            model.addAttribute("defaultAddress", defaultAddress);
        }
        return "thanh-toan";
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

            // Update quantity if specified
            if (buyNowQuantity != null && buyNowQuantity > 0) {
                selectedItem.setSoLuong(buyNowQuantity);
            }

            // Create a list with just this item
            itemsToProcess = List.of(selectedItem);
        } else {
            // Normal checkout - process all items
            itemsToProcess = gioHang.getItems();
        }

        try {
            HoaDon hoaDon = hoaDonService.taoHoaDon(hoTen, sdt, email, diaChiGiaoHang,
                    phuongThucThanhToan, ghiChu, itemsToProcess, taiKhoanId, laKhachVangLai);

            // Only clear the entire cart if not "Buy Now"
            if (!Boolean.TRUE.equals(buyNow) && !laKhachVangLai) {
                gioHangService.clearGioHangByUserId(taiKhoanId);
            } else if (Boolean.TRUE.equals(buyNow)) {
                gioHangService.removeFromCart(buyNowItemId);
            }

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
