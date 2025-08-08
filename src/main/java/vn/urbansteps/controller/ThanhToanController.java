package vn.urbansteps.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import vn.urbansteps.service.SanPhamChiTietService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Controller
@RequestMapping("/checkout")
public class ThanhToanController {

    private static final Logger logger = LoggerFactory.getLogger(ThanhToanController.class);

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

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    @GetMapping
    public String showCheckoutPage(
            @RequestParam(required = false) Boolean buyNow,
            @RequestParam(required = false) Integer buyNowItemId,
            @RequestParam(required = false) Integer buyNowQuantity,
            Model model,
            HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
        logger.info("Username from SecurityContext: {}", username);
        GioHang gioHang = null;
        String defaultAddress = ""; // Khởi tạo empty string thay vì null

        if (Boolean.TRUE.equals(buyNow) && buyNowItemId != null) {
            // Mua ngay - chỉ tạo giỏ hàng tạm thời với 1 sản phẩm
            GioHang tempCart = new GioHang();
            tempCart.setId(0); // ID tạm thời
            tempCart.setItems(new ArrayList<>());
            
            // Tìm sản phẩm từ buyNowItemId (đây là sanPhamChiTietId từ chi-tiet-san-pham.js)
            try {
                java.util.Optional<vn.urbansteps.model.SanPhamChiTiet> sanPhamChiTietOpt = sanPhamChiTietService.findById(buyNowItemId);
                if (sanPhamChiTietOpt.isPresent()) {
                    vn.urbansteps.model.SanPhamChiTiet sanPhamChiTiet = sanPhamChiTietOpt.get();
                    GioHangItem buyNowItem = new GioHangItem();
                    buyNowItem.setId(0); // ID tạm thời
                    buyNowItem.setGioHang(tempCart);
                    buyNowItem.setSanPhamChiTiet(sanPhamChiTiet);
                    buyNowItem.setSoLuong(buyNowQuantity != null && buyNowQuantity > 0 ? buyNowQuantity : 1);
                    buyNowItem.setGiaTaiThoidiem(sanPhamChiTiet.getGiaBanThucTe());
                    
                    tempCart.getItems().add(buyNowItem);
                    gioHang = tempCart;
                    
                    logger.info("Tạo giỏ hàng mua ngay: sanPhamChiTietId={}, soLuong={}", buyNowItemId, buyNowItem.getSoLuong());
                } else {
                    logger.warn("Không tìm thấy sản phẩm với ID: {}", buyNowItemId);
                    model.addAttribute("error", "Sản phẩm không tồn tại.");
                    return "gio-hang";
                }
            } catch (Exception e) {
                logger.error("Lỗi khi tạo giỏ hàng mua ngay: {}", e.getMessage(), e);
                model.addAttribute("error", "Có lỗi xảy ra khi xử lý mua ngay.");
                return "gio-hang";
            }
            
            model.addAttribute("buyNow", true);
            model.addAttribute("buyNowItemId", buyNowItemId);
            model.addAttribute("buyNowQuantity", buyNowQuantity);
        } else {
            // Checkout bình thường - lấy giỏ hàng của user
            if (username != null) {
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                logger.info("TaiKhoan found: {}", taiKhoan != null ? taiKhoan.getId() : "null");
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangWithItemsByUserId(taiKhoan.getId());
                }
            } else {
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangWithItemsBySessionId(sessionId);
            }
        }

        if (gioHang == null || gioHang.getItems().isEmpty()) {
            model.addAttribute("error", "Giỏ hàng của bạn đang trống.");
            return "gio-hang";
        }

        List<PhieuGiamGia> publicVouchers = phieuGiamGiaService.findPublicVouchers();
        model.addAttribute("gioHang", gioHang);
        model.addAttribute("publicVouchers", publicVouchers);
        model.addAttribute("discountedTotal", session.getAttribute("discountedTotal") != null ? session.getAttribute("discountedTotal") : gioHang.getTongTien());
        
