package vn.urbansteps.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.PhieuGiamGia;
import vn.urbansteps.service.PhieuGiamGiaService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminDiscountController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDiscountController.class);

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;
    // Aspect-based logging will capture create/update/delete actions

    @GetMapping("/discount-management")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDiscountManagement(Model model, @RequestParam(required = false) String filter) {
        try {
            List<PhieuGiamGia> discounts;
            if ("active".equals(filter)) {
                discounts = phieuGiamGiaService.findActiveVouchers();
                model.addAttribute("filter", "active");
            } else if ("expiring".equals(filter)) {
                discounts = phieuGiamGiaService.findExpiringSoon();
                model.addAttribute("filter", "expiring");
            } else if ("inactive".equals(filter)) {
                discounts = phieuGiamGiaService.findInactiveVouchers();
                model.addAttribute("filter", "inactive");
            } else {
                discounts = phieuGiamGiaService.getAllPhieuGiamGia();
                model.addAttribute("filter", "all");
            }
            model.addAttribute("discounts", discounts);
            int currentYear = LocalDateTime.now().getYear();
            int currentMonth = LocalDateTime.now().getMonthValue();
            model.addAttribute("monthlyStats", phieuGiamGiaService.getMonthlyStatistics(currentYear));
            // Debug: Lấy danh sách phiếu trong tháng hiện tại
            List<PhieuGiamGia> vouchersThisMonth = phieuGiamGiaService.findVouchersByMonth(currentYear, currentMonth);
            logger.info("Vouchers in year {} month {}: {}", currentYear, currentMonth, vouchersThisMonth);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách phiếu giảm giá: " + e.getMessage());
            logger.error("Error loading discount management: {}", e.getMessage());
        }
        return "admin/discount-management";
    }

    @GetMapping("/discount-add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddDiscountForm(Model model) {
        model.addAttribute("phieuGiamGia", new PhieuGiamGia());
        return "admin/discount-add";
    }

    @PostMapping("/discount-save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveDiscount(@ModelAttribute PhieuGiamGia phieuGiamGia, Model model) {
        try {
            if (phieuGiamGia.getMaPhieuGiamGia() == null || phieuGiamGia.getMaPhieuGiamGia().trim().isEmpty()) {
                phieuGiamGia.setMaPhieuGiamGia("PGG_DEFAULT_" + System.currentTimeMillis());
            }
            if (phieuGiamGia.getNgayBatDau() == null) phieuGiamGia.setNgayBatDau(LocalDateTime.now());
            if (phieuGiamGia.getNgayKetThuc() == null) phieuGiamGia.setNgayKetThuc(LocalDateTime.now().plusMonths(1));
            phieuGiamGiaService.saveWithCreateAt(phieuGiamGia);
            if (phieuGiamGia.getId() != null) {
                phieuGiamGiaService.incrementUsageCount(phieuGiamGia.getId());
            }
            // log by aspect
            return "redirect:/admin/discount-management";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lưu phiếu giảm giá: " + e.getMessage());
            return "admin/discount-add";
        }
    }

    @GetMapping("/discount-edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditDiscountForm(@PathVariable Integer id, Model model) {
        try {
            Optional<PhieuGiamGia> discount = phieuGiamGiaService.findById(id);
            if (discount.isPresent()) {
                model.addAttribute("phieuGiamGia", discount.get());
            } else {
                model.addAttribute("error", "Phiếu giảm giá không tồn tại với ID: " + id);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin phiếu giảm giá: " + e.getMessage());
        }
        return "admin/discount-add";
    }

    @GetMapping("/discount-delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteDiscount(@PathVariable Integer id, Model model) {
        try {
            Optional<PhieuGiamGia> discount = phieuGiamGiaService.findById(id);
            if (discount.isPresent()) {
                phieuGiamGiaService.deactivate(id);
                // log by aspect
                return "redirect:/admin/discount-management?success=true";
            } else {
                return "redirect:/admin/discount-management?error=true";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa phiếu giảm giá: " + e.getMessage());
            return "redirect:/admin/discount-management?error=true";
        }
    }
}