package vn.urbansteps.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.HoaDonChiTiet;
import vn.urbansteps.service.InvoiceService;
import vn.urbansteps.service.EmailService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminInvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(AdminInvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @Autowired(required = false)
    private EmailService emailService;

    @GetMapping("/invoice-management")
    @PreAuthorize("hasRole('ADMIN')")
    public String showInvoiceManagement(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
                                        Model model) {
        try {
            List<HoaDon> invoices;
            switch (filter) {
                case "pending":
                    invoices = invoiceService.getAllInvoices().stream()
                            .filter(h -> h.getTrangThai() != null && (h.getTrangThai() == 0 || h.getTrangThai() == 1))
                            .toList();
                    break;
                case "completed":
                    invoices = invoiceService.getAllInvoices().stream()
                            .filter(h -> h.getTrangThai() != null && (h.getTrangThai() == 3 || h.getTrangThai() == 5))
                            .toList();
                    break;
                case "cancelled":
                    invoices = invoiceService.getAllInvoices().stream()
                            .filter(h -> h.getTrangThai() != null && h.getTrangThai() == 4)
                            .toList();
                    break;
                default:
                    invoices = invoiceService.getAllInvoices();
            }
            model.addAttribute("invoices", invoices);
            model.addAttribute("filter", filter);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách hóa đơn: " + e.getMessage());
            logger.error("Error loading invoice management: {}", e.getMessage());
        }
        return "admin/invoice-management";
    }

    @GetMapping("/invoice-detail/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showInvoiceDetail(@PathVariable Integer id, Model model) {
        try {
            Optional<HoaDon> invoice = invoiceService.findById(id);
            if (invoice.isPresent()) {
                List<HoaDonChiTiet> details = invoiceService.getInvoiceDetails(id);
                model.addAttribute("invoice", invoice.get());
                model.addAttribute("details", details);
            } else {
                model.addAttribute("error", "Hóa đơn không tồn tại với ID: " + id);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            logger.error("Error loading invoice detail: {}", e.getMessage());
        }
        return "admin/invoice-detail";
    }

    @GetMapping("/invoice-delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteInvoice(@PathVariable Integer id, Model model) {
        try {
            Optional<HoaDon> invoice = invoiceService.findById(id);
            if (invoice.isPresent()) {
                invoiceService.delete(id);
                return "redirect:/admin/invoice-management?success=true";
            } else {
                return "redirect:/admin/invoice-management?error=true";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa hóa đơn: " + e.getMessage());
            return "redirect:/admin/invoice-management?error=true";
        }
    }

    // Demo offline: mark as paid (QR confirmed manually)
    @PostMapping("/invoice-mark-paid/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String markInvoicePaid(@PathVariable Integer id) {
        Optional<HoaDon> opt = invoiceService.findById(id);
        if(opt.isEmpty()) return "redirect:/admin/invoice-management?error=notfound";
        HoaDon hd = opt.get();
        hd.setTrangThai((byte) 5); // DA_THANH_TOAN
        invoiceService.save(hd);
    try { if (emailService != null) emailService.sendInvoicePaidEmail(hd); } catch (Exception ex) { logger.warn("Send invoice email failed: {}", ex.getMessage()); }
        return "redirect:/admin/invoice-detail/" + id + "?paid=1";
    }

    @PostMapping("/invoice-mark-cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String markInvoiceCancelled(@PathVariable Integer id) {
        Optional<HoaDon> opt = invoiceService.findById(id);
        if(opt.isEmpty()) return "redirect:/admin/invoice-management?error=notfound";
        HoaDon hd = opt.get();
        hd.setTrangThai((byte) 4); // DA_HUY
        invoiceService.save(hd);
        return "redirect:/admin/invoice-detail/" + id + "?cancelled=1";
    }
}