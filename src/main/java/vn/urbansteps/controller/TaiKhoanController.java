package vn.urbansteps.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.TaiKhoanService;

@Controller
@RequestMapping("/tai-khoan")
public class TaiKhoanController {
    @GetMapping
    public String taiKhoanPage(Model model) {
        // Lấy username từ session (Spring Security)
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
        if (username != null && !"anonymousUser".equals(username)) {
            TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
            model.addAttribute("taiKhoan", taiKhoan);
            // TODO: Lấy danh sách địa chỉ đã lưu của user
            // ...existing code...
            // TODO: Lấy lịch sử đơn hàng
            // List<Order> orders = orderService.findByUserId(taiKhoan.getId());
            // model.addAttribute("orders", orders);
        }
        return "tai-khoan";
    }

    private static final Logger logger = LoggerFactory.getLogger(TaiKhoanController.class);

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
                           Model model) {
        TaiKhoan tk = new TaiKhoan();
        tk.setTaiKhoan(taiKhoan);
        tk.setEmail(email);
        tk.setMatKhau(matKhau);
        model.addAttribute("taiKhoan", tk);

        boolean hasError = false;

        if (taiKhoan.length() < 5 || taiKhoan.length() > 12) {
            model.addAttribute("usernameError", "Tên đăng nhập phải từ 6 đến 12 ký tự.");
            hasError = true;
        }

        if (!email.matches("^[\\w.-]+@(?:gmail\\.com|yahoo\\.com|outlook\\.com|hotmail\\.com)$")) {
            model.addAttribute("emailError", "Email chỉ được sử dụng các đuôi: gmail, yahoo, outlook, hotmail.");
            hasError = true;
        }


        if (matKhau.length() < 8 || matKhau.length() > 12) {
            model.addAttribute("passwordError", "Mật khẩu phải từ 8 đến 12 ký tự.");
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
        model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "dang-nhap";
    }

}

