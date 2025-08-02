package vn.urbansteps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminThongKePageController {
    @GetMapping("/admin/thong-ke")
    public String thongKePage() {
        return "admin/thong-ke";
    }
}
