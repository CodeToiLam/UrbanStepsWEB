package vn.urbansteps.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.TaiKhoanService;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.KhachHang;
import vn.urbansteps.model.DiaChiGiaoHang;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.service.DiaChiGiaoHangService;
import vn.urbansteps.repository.ReturnRequestRepository;

@Controller
@RequestMapping("/tai-khoan")
public class TaiKhoanController {
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private DiaChiGiaoHangService diaChiGiaoHangService;
    @Autowired
    private vn.urbansteps.service.PhieuGiamGiaService phieuGiamGiaService;

    @Autowired
    private ReturnRequestRepository returnRequestRepository;
    @GetMapping
    public String taiKhoanPage(Model model) {
        // Lấy username từ session (Spring Security)
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
        if (username == null || "anonymousUser".equals(username)) {
            return "redirect:/dang-nhap?redirectUrl=/tai-khoan";
        }
        if (username != null && !"anonymousUser".equals(username)) {
            TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
            model.addAttribute("taiKhoan", taiKhoan);
            if (taiKhoan != null) {
                model.addAttribute("customerName", taiKhoan.getHoTenTaiKhoan() != null ? taiKhoan.getHoTenTaiKhoan() : taiKhoan.getTaiKhoan());
            }
            // TODO: Lấy danh sách địa chỉ đã lưu của user
            // ...existing code...
            // Lấy lịch sử đơn hàng
            KhachHang khachHang = khachHangRepository.findByTaiKhoan(taiKhoan).orElse(null);
            if (khachHang != null) {
                java.util.List<HoaDon> orders = hoaDonService.getOrdersByKhachHangId(khachHang.getId());
                // Build a map of orderId -> hasPendingReturn for quick lookup in template
                java.util.Map<Integer, Boolean> pendingMap = new java.util.HashMap<>();
                for (HoaDon hd : orders) {
                    long pendingCount = returnRequestRepository.countByOrderIdAndStatus(hd.getId(), vn.urbansteps.model.ReturnRequest.Status.NEW)
                            + returnRequestRepository.countByOrderIdAndStatus(hd.getId(), vn.urbansteps.model.ReturnRequest.Status.PROCESSING);
                    pendingMap.put(hd.getId(), pendingCount > 0);
                }
                model.addAttribute("orders", orders);
                model.addAttribute("pendingReturns", pendingMap);
            }
            // Addresses
            java.util.List<DiaChiGiaoHang> addresses = diaChiGiaoHangService.listByTaiKhoan(taiKhoan);
            model.addAttribute("addresses", addresses);
            // Voucher wallet: public vouchers (and later user-bound)
            try { model.addAttribute("vouchers", phieuGiamGiaService.findPublicVouchers()); } catch (Exception ignored) {}
        }
        return "tai-khoan";
    }

