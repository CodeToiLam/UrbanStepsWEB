package vn.urbansteps.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.AdminActionLog;
import vn.urbansteps.repository.AdminActionLogRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminActionLogService {

    private final AdminActionLogRepository adminActionLogRepository;

    public void logAction(Integer adminId, String action, String description) {
        AdminActionLog log = new AdminActionLog();
        log.setAdminId(adminId);
        log.setHanhDong(action);
        log.setMoTa(description);
        log.setThoiGian(LocalDateTime.now());
        adminActionLogRepository.save(log);
    }

    public List<AdminActionLog> getAllLogs() {
        return adminActionLogRepository.findAllByOrderByThoiGianDesc();
    }
}
