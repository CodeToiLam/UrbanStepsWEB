package vn.urbansteps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPosController {
    @GetMapping("/admin/pos")
    public String showPosPage() {
        return "admin/pos";
    }
}