        // Thêm logging để debug
        logger.info("Checkout page - GioHang items: {}, Total: {}", 
                gioHang.getItems().size(), gioHang.getTongTien());
        
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
            logger.info("Username from SecurityContext (apply-voucher): {}", username);
            if (username != null) {
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                logger.info("TaiKhoan found (apply-voucher): {}", taiKhoan != null ? taiKhoan.getId() : "null");
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
            logger.error("Lỗi khi áp dụng mã khuyến mãi: {}", e.getMessage(), e);
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
            logger.info("Username from SecurityContext (apply-voucher-json): {}", username);
            if (username != null) {
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                logger.info("TaiKhoan found (apply-voucher-json): {}", taiKhoan != null ? taiKhoan.getId() : "null");
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
            logger.error("Lỗi khi áp dụng mã khuyến mãi: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi áp dụng mã khuyến mãi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/place-order")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> placeOrder(
            @RequestBody Map<String, Object> orderData,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Bắt đầu xử lý đặt hàng...");
            String hoTen = (String) orderData.get("fullName");
            String sdt = (String) orderData.get("phoneNumber");
            String email = (String) orderData.get("email");
            String province = (String) orderData.get("province");
            String district = (String) orderData.get("district");
            String ward = (String) orderData.get("ward");
            String addressDetail = (String) orderData.get("addressDetail");
            String diaChiGiaoHang = addressDetail + ", " + ward + ", " + district + ", " + province;
            String ghiChu = (String) orderData.get("note");
            int phuongThucThanhToan = Integer.parseInt((String) orderData.get("paymentMethod"));
            String appliedVoucherCode = (String) orderData.get("promoCode");

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
            logger.info("Username from SecurityContext (place-order): {}", username);
            Integer taiKhoanId = null;
            boolean laKhachVangLai = true;

            if (username != null) {
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                logger.info("TaiKhoan found (place-order): {}", taiKhoan != null ? taiKhoan.getId() : "null");
                if (taiKhoan != null) {
                    taiKhoanId = taiKhoan.getId();
                    laKhachVangLai = false;
                } else {
                    logger.warn("Tài khoản không tồn tại cho username: {}", username);
                    response.put("success", false);
                    response.put("message", "Tài khoản không tồn tại hoặc phiên đăng nhập không hợp lệ");
                    return ResponseEntity.badRequest().body(response);
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
                logger.warn("Giỏ hàng rỗng hoặc không tồn tại");
                response.put("success", false);
                response.put("message", "Giỏ hàng của bạn đang trống.");
                return ResponseEntity.badRequest().body(response);
            }

            itemsToProcess = gioHang.getItems();

            try {
                BigDecimal tongThanhToan = (BigDecimal) session.getAttribute("discountedTotal");
                if (tongThanhToan == null) {
                    tongThanhToan = gioHang.getTongTien();
                }

                logger.info("Tạo hóa đơn với tổng thanh toán: {}", tongThanhToan);
                HoaDon hoaDon = hoaDonService.taoHoaDon(hoTen, sdt, email, diaChiGiaoHang,
                        phuongThucThanhToan, ghiChu, itemsToProcess, taiKhoanId, laKhachVangLai,
                        appliedVoucherCode, tongThanhToan);

                // XÓA GIỎ HÀNG SAU KHI ĐẶT HÀNG THÀNH CÔNG
                logger.info("Xóa giỏ hàng sau khi đặt hàng thành công");
                if (!laKhachVangLai) {
                    // User đã đăng nhập
                    gioHangService.clearGioHangByUserId(taiKhoanId);
                    logger.info("Đã xóa giỏ hàng cho user ID: {}", taiKhoanId);
                } else {
                    // Guest user
                    gioHangService.clearGioHangBySessionId(session.getId());
                    logger.info("Đã xóa giỏ hàng cho session ID: {}", session.getId());
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
                response.put("success", true);
                response.put("message", "Đặt hàng thành công!");
                logger.info("Đặt hàng thành công, mã hóa đơn: {}", hoaDon.getMaHoaDon());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Lỗi khi đặt hàng: {}", e.getMessage(), e);
                response.put("success", false);
                response.put("message", "Đặt hàng thất bại: " + e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            logger.error("Lỗi xử lý yêu cầu đặt hàng: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Đặt hàng thất bại: Lỗi xử lý yêu cầu");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "thanh-toan-thanh-cong";
    }
}