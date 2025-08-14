package vn.urbansteps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.KhachHang;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.repository.TaiKhoanRepository;
import vn.urbansteps.service.HoaDonService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/don-hang")
public class OrderCancelController {
    
    private final HoaDonService hoaDonService;
    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    /**
     * Cancel order for logged-in users
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Integer id, 
                            Authentication auth,
                            RedirectAttributes redirectAttributes) {
        try {
            if (auth == null || !auth.isAuthenticated()) {
                redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để thực hiện chức năng này.");
                return "redirect:/dang-nhap";
            }

            // Get the order
            HoaDon order = hoaDonService.getOrderById(id);
            if (order == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
                return "redirect:/don-hang";
            }

            // Check ownership
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTaiKhoan(auth.getName());
            if (!taiKhoanOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
                return "redirect:/dang-nhap";
            }
            
            Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoanOpt.get());
            if (!khachHangOpt.isPresent() || !order.getKhachHang().getId().equals(khachHangOpt.get().getId())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền hủy đơn hàng này.");
                return "redirect:/don-hang";
            }

            // Check if order can be cancelled (only pending orders - status 0)
            if (order.getTrangThai() != HoaDon.TrangThaiHoaDon.CHO_XU_LY.getValue()) {
                redirectAttributes.addFlashAttribute("error", "Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xử lý'.");
                return "redirect:/don-hang/" + id;
            }

            // Cancel the order
            order.setTrangThai((byte) HoaDon.TrangThaiHoaDon.DA_HUY.getValue());
            hoaDonService.save(order);
            
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được hủy thành công.");
            return "redirect:/tai-khoan?tab=orders";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi hủy đơn hàng: " + e.getMessage());
            return "redirect:/tai-khoan?tab=orders";
        }
    }

    /**
     * Cancel order for guests using order code and phone
     */
    @PostMapping("/guest/{orderCode}/cancel")
    public String cancelGuestOrder(@PathVariable String orderCode,
                                 @RequestParam String soDienThoai,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Find order by code and phone
            Optional<HoaDon> orderOpt = hoaDonService.findByMaHoaDonAndSdt(orderCode, soDienThoai);
            if (!orderOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng với mã và số điện thoại này.");
                return "redirect:/don-hang/guest/" + orderCode + "?soDienThoai=" + soDienThoai;
            }
            
            HoaDon order = orderOpt.get();

            // Check if order can be cancelled (only pending orders - status 0)
            if (order.getTrangThai() != HoaDon.TrangThaiHoaDon.CHO_XU_LY.getValue()) {
                redirectAttributes.addFlashAttribute("error", "Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xử lý'.");
                return "redirect:/don-hang/guest/" + orderCode + "?soDienThoai=" + soDienThoai;
            }

            // Cancel the order
            order.setTrangThai((byte) HoaDon.TrangThaiHoaDon.DA_HUY.getValue());
            hoaDonService.save(order);
            
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được hủy thành công.");
            return "redirect:/don-hang/guest/" + orderCode + "?soDienThoai=" + soDienThoai;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi hủy đơn hàng: " + e.getMessage());
            return "redirect:/don-hang/guest/" + orderCode + "?soDienThoai=" + soDienThoai;
        }
    }

    /**
     * AJAX endpoint to check if order can be cancelled
     */
    @GetMapping("/{id}/can-cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> canCancelOrder(@PathVariable Integer id,
                                                            Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            HoaDon order = hoaDonService.getOrderById(id);
            if (order == null) {
                response.put("canCancel", false);
                response.put("message", "Không tìm thấy đơn hàng.");
                return ResponseEntity.ok(response);
            }

            // Check if user is authenticated and owns the order
            if (auth != null && auth.isAuthenticated()) {
                Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTaiKhoan(auth.getName());
                if (!taiKhoanOpt.isPresent()) {
                    response.put("canCancel", false);
                    response.put("message", "Không tìm thấy tài khoản.");
                    return ResponseEntity.ok(response);
                }
                
                Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoanOpt.get());
                if (!khachHangOpt.isPresent() || !order.getKhachHang().getId().equals(khachHangOpt.get().getId())) {
                    response.put("canCancel", false);
                    response.put("message", "Bạn không có quyền hủy đơn hàng này.");
                    return ResponseEntity.ok(response);
                }
            }

            // Check if order status allows cancellation
            boolean canCancel = order.getTrangThai() == HoaDon.TrangThaiHoaDon.CHO_XU_LY.getValue();
            response.put("canCancel", canCancel);
            
            if (!canCancel) {
                response.put("message", "Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xử lý'.");
            } else {
                response.put("message", "Đơn hàng có thể được hủy.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("canCancel", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * AJAX endpoint for guest orders to check cancellation eligibility
     */
    @GetMapping("/guest/{orderCode}/can-cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> canCancelGuestOrder(@PathVariable String orderCode,
                                                                 @RequestParam String soDienThoai) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<HoaDon> orderOpt = hoaDonService.findByMaHoaDonAndSdt(orderCode, soDienThoai);
            if (!orderOpt.isPresent()) {
                response.put("canCancel", false);
                response.put("message", "Không tìm thấy đơn hàng.");
                return ResponseEntity.ok(response);
            }
            
            HoaDon order = orderOpt.get();

            boolean canCancel = order.getTrangThai() == HoaDon.TrangThaiHoaDon.CHO_XU_LY.getValue();
            response.put("canCancel", canCancel);
            
            if (!canCancel) {
                response.put("message", "Chỉ có thể hủy đơn hàng ở trạng thái 'Chờ xử lý'.");
            } else {
                response.put("message", "Đơn hàng có thể được hủy.");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("canCancel", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
