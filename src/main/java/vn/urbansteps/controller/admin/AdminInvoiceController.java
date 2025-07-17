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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminInvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(AdminInvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/invoice-management")
    @PreAuthorize("hasRole('ADMIN')")
    public String showInvoiceManagement(Model model) {
        try {
            List<HoaDon> invoices = invoiceService.getAllInvoices();
            model.addAttribute("invoices", invoices);
            model.addAttribute("filter", "all");
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
}