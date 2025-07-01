package vn.urbansteps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GioHangController {

    @GetMapping("/gio-hang")
    public String gioHang(Model model) {
        // TODO: Implement cart logic
        model.addAttribute("message", "Tính năng giỏ hàng đang được phát triển");
        return "gio-hang";
    }
}
