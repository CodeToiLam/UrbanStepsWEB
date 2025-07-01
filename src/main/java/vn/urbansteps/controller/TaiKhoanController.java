package vn.urbansteps.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.TaiKhoanService;

@Controller
@RequestMapping("/tai-khoan")
public class TaiKhoanController {


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

        model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/tai-khoan/dang-nhap";
    }
}