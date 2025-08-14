package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.urbansteps.model.AdminActionLog;
import vn.urbansteps.service.AdminActionLogService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminLogController {
    @Autowired
    private AdminActionLogService adminActionLogService;

    // JSON API for logs
    @GetMapping("/logs")
    @ResponseBody
    public List<AdminActionLog> getAllLogs() {
        return adminActionLogService.getAllLogs();
    }

    // View page
    @GetMapping("/admin-logs")
    public String logsPage() {
        return "admin/admin-logs";
    }
}
