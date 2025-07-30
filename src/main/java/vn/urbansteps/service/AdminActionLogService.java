package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.AdminActionLog;
import vn.urbansteps.repository.AdminActionLogRepository;

import java.util.List;

@Service
public class AdminActionLogService {
    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    public void logAction(String adminUsername, String action, String details) {
        AdminActionLog log = new AdminActionLog(adminUsername, action, details);
        adminActionLogRepository.save(log);
    }

    public List<AdminActionLog> getAllLogs() {
        return adminActionLogRepository.findAll();
    }
}
