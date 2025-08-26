package vn.urbansteps.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.PhieuGiamGia;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.service.AdminActionLogService;

import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminActionLoggingAspect {

    private final AdminActionLogService adminLogService;

    @AfterReturning(pointcut = "@annotation(adminAction)", returning = "result")
    public void logAction(JoinPoint joinPoint, AdminAction adminAction, Object result) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminName = (auth != null && auth.getName() != null) ? auth.getName() : "Unknown";

        Object[] args = joinPoint.getArgs();
        String description = adminAction.description();

        Integer idValue = null;
        String productName = null;
        String discountCode = null;

        // Duyệt qua các tham số của controller
        for (Object arg : args) {
            if (arg instanceof Integer) {
                idValue = (Integer) arg;

            } else if (arg instanceof Long) {
                idValue = ((Long) arg).intValue();

            } else if (arg instanceof Model model) {
                // Lấy tất cả attribute trong Model và thay thế
                Map<String, Object> modelMap = model.asMap();
                for (Map.Entry<String, Object> entry : modelMap.entrySet()) {
                    if (entry.getValue() != null) {
                        String value = entry.getKey().equals("prevStatus") || entry.getKey().equals("newStatus")
                                ? convertStatus(entry.getValue().toString())
                                : entry.getValue().toString();

                        description = description.replace(
                                "#{" + entry.getKey() + "}",
                                value
                        );
                    }
                }

                // Lấy tên sản phẩm nếu có
                Object productObj = modelMap.get("product");
                if (productObj instanceof SanPham sp) {
                    productName = sp.getTenSanPham();
                }

                // Lấy mã phiếu giảm giá nếu có
                Object discountObj = modelMap.get("discount");
                if (discountObj instanceof PhieuGiamGia discount) {
                    discountCode = discount.getMaPhieuGiamGia();
                }

                // Lấy mã đơn hàng nếu có object "order"
                Object orderObj = modelMap.get("order");
                if (orderObj instanceof HoaDon hoaDonModel) {
                    description = description.replace("#{maHoaDon}", hoaDonModel.getMaHoaDon());
                }

            } else if (arg instanceof SanPham sp) {
                productName = sp.getTenSanPham();
                idValue = sp.getId();

            } else if (arg instanceof PhieuGiamGia discount) {
                discountCode = discount.getMaPhieuGiamGia();
                idValue = discount.getId();

            } else if (arg instanceof SanPhamChiTiet chiTiet) {
                if (chiTiet.getSanPham() != null) {
                    productName = chiTiet.getSanPham().getTenSanPham();
                    idValue = chiTiet.getId();
                }

            } else if (arg instanceof HoaDon hoaDon) {
                productName = "Đơn hàng #" + (hoaDon.getMaHoaDon() != null ? hoaDon.getMaHoaDon() : hoaDon.getId());
                idValue = hoaDon.getId();
                description = description.replace("#{maHoaDon}", hoaDon.getMaHoaDon());
            }
        }

        // Thay thế các placeholder cơ bản
        if (idValue != null) {
            description = description.replace("#{id}", String.valueOf(idValue))
                    .replace("#{orderId}", String.valueOf(idValue))
                    .replace("#{orderItemId}", String.valueOf(idValue));
        }
        if (productName != null) {
            description = description.replace("{name}", productName);
        }
        if (discountCode != null) {
            description = description.replace("{code}", discountCode);
        }

        // Ghi log
        adminLogService.logAction(
                1,
                adminAction.action(),
                "Admin " + adminName + " thực hiện hành động: " + description
        );
    }

    // Helper: Chuyển trạng thái từ mã số sang tên trạng thái
    private String convertStatus(String statusValue) {
        try {
            int status = Integer.parseInt(statusValue);
            return switch (status) {
                case 0 -> "Chờ xác nhận";
                case 1 -> "Đã xác nhận";
                case 2 -> "Đang giao hàng";
                case 3 -> "Hoàn thành";
                case 4 -> "Đã hủy";
                case 5 -> "Đã Thanh Toán";
                case 6 -> "Trả hàng";
                case 7 -> "Xử lý trả hàng";
                case 8 -> "Trả hàng thất bại";
                default -> "Không xác định";
            };
        } catch (NumberFormatException e) {
            return statusValue;
        }
    }
}
