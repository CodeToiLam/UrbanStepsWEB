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
    @Autowired(required = false)
    private vn.urbansteps.service.TaiKhoanService taiKhoanService;

    public void logAction(Integer adminId, String action, String details) {
    // Ignore non-mutating actions (views, logins, GET pages)
    if (isNonMutating(action, details)) return;
    String friendly = toFriendlyAction(action);
    AdminActionLog log = new AdminActionLog(adminId, friendly, details);
    adminActionLogRepository.save(log);
    }

    public List<AdminActionLog> getAllLogs() {
        return adminActionLogRepository.findAll();
    }

    /**
     * Convenience: log using the currently authenticated admin (if available).
     */
    public void logActionCurrent(String action, String details) {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null) return;
            Integer adminId = null;
            Object p = auth.getPrincipal();
            if (p instanceof vn.urbansteps.model.TaiKhoan tk) {
                adminId = tk.getId();
            } else if (p instanceof org.springframework.security.core.userdetails.User u && taiKhoanService != null) {
                vn.urbansteps.model.TaiKhoan tk = taiKhoanService.findByTaiKhoan(u.getUsername());
                if (tk != null) adminId = tk.getId();
            }
            if (adminId != null) {
                logAction(adminId, action, details);
            }
        } catch (Exception ignored) {}
    }

    // Heuristic mapper: convert raw controller.method strings → friendly Vietnamese labels
    private String toFriendlyAction(String raw) {
        if (raw == null || raw.isBlank()) return "";
        String s = raw;
        try {
            // Products (mutations only)
            if (s.contains("addProduct")) return "Thêm sản phẩm";
            if (s.contains("updateProduct")) return "Cập nhật sản phẩm";
            if (s.contains("deleteProduct")) return "Xóa sản phẩm";

            // Discounts / vouchers (mutations only)
            if (s.contains("saveDiscount") || s.contains("discount-save")) return "Lưu phiếu giảm giá";
            if (s.contains("deleteDiscount") || s.contains("discount-delete")) return "Xóa phiếu giảm giá";

            // Orders (mutations only)
            if (s.contains("updateOrderStatus")) return "Cập nhật trạng thái đơn hàng";
            if (s.contains("cancelOrder")) return "Hủy đơn hàng";
            if (s.contains("refundItem")) return "Hoàn tiền/Trả hàng một phần";
            if (s.contains("approveReturn")) return "Duyệt trả hàng";
            if (s.contains("rejectReturn")) return "Từ chối trả hàng";

            // POS
            if (s.toLowerCase().contains("pos") && !s.toLowerCase().contains("get")) return "Thao tác POS";
        } catch (Exception ignored) {}
        return s; // fallback to raw
    }

    private boolean isNonMutating(String action, String details) {
        String a = action == null ? "" : action.toLowerCase();
        String d = details == null ? "" : details.toLowerCase();
        if (d.contains("method=get")) return true;
        return a.startsWith("xem ") || a.startsWith("mở form") || a.contains("đăng nhập") || a.contains("dashboard") || a.startsWith("get ");
    }
}
