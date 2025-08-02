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

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/order-management")
    public String orderManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
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
            
            redirectAttributes.addFlashAttribute("success", "Hủy đơn hàng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/order-detail/" + id;
    }
    
    private boolean isValidStatusTransition(Byte currentStatus, Byte newStatus) {
        if (currentStatus == null || newStatus == null) return false;
        
        // Logic chuyển trạng thái hợp lệ
        switch (currentStatus.intValue()) {
            case 0: // Chờ xử lý -> có thể chuyển thành xác nhận hoặc hủy
                return newStatus == 1 || newStatus == 4;
            case 1: // Đã xác nhận -> có thể chuyển thành đang giao hoặc hủy
                return newStatus == 2 || newStatus == 4;
            case 2: // Đang giao -> có thể chuyển thành hoàn thành
                return newStatus == 3;
            case 3: // Hoàn thành -> không thể chuyển
            case 4: // Đã hủy -> không thể chuyển
            case 5: // Đã thanh toán -> không thể chuyển
                return false;
            default:
                return false;
        }
    }
}
