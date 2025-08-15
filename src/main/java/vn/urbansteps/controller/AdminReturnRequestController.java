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
import vn.urbansteps.model.ReturnRequest;
import vn.urbansteps.model.HoaDonChiTiet;
import vn.urbansteps.repository.ReturnRequestRepository;
import vn.urbansteps.service.ReturnRequestService;
import vn.urbansteps.service.EmailService;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.AdminActionLogService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminReturnRequestController {

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Autowired
    private ReturnRequestService returnRequestService;

    @Autowired(required = false)
    private EmailService emailService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired(required = false)
    private AdminActionLogService adminActionLogService;

    // Quản lý yêu cầu trả hàng
    @GetMapping({"/return-requests", "/quan-ly-yeu-cau-tra-hang"})
    public String returnRequestManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String status,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReturnRequest> requests;
        
        // Tìm kiếm và lọc theo trạng thái
        if (!search.isEmpty() || status != null) {
            if (status != null && !status.isEmpty()) {
                ReturnRequest.Status statusEnum = ReturnRequest.Status.valueOf(status);
                if (!search.isEmpty()) {
                    requests = returnRequestRepository.findByOrderCodeContainingIgnoreCaseAndStatus(search, statusEnum, pageable);
                } else {
                    requests = returnRequestRepository.findByStatus(statusEnum, pageable);
                }
            } else {
                requests = returnRequestRepository.findByOrderCodeContainingIgnoreCaseOrCustomerNameContainingIgnoreCaseOrCustomerPhoneContaining(
                        search, search, search, pageable);
            }
        } else {
            requests = returnRequestRepository.findAll(pageable);
        }
        
        // Thống kê số lượng theo trạng thái
        long newCount = returnRequestRepository.countByStatus(ReturnRequest.Status.NEW);
        long approvedCount = returnRequestRepository.countByStatus(ReturnRequest.Status.APPROVED);
        long rejectedCount = returnRequestRepository.countByStatus(ReturnRequest.Status.REJECTED);
        long processingCount = returnRequestRepository.countByStatus(ReturnRequest.Status.PROCESSING);
        
        model.addAttribute("requests", requests);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requests.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("newCount", newCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        model.addAttribute("processingCount", processingCount);
        model.addAttribute("title", "Quản lý yêu cầu trả hàng");
        
        return "admin/return-request-management";
    }

    // Chi tiết yêu cầu trả hàng
    @GetMapping({"/return-request/{id}", "/yeu-cau-tra-hang/{id}"})
    public String returnRequestDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            // Load with items via @EntityGraph to avoid LazyInitializationException when iterating in view
            ReturnRequest request = returnRequestRepository.findByIdWithItems(id).orElse(null);
            if (request == null) {
                ra.addFlashAttribute("error", "Không tìm thấy yêu cầu trả hàng!");
                return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
            }
            
            // Lấy thông tin đơn hàng kèm chi tiết để dùng trong view
            var order = hoaDonService.getOrderByIdWithDetails(request.getOrderId());
            
            model.addAttribute("request", request);
            model.addAttribute("order", order);
            model.addAttribute("title", "Chi tiết yêu cầu trả hàng #" + request.getId());

        // Tính tổng tạm hoàn và map đơn giá/tạm hoàn theo id dòng (tránh Thymeleaf expression phức tạp)
            try {
                BigDecimal totalRefund = BigDecimal.ZERO;
        java.util.Map<Integer, BigDecimal> unitPriceById = new java.util.HashMap<>();
        java.util.Map<Integer, BigDecimal> refundById = new java.util.HashMap<>();
                if (order != null && order.getHoaDonChiTietList() != null && request.getItems() != null) {
                    Map<Integer, HoaDonChiTiet> byId = order.getHoaDonChiTietList()
                            .stream()
                            .collect(Collectors.toMap(HoaDonChiTiet::getId, Function.identity(), (a,b)->a));
                    for (var it : request.getItems()) {
                        if (it == null || it.getOrderItemId() == null || it.getQty() == null) continue;
                        HoaDonChiTiet d = byId.get(it.getOrderItemId());
                        if (d == null) continue;
                        // Đơn giá là giá bán từng đơn vị
                        BigDecimal unitPrice = d.getGiaBan() != null ? d.getGiaBan() : BigDecimal.ZERO;
            unitPriceById.put(d.getId(), unitPrice);
            BigDecimal lineRefund = unitPrice.multiply(BigDecimal.valueOf(it.getQty().longValue()));
            refundById.put(d.getId(), lineRefund);
                        totalRefund = totalRefund.add(lineRefund);
                    }
                }
                model.addAttribute("totalRefund", totalRefund);
        model.addAttribute("unitPriceById", unitPriceById);
        model.addAttribute("refundById", refundById);
            } catch (Exception ignored) {
                model.addAttribute("totalRefund", BigDecimal.ZERO);
            }
            
            return "admin/return-request-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
        }
    }

    // Phê duyệt yêu cầu trả hàng
    @PostMapping({"/return-request/{id}/approve", "/yeu-cau-tra-hang/{id}/approve"})
    public String approveReturnRequest(@PathVariable Long id, 
                                     @RequestParam(name = "adminNote", required = false) String adminNote,
                                     RedirectAttributes ra) {
        try {
            // Load with items for safe iteration
            ReturnRequest request = returnRequestRepository.findByIdWithItems(id).orElse(null);
            if (request == null) {
                ra.addFlashAttribute("error", "Không tìm thấy yêu cầu trả hàng!");
                return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
            }

            // Cập nhật trạng thái yêu cầu
            returnRequestService.approve(id);
            // Hoàn trả các dòng đã chọn theo số lượng trong yêu cầu
            var order = hoaDonService.getOrderById(request.getOrderId());
            if (order != null) {
                if (request.getItems() != null) {
                    for (var it : request.getItems()) {
                        try {
                            hoaDonService.refundOrderItem(order.getId(), it.getOrderItemId(), it.getQty(),
                                    adminNote != null ? adminNote : ("Trả hàng theo yêu cầu #" + id));
                        } catch (Exception ex) {
                            // Ghi log và tiếp tục các item khác
                            System.err.println("Refund item failed: " + ex.getMessage());
                        }
                    }
                }
                // Đặt trạng thái đơn sang Trả hàng sau khi phê duyệt
                order.setTrangThai((byte) vn.urbansteps.model.HoaDon.TrangThaiHoaDon.TRA_HANG.getValue());
                hoaDonService.save(order);
            }
            
            // Thêm ghi chú nếu có
            if (adminNote != null && !adminNote.trim().isEmpty()) {
                request.setAdminNote(adminNote);
                returnRequestRepository.save(request);
            }

            // Gửi email thông báo
            if (emailService != null && request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
                String subject = "Yêu cầu trả hàng được phê duyệt - " + request.getOrderCode();
                String content = String.format(
                    "Chào %s,\n\n" +
                    "Yêu cầu trả hàng cho đơn hàng %s của bạn đã được phê duyệt.\n" +
                    "Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất để thu hồi sản phẩm.\n\n" +
                    "%s\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ UrbanSteps",
                    request.getCustomerName(),
                    request.getOrderCode(),
                    adminNote != null ? "Ghi chú: " + adminNote : ""
                );
                
                try {
                    emailService.sendOrderConfirmation(request.getCustomerEmail(), subject, content);
                } catch (Exception e) {
                    // Log error but don't fail the approval
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            }

            // Lưu lịch sử admin
            if (adminActionLogService != null) {
                adminActionLogService.logActionCurrent("approveReturn", "Duyệt yêu cầu trả hàng #" + id + ", order=" + (request != null ? request.getOrderCode() : ""));
            }

            ra.addFlashAttribute("success", "Đã phê duyệt yêu cầu trả hàng thành công!");
            // Redirect to Vietnamese-friendly detail URL
            return "redirect:/admin/yeu-cau-tra-hang/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            // Redirect to Vietnamese-friendly list URL
            return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
        }
    }

    // Từ chối yêu cầu trả hàng
    @PostMapping({"/return-request/{id}/reject", "/yeu-cau-tra-hang/{id}/reject"})
    public String rejectReturnRequest(@PathVariable Long id, 
                                    @RequestParam String reason,
                                    RedirectAttributes ra) {
        try {
            ReturnRequest request = returnRequestRepository.findByIdWithItems(id).orElse(null);
            if (request == null) {
                ra.addFlashAttribute("error", "Không tìm thấy yêu cầu trả hàng!");
                return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
            }

            if (reason == null || reason.trim().isEmpty()) {
                ra.addFlashAttribute("error", "Vui lòng nhập lý do từ chối!");
                // put form value back if needed
                ra.addFlashAttribute("reason", reason);
                return "redirect:/admin/yeu-cau-tra-hang/" + id;
            }
            // Log admin action
            if (adminActionLogService != null) {
                adminActionLogService.logActionCurrent("rejectReturn", "Từ chối yêu cầu trả hàng #" + id + ", reason=" + reason);
            }
            returnRequestService.reject(id, reason);
            // Nếu từ chối, cập nhật ghi chú và set trạng thái đơn sang Trả hàng thất bại (8)
            try {
                var order = hoaDonService.getOrderById(request.getOrderId());
                if (order != null) {
                    if (noteNotBlank(reason)) {
                        String existing = order.getGhiChu();
                        String append = "Từ chối yêu cầu trả hàng #" + id + ": " + reason;
                        order.setGhiChu((existing == null || existing.isBlank()) ? append : (existing + " | " + append));
                    }
                    order.setTrangThai((byte) vn.urbansteps.model.HoaDon.TrangThaiHoaDon.TRA_HANG_THAT_BAI.getValue());
                    hoaDonService.save(order);
                }
            } catch (Exception ignored) {}

            // Gửi email thông báo
            if (emailService != null && request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
                String subject = "Yêu cầu trả hàng bị từ chối - " + request.getOrderCode();
                String content = String.format(
                    "Chào %s,\n\n" +
                    "Rất tiếc, yêu cầu trả hàng cho đơn hàng %s của bạn đã bị từ chối.\n\n" +
                    "Lý do: %s\n\n" +
                    "Nếu bạn có thắc mắc, vui lòng liên hệ với chúng tôi qua hotline: 1900-123-456\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ UrbanSteps",
                    request.getCustomerName(),
                    request.getOrderCode(),
                    reason
                );
                
                try {
                    emailService.sendOrderConfirmation(request.getCustomerEmail(), subject, content);
                } catch (Exception e) {
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            }

            // Lưu lịch sử admin
            if (adminActionLogService != null) {
                adminActionLogService.logActionCurrent("rejectReturn", "Từ chối trả hàng #" + id + ", đơn: " + (request != null ? request.getOrderCode() : "") + "; lý do: " + reason);
            }

            ra.addFlashAttribute("success", "Đã từ chối yêu cầu trả hàng!");
            return "redirect:/admin/yeu-cau-tra-hang/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
        }
    }

    // Đánh dấu đang xử lý
    @PostMapping({"/return-request/{id}/processing", "/yeu-cau-tra-hang/{id}/processing"})
    public String markAsProcessing(@PathVariable Long id, RedirectAttributes ra) {
        try {
            ReturnRequest request = returnRequestRepository.findByIdWithItems(id).orElse(null);
            if (request == null) {
                ra.addFlashAttribute("error", "Không tìm thấy yêu cầu trả hàng!");
                return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
            }

            request.setStatus(ReturnRequest.Status.PROCESSING);
            request.setUpdatedAt(java.time.LocalDateTime.now());
            returnRequestRepository.save(request);

            // Đồng bộ trạng thái đơn hàng sang Xử lý trả hàng
            try {
                var order = hoaDonService.getOrderById(request.getOrderId());
                if (order != null) {
                    order.setTrangThai((byte) vn.urbansteps.model.HoaDon.TrangThaiHoaDon.XU_LY_TRA_HANG.getValue());
                    hoaDonService.save(order);
                }
            } catch (Exception ignored) {}

            // Ghi lịch sử admin
            if (adminActionLogService != null) {
                adminActionLogService.logActionCurrent("processingReturn", "Đánh dấu đang xử lý yêu cầu trả hàng #" + id);
            }

            ra.addFlashAttribute("success", "Đã đánh dấu đang xử lý!");
            return "redirect:/admin/yeu-cau-tra-hang/" + id;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/admin/quan-ly-yeu-cau-tra-hang";
        }
    }

    private boolean noteNotBlank(String s){ return s != null && !s.trim().isEmpty(); }
}
