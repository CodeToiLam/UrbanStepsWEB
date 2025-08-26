package vn.urbansteps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.urbansteps.service.AdminActionLogService;

@Controller
@RequestMapping("/admin/admin-logs")
@RequiredArgsConstructor
public class AdminLogController {
    private final AdminActionLogService adminLogService;

    @GetMapping
    public String showLogs(Model model) {
        model.addAttribute("logs", adminLogService.getAllLogs());
        return "admin/admin-logs";
    }
}
