package vn.urbansteps.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.AdminActionLogService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.userdetails.User;
import vn.urbansteps.service.TaiKhoanService;

@Aspect
@Component
public class AdminActionLoggingAspect {

    @Autowired
    private AdminActionLogService adminActionLogService;
    @Autowired
    private TaiKhoanService taiKhoanService;

    @Pointcut("within(vn.urbansteps.controller.admin..*) || execution(* vn.urbansteps.controller.AdminOrderController.*(..))")
    public void adminControllers() {}

    @AfterReturning("adminControllers()")
    public void logAdminAction(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return;

        TaiKhoan tk = null;
        if (auth.getPrincipal() instanceof TaiKhoan principalTk) {
            tk = principalTk;
        } else if (auth.getPrincipal() instanceof User springUser) {
            tk = taiKhoanService.findByTaiKhoan(springUser.getUsername());
        }
        if (tk == null) return;
        if (tk.getRole() == null || !tk.getRole().contains("ADMIN")) return;
        String rawAction = joinPoint.getSignature().toShortString();
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String path = "";
        String ip = "";
        String method = "";
        String query = "";
        if (attrs != null && attrs.getRequest() != null) {
            try {
                path = attrs.getRequest().getRequestURI();
                String xff = attrs.getRequest().getHeader("X-Forwarded-For");
                ip = (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : attrs.getRequest().getRemoteAddr();
                method = attrs.getRequest().getMethod();
                query = attrs.getRequest().getQueryString();
            } catch (Exception ignored) {}
        }
    boolean isMutating = method != null && (
        method.equalsIgnoreCase("POST") ||
        method.equalsIgnoreCase("PUT") ||
        method.equalsIgnoreCase("PATCH") ||
        method.equalsIgnoreCase("DELETE")
    );
    if (!isMutating) {
        String p = path == null ? "" : path;
        boolean allowGet = p.contains("/delete/") || p.contains("/discount-delete/") || p.contains("/deactivate/") || p.contains("/toggle/");
        if (!allowGet) return; 
    }

    String friendly = toFriendlyAction(rawAction, path, method);
        // Build a human friendly description (moTa) to show in admin UI. Keep it concise and readable in Vietnamese.
        String pathWithQuery = (path == null ? "" : path) + (query != null && !query.isBlank() ? ("?" + query) : "");
        String moTa = String.format("%s • %s %s • user:%s • ip:%s",
                friendly,
                (method == null ? "" : method),
                (pathWithQuery == null ? "" : pathWithQuery),
                tk.getTaiKhoan(),
                ip == null ? "" : ip
        );
        adminActionLogService.logAction(tk.getId(), friendly, moTa);
    }

    private String toFriendlyAction(String raw, String path, String method){
        try {
            String m = method == null ? "" : method.toUpperCase();
            String p = path == null ? "" : path;

            // Products
            if (p.startsWith("/admin/products")) {
                if (m.equals("GET")) {
                    if (p.matches("^/admin/products(/\\d+)?$")) return "Xem danh sách/chi tiết sản phẩm";
                    if (p.contains("/edit/")) return "Mở form sửa sản phẩm";
                    if (p.contains("/add")) return "Mở form thêm sản phẩm";
                    if (p.contains("/delete/")) return "Xóa sản phẩm"; // some deletes via GET link
                }
                if (m.equals("POST")) {
                    if (p.equals("/admin/products")) return "Thêm sản phẩm";
                    if (p.matches("^/admin/products/\\d+$")) return "Cập nhật sản phẩm";
                }
                return "Quản lý sản phẩm";
            }

            // Orders
            if (p.startsWith("/admin/order")) {
                if (p.contains("/refund")) return "Hoàn tiền một phần đơn hàng";
                if (p.contains("/return-all")) return "Trả hàng toàn bộ đơn";
                if (p.contains("/cancel")) return "Hủy đơn hàng";
                if (p.contains("/update-status")) return "Cập nhật trạng thái đơn hàng";
                return "Quản lý đơn hàng";
            }

            // Discounts
            if (p.startsWith("/admin/discount")) {
                if (m.equals("POST")) return "Lưu phiếu giảm giá";
                if (p.contains("/delete/")) return "Xóa phiếu giảm giá";
                return "Quản lý phiếu giảm giá";
            }

            // POS
            if (p.startsWith("/admin/pos")) return "Mở POS";

            if (raw != null) {
                if (raw.contains("addProduct")) return "Thêm sản phẩm";
                if (raw.contains("updateProduct")) return "Cập nhật sản phẩm";
                if (raw.contains("deleteProduct")) return "Xóa sản phẩm";
                if (raw.contains("saveDiscount")) return "Lưu phiếu giảm giá";
                if (raw.contains("deleteDiscount")) return "Xóa phiếu giảm giá";
                if (raw.contains("updateOrderStatus")) return "Cập nhật trạng thái đơn hàng";
                if (raw.contains("orderManagement")) return "Xem quản lý đơn hàng";
                if (raw.contains("refundItem")) return "Hoàn tiền một phần đơn hàng";
                if (raw.contains("getProductList")) return "Xem danh sách sản phẩm";
                if (raw.contains("pos")) return "Mở POS";
            }
            return (m.isEmpty()?"":m+" ") + p;
        } catch (Exception e){
            return (method==null?"":method+" ") + (path==null?"":path);
        }
    }
}
