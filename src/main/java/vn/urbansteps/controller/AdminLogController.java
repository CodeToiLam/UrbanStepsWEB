package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.urbansteps.model.AdminActionLog;
import vn.urbansteps.service.AdminActionLogService;

import java.util.List;

@RestController
@RequestMapping("/admin/logs")
public class AdminLogController {
    @Autowired
    private AdminActionLogService adminActionLogService;

    @GetMapping
    public List<AdminActionLog> getAllLogs() {
        return adminActionLogService.getAllLogs();
    }
}