    // API: lấy danh sách voucher khả dụng (tạm thời: public vouchers). Có thể mở rộng: voucher đã claim theo user
    @GetMapping("/api/vouchers")
    @ResponseBody
    public java.util.List<vn.urbansteps.model.PhieuGiamGia> apiVouchers(){
        try {
            return phieuGiamGiaService.findPublicVouchers();
        } catch (Exception e){
            return java.util.List.of();
        }
    }

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("/dang-ky")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("taiKhoan")) {
            model.addAttribute("taiKhoan", new TaiKhoan());
        }
        return "dang-ky";
    }

    @GetMapping("/dang-nhap")
    public String showLoginForm(Model model, @RequestParam(value = "redirectUrl", required = false) String redirectUrl) {
        model.addAttribute("taiKhoan", new TaiKhoan());
        if (redirectUrl != null) {
            model.addAttribute("redirectUrl", redirectUrl);
        }
        return "dang-nhap";
    }
    @PostMapping("/dang-ky")
    public String register(@RequestParam String taiKhoan,
                           @RequestParam String email,
                           @RequestParam String matKhau,
                           @RequestParam("confirm-password") String confirmPassword,
                           Model model,
                           RedirectAttributes ra) {
        TaiKhoan tk = new TaiKhoan();
        tk.setTaiKhoan(taiKhoan);
        tk.setEmail(email);
        tk.setMatKhau(matKhau);
        model.addAttribute("taiKhoan", tk);

        boolean hasError = false;
        // Normalize inputs
        taiKhoan = taiKhoan == null ? "" : taiKhoan.trim();
        email = email == null ? "" : email.trim();

        // Username 6-12 per UI hint
        if (taiKhoan.length() < 6 || taiKhoan.length() > 12) {
            model.addAttribute("usernameError", "Tên đăng nhập phải từ 6 đến 12 ký tự.");
            hasError = true;
        }

        // Email whitelist common domains; basic shape check first
        if (!email.matches("^[\\w.+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") ||
            !email.matches("^[\\w.+-]+@(?:gmail\\.com|yahoo\\.com|outlook\\.com|hotmail\\.com)$")) {
            model.addAttribute("emailError", "Email chỉ được sử dụng các đuôi: gmail, yahoo, outlook, hotmail.");
            hasError = true;
        }

        // Password 8-12 and contains at least one letter and one digit
        if (matKhau == null || matKhau.length() < 8 || matKhau.length() > 12 ||
                !matKhau.matches(".*[A-Za-z].*") || !matKhau.matches(".*[0-9].*")) {
            model.addAttribute("passwordError", "Mật khẩu 8-12 ký tự, bao gồm chữ và số.");
            hasError = true;
        }

        if (!matKhau.equals(confirmPassword)) {
            model.addAttribute("confirmError", "Mật khẩu xác nhận không khớp.");
            hasError = true;
        }

        if (taiKhoanService.findByTaiKhoan(taiKhoan) != null) {
            model.addAttribute("usernameError", "Tài khoản đã tồn tại.");
            hasError = true;
        }

        if (taiKhoanService.findByEmail(email) != null) {
            model.addAttribute("emailError", "Email đã được sử dụng.");
            hasError = true;
        }

        if (hasError) return "dang-ky";

        tk.setRole("USER");
    taiKhoanService.registerTaiKhoan(tk);
    ra.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
    return "redirect:/dang-nhap";
    }
    @PostMapping("/huy-don-hang")
    public String huyDonHang(@RequestParam("orderId") Integer orderId, Model model) {
        HoaDon hoaDon = hoaDonService.getOrderById(orderId);
        if (hoaDon != null && (hoaDon.getTrangThai() == 0 || hoaDon.getTrangThai() == 1)) {
            hoaDon.setTrangThai((byte) 4); // 4: Đã hủy
            hoaDonService.save(hoaDon);
            model.addAttribute("message", "Đã hủy đơn hàng thành công.");
        } else {
            model.addAttribute("error", "Không thể hủy đơn hàng này.");
        }
        return "redirect:/tai-khoan";
    }

    @PostMapping("/cap-nhat")
    public String capNhatThongTin(@ModelAttribute("taiKhoan") TaiKhoan formTk, RedirectAttributes redirect) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
        if (username == null || "anonymousUser".equals(username)) {
            return "redirect:/dang-nhap";
        }
        TaiKhoan updated = taiKhoanService.updateProfile(username, tk -> {
            tk.setHoTenTaiKhoan(formTk.getHoTenTaiKhoan());
            tk.setSdt(formTk.getSdt());
            tk.setEmail(formTk.getEmail());
            tk.setGioiTinh(formTk.getGioiTinh());
            tk.setDiaChi(formTk.getDiaChi());
        });
        if (updated != null) {
            redirect.addFlashAttribute("success", "Cập nhật thông tin thành công");
        } else {
            redirect.addFlashAttribute("error", "Không thể cập nhật tài khoản");
        }
        return "redirect:/tai-khoan";
    }

    @PostMapping("/doi-mat-khau")
    public String doiMatKhau(@RequestParam String oldPassword,
                             @RequestParam String newPassword,
                             @RequestParam String confirmPassword,
                             RedirectAttributes redirect) {
        if (!newPassword.equals(confirmPassword)) {
            redirect.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
            return "redirect:/tai-khoan#security";
        }
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
        if (username == null || "anonymousUser".equals(username)) {
            return "redirect:/dang-nhap";
        }
        boolean changed = taiKhoanService.changePassword(username, oldPassword, newPassword);
        if (changed) {
            redirect.addFlashAttribute("success", "Đổi mật khẩu thành công");
        } else {
            redirect.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
        }
        return "redirect:/tai-khoan#security";
    }
}

