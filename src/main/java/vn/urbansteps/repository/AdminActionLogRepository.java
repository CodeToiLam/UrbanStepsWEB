package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.urbansteps.model.AdminActionLog;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {
    // Có thể thêm các phương thức truy vấn tuỳ ý
}
