package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.EmailService;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired(required = false)
    private EmailService emailService;

    @GetMapping("/order-management")
    public String orderManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Byte status,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<HoaDon> orders;
        
        // Tìm kiếm và lọc theo trạng thái
        if (!search.isEmpty() || status != null) {
            orders = hoaDonService.searchOrders(search, status, pageable);
        } else {
            orders = hoaDonService.getAllOrders(pageable);
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("title", "Quản lý đơn hàng");
        
        return "admin/order-management";
    }

    // Form-friendly endpoint: update status then redirect back (like big ecom sites)
    @PostMapping("/order/update-status-form")
    public String updateOrderStatusForm(@RequestParam Integer orderId,
                                        @RequestParam Byte newStatus,
                                        @RequestParam(name = "redirect", required = false, defaultValue = "/admin/order-management") String redirect,
                                        RedirectAttributes ra) {
        try {
            HoaDon hoaDon = hoaDonService.getOrderById(orderId);
            if (hoaDon == null) {
                ra.addFlashAttribute("error", "Không tìm thấy đơn hàng");
                return "redirect:" + redirect;
            }
            if (!isValidStatusTransition(hoaDon.getTrangThai(), newStatus)) {
                ra.addFlashAttribute("error", "Không thể chuyển trạng thái này");
                return "redirect:" + redirect;
            }
            hoaDon.setTrangThai(newStatus);
            hoaDonService.save(hoaDon);
            // Email notify on any status change
            try { if (emailService != null) emailService.sendOrderStatusUpdateEmail(hoaDon); } catch (Exception ignore) {}
            ra.addFlashAttribute("success", "Cập nhật trạng thái thành công. Đã gửi email thông báo (hoặc mô phỏng nếu chưa cấu hình SMTP).");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:" + redirect;
    }
    
    @GetMapping("/order-detail/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {
        HoaDon hoaDon = hoaDonService.getOrderById(id);
        if (hoaDon == null) {
            return "redirect:/admin/order-management";
        }
        
        model.addAttribute("order", hoaDon);
        model.addAttribute("title", "Chi tiết đơn hàng #" + hoaDon.getMaHoaDon());
        
        return "admin/order-detail";
    }
    
    @PostMapping("/order/update-status")
    @ResponseBody
    public String updateOrderStatus(@RequestParam Integer orderId, @RequestParam Byte newStatus) {
        try {
            HoaDon hoaDon = hoaDonService.getOrderById(orderId);
            if (hoaDon == null) {
                return "error:Không tìm thấy đơn hàng";
            }
            
            // Kiểm tra logic chuyển trạng thái
            if (!isValidStatusTransition(hoaDon.getTrangThai(), newStatus)) {
                return "error:Không thể chuyển trạng thái này";
            }
            
            hoaDon.setTrangThai(newStatus);
            hoaDonService.save(hoaDon);
            try { if (emailService != null) emailService.sendOrderStatusUpdateEmail(hoaDon); } catch (Exception ignore) {}
            
            return "success:Cập nhật trạng thái thành công";
        } catch (Exception e) {
            return "error:Có lỗi xảy ra: " + e.getMessage();
        }
    }
    
    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            HoaDon hoaDon = hoaDonService.getOrderById(id);
            if (hoaDon == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng");
                return "redirect:/admin/order-management";
            }
            
            if (!hoaDon.canCancel()) {
                redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng này");
                return "redirect:/admin/order-detail/" + id;
            }
            
            hoaDon.setTrangThai((byte) 4); // 4: Đã hủy
            hoaDonService.save(hoaDon);
            try { if (emailService != null) emailService.sendOrderStatusUpdateEmail(hoaDon); } catch (Exception ignore) {}
            
            redirectAttributes.addFlashAttribute("success", "Hủy đơn hàng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/order-detail/" + id;
    }
    
    private boolean isValidStatusTransition(Byte currentStatus, Byte newStatus) {
        if (currentStatus == null || newStatus == null) return false;
        
        // Nới lỏng cho demo: cho phép đặt thẳng các trạng thái chính hợp lý
        int cur = currentStatus.intValue();
        int nxt = newStatus.intValue();
        if (cur == 4 || cur == 3) { // Đã hủy hoặc Hoàn thành thì khóa
            return false;
        }
        // Cho phép: Pending(0) -> Confirmed(1), Shipping(2), Completed(3), Cancelled(4), Paid(5)
        // Confirmed(1) -> Shipping(2), Completed(3), Cancelled(4), Paid(5)
        // Shipping(2) -> Completed(3), Paid(5)
        if (cur == 0) return nxt == 1 || nxt == 2 || nxt == 3 || nxt == 4 || nxt == 5;
        if (cur == 1) return nxt == 2 || nxt == 3 || nxt == 4 || nxt == 5;
        if (cur == 2) return nxt == 3 || nxt == 5;
        // Paid(5) treated as final like completed
        if (cur == 5) return false;
        return false;
    }
}
