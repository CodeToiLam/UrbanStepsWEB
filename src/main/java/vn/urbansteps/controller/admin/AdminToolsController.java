package vn.urbansteps.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.urbansteps.service.EmailService;

@Controller
@RequestMapping("/admin")
public class AdminToolsController {

    @Autowired(required = false)
    private EmailService emailService;

    @GetMapping("/email-test")
    public String emailTest(@RequestParam(name = "to", required = false) String to,
                            RedirectAttributes ra) {
        try {
            if (emailService == null) {
                ra.addFlashAttribute("error", "EmailService không khả dụng trong ứng dụng.");
                return "redirect:/admin/order-management";
            }
            String target = (to == null || to.isBlank()) ? System.getProperty("user.name") + "@example.com" : to;
            emailService.sendOrderConfirmation(target,
                    "UrbanSteps - Email test",
                    "Đây là email test từ hệ thống UrbanSteps.");
            ra.addFlashAttribute("success", "Đã gọi gửi email test tới: " + target +
                    " (nếu chưa cấu hình SMTP sẽ chỉ mô phỏng trong log).");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gửi email test thất bại: " + e.getMessage());
        }
        return "redirect:/admin/order-management";
    }
}
