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
import vn.urbansteps.aspect.AdminAction;
import vn.urbansteps.repository.SanPhamRepository;
import vn.urbansteps.service.ImageService;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.repository.ReturnRequestRepository;
import vn.urbansteps.service.EmailService;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {
    
    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired(required = false)
    private EmailService emailService;
    // Aspect-based logging will capture mutations; no direct logging here

    @GetMapping("/order-management")
    public String orderManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Byte status,
            Model model,
            ReturnRequestRepository returnRequestRepository) {
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
        // Map orderId -> count NEW return requests for inline badge
        java.util.Map<Integer, Long> returnCounts = new java.util.HashMap<>();
        for (HoaDon hd : orders.getContent()) {
            long c = 0L;
            try { c = returnRequestRepository.countByOrderIdAndStatus(hd.getId(), vn.urbansteps.model.ReturnRequest.Status.NEW); } catch (Exception ignore) {}
            returnCounts.put(hd.getId(), c);
        }
        model.addAttribute("returnCounts", returnCounts);

        return "admin/order-management";
    }

    // Form-friendly endpoint: update status then redirect back (like big ecom sites)
    @AdminAction(action = "UPDATE", description = "Cập nhật trạng thái đơn hàng #{orderId}, Mã hóa đơn #{maHoaDon}, từ trạng thái #{prevStatus} sang #{newStatus}")
    @PostMapping("/order/update-status-form")
    public String updateOrderStatusForm(@RequestParam Integer orderId,
                                        @RequestParam Byte newStatus,
                                        @RequestParam(name = "redirect", required = false, defaultValue = "/admin/order-management") String redirect,
                                        RedirectAttributes ra,
                                        Model model) {
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
            byte prevStatus = hoaDon.getTrangThai();
            hoaDon.setTrangThai(newStatus);
            hoaDonService.save(hoaDon);
            if (prevStatus == 0 && newStatus == 1) {
                hoaDonService.xacNhanHoaDon(orderId);
            }
            // Nếu chuyển sang Hoàn thành (3) từ trạng thái khác, cộng lượt bán cho từng dòng
            if (prevStatus != 3 && newStatus == 3) {
                try {
                    var saved = hoaDonService.getOrderByIdWithDetails(hoaDon.getId());
            if (saved != null && saved.getHoaDonChiTietList() != null) {
                        for (var ct : saved.getHoaDonChiTietList()) {
                            var sp = ct.getSanPhamChiTiet() != null ? ct.getSanPhamChiTiet().getSanPham() : null;
                            Integer spId = (sp != null ? sp.getId() : null);
                            Integer qty = (ct.getSoLuong() == null ? 0 : ct.getSoLuong());
                if (spId != null && qty > 0) sanPhamRepository.incrementLuotBan(spId, qty);
                        }
                    }
                } catch (Exception ignore) {}
            }
            String maHoaDon = hoaDon.getMaHoaDon();
            model.addAttribute("maHoaDon", maHoaDon);
            model.addAttribute("prevStatus", prevStatus);
            model.addAttribute("newStatus", newStatus);
            // log by aspect
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
        // Chuẩn hóa ảnh sản phẩm trong chi tiết đơn để tránh lỗi hiển thị
        try {
            if (hoaDon.getHoaDonChiTietList() != null) {
                for (var ct : hoaDon.getHoaDonChiTietList()) {
                    var sp = ct.getSanPhamChiTiet() != null ? ct.getSanPhamChiTiet().getSanPham() : null;
                    if (sp != null && sp.getIdHinhAnhDaiDien() != null && sp.getIdHinhAnhDaiDien().getDuongDan() != null) {
                        String normalized = imageService.normalizeImagePath(sp.getIdHinhAnhDaiDien().getDuongDan());
                        sp.getIdHinhAnhDaiDien().setDuongDan(normalized);
                    }
                }
            }
        } catch (Exception ignore) {}
        
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
            
            byte prevStatus = hoaDon.getTrangThai();
            hoaDon.setTrangThai(newStatus);
            hoaDonService.save(hoaDon);
            if (prevStatus != 3 && newStatus == 3) {
                try {
                    var saved = hoaDonService.getOrderByIdWithDetails(hoaDon.getId());
            if (saved != null && saved.getHoaDonChiTietList() != null) {
                        for (var ct : saved.getHoaDonChiTietList()) {
                            var sp = ct.getSanPhamChiTiet() != null ? ct.getSanPhamChiTiet().getSanPham() : null;
                            Integer spId = (sp != null ? sp.getId() : null);
                            Integer qty = (ct.getSoLuong() == null ? 0 : ct.getSoLuong());
                if (spId != null && qty > 0) sanPhamRepository.incrementLuotBan(spId, qty);
                        }
                    }
                } catch (Exception ignore) {}
            }
            // log by aspect
            try { if (emailService != null) emailService.sendOrderStatusUpdateEmail(hoaDon); } catch (Exception ignore) {}
            
            return "success:Cập nhật trạng thái thành công";
        } catch (Exception e) {
            return "error:Có lỗi xảy ra: " + e.getMessage();
        }
    }
    @AdminAction(action = "DELETE", description = "Hủy đơn hàng #{id}, mã hóa đơn #{maHoaDon}")
    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            HoaDon hoaDon = hoaDonService.getOrderById(id);
            if (hoaDon == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng");
                return "redirect:/admin/order-management";
            }

            // Thêm thông tin vào model để Aspect sử dụng
            model.addAttribute("order", hoaDon);
            model.addAttribute("prevStatus", hoaDon.getTrangThai());
            model.addAttribute("newStatus", 4); // Trạng thái hủy

            if (hoaDon.getTrangThai() == 4) {
                redirectAttributes.addFlashAttribute("error", "Đơn hàng đã được hủy trước đó");
                return "redirect:/admin/order-detail/" + id;
            }
            if (hoaDon.getTrangThai() >= 1) {
                hoaDonService.huyHoaDon(id);
            }

            hoaDon.setTrangThai((byte) 4); // 4: Đã hủy
            hoaDonService.save(hoaDon);

            try {
                if (emailService != null)
                    emailService.sendOrderStatusUpdateEmail(hoaDon);
            } catch (Exception ignore) {}

            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/admin/order-detail/" + id;
    }


    @AdminAction(action = "UPDATE", description = "Hoàn trả sản phẩm #{orderItemId} trong đơn hàng #{orderId}")
    @PostMapping("/order/refund-item")
    public String refundItem(@RequestParam Integer orderId,
                             @RequestParam Integer orderItemId,
                             @RequestParam Integer quantity,
                             @RequestParam(required = false) String note,
                             @RequestParam(name = "redirect", required = false, defaultValue = "/admin/order-detail/{orderId}") String redirect,
                             RedirectAttributes ra) {
        try {
            hoaDonService.refundOrderItem(orderId, orderItemId, quantity, note);
            // log by aspect
            ra.addFlashAttribute("success", "Hoàn trả thành công");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/order-detail/" + orderId;
    }

    // Optional: mark entire order as returned (all items), if business supports it
    @PostMapping("/order/return-all")
    public String returnAll(@RequestParam Integer orderId,
                            @RequestParam(required = false) String note,
                            RedirectAttributes ra) {
        try {
            hoaDonService.returnAllItems(orderId, note);
            ra.addFlashAttribute("success", "Đã đánh dấu trả hàng toàn bộ");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/order-detail/" + orderId;
    }
    
    private boolean isValidStatusTransition(Byte currentStatus, Byte newStatus) {
        if (currentStatus == null || newStatus == null) return false;
        
        // Nới lỏng cho demo: cho phép đặt thẳng các trạng thái chính hợp lý
        int cur = currentStatus.intValue();
        int nxt = newStatus.intValue();
    if (cur == 4 || cur == 3) { // Đã hủy hoặc Hoàn thành thì khóa
            return false;
        }
    // Cho phép các trạng thái trả hàng: 7 (xử lý trả hàng), 6 (trả hàng), 8 (trả hàng thất bại)
    if (nxt == 7 || nxt == 6 || nxt == 8) return true;
    // Chỉ cho phép: 0->1/2/4; 1->2/4/3; 2->3; 3/4 khóa
    if (cur == 0) return nxt == 1 || nxt == 2 || nxt == 4;
    if (cur == 1) return nxt == 2 || nxt == 3 || nxt == 4;
    if (cur == 2) return nxt == 3; return false;
    }
}
