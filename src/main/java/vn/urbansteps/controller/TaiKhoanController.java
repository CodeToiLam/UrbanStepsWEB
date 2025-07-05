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

    private static final Logger logger = LoggerFactory.getLogger(TaiKhoanController.class);

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("/dang-ky")
    public String showRegistrationForm(Model model) {
        model.addAttribute("taiKhoan", new TaiKhoan());
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
    public String register(TaiKhoan taiKhoan, Model model) {
        try {
            if (taiKhoan.getTaiKhoan() == null || taiKhoan.getTaiKhoan().isEmpty() ||
                    taiKhoan.getMatKhau() == null || taiKhoan.getMatKhau().isEmpty()) {
                model.addAttribute("error", "Tài khoản và mật khẩu không được để trống.");
                return "dang-ky";
            }
            TaiKhoan existingAccount = taiKhoanService.findByTaiKhoan(taiKhoan.getTaiKhoan());
            if (existingAccount != null) {
                model.addAttribute("error", "Tài khoản đã tồn tại.");
                return "dang-ky";
            }
            taiKhoanService.registerTaiKhoan(taiKhoan);
            model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/tai-khoan/dang-nhap";
        } catch (Exception e) {
            logger.error("Lỗi khi đăng ký tài khoản: {}", e.getMessage());
            model.addAttribute("error", "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại.");
            return "dang-ky";
        }
    }
}