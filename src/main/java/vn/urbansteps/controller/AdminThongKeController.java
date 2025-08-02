package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.urbansteps.service.ThongKeService;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminThongKeController {
    
    @Autowired
    private ThongKeService thongKeService;
    
    @GetMapping("/thong-ke")
    public String thongKePage(Model model) {
        // Lấy dữ liệu thống kê
        Map<String, Object> thongKe = thongKeService.thongKeTongQuan();
        model.addAllAttributes(thongKe);
        model.addAttribute("title", "Dashboard - Thống kê tổng quan");
        return "admin/thong-ke";
    }
}
