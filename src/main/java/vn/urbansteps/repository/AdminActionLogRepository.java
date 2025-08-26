package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.urbansteps.model.AdminActionLog;

import java.util.List;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Integer> {
    // Có thể thêm các phương thức truy vấn tuỳ ý
    List<AdminActionLog> findAllByOrderByThoiGianDesc();
}
