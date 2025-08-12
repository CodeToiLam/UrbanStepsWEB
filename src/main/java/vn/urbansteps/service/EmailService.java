package vn.urbansteps.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.urbansteps.model.HoaDon;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String defaultFrom;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private boolean smtpConfigured() {
        return StringUtils.hasText(defaultFrom) && StringUtils.hasText(mailPassword);
    }

    public void sendOrderConfirmation(String to, String subject, String text) {
        try {
            // Demo-safe: if SMTP is not configured, simulate sending to avoid runtime errors
            if (!smtpConfigured()) {
                log.info("[EMAIL-SIMULATED] To: {} | Subject: {} | Body: {} chars", to, subject, text != null ? text.length() : 0);
                return;
            }
            SimpleMailMessage message = new SimpleMailMessage();
            if (defaultFrom != null && !defaultFrom.isBlank()) {
                message.setFrom(defaultFrom);
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Send plain email failed: {}", e.getMessage());
        }
    }

    public void sendOrderConfirmationHtml(String to, String subject, String html, @Nullable String plainFallback) {
        try {
            // Demo-safe: if SMTP is not configured, simulate sending to avoid runtime errors
            if (!smtpConfigured()) {
                String fallback = plainFallback != null ? plainFallback : (html != null ? html.replaceAll("<[^>]+>", " ") : "");
                log.info("[EMAIL-SIMULATED-HTML] To: {} | Subject: {} | Body(plain-ish): {} chars", to, subject, fallback.length());
                return;
            }
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            if (defaultFrom != null && !defaultFrom.isBlank()) {
                helper.setFrom(defaultFrom);
            }
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainFallback != null ? plainFallback : html.replaceAll("<[^>]+>", " "), html);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Send HTML email failed, fallback to plain: {}", e.getMessage());
            if (plainFallback != null) {
                sendOrderConfirmation(to, subject, plainFallback);
            }
        }
    }

    /**
     * Send order status update email for any status change.
     */
    public void sendOrderStatusUpdateEmail(HoaDon order) {
        try {
            if (order == null || order.getKhachHang() == null) {
                log.warn("sendOrderStatusUpdateEmail: missing customer for order {}", order != null ? order.getId() : null);
                return;
            }
            String to = order.getKhachHang().getEmail();
            if (!StringUtils.hasText(to)) {
                log.warn("sendOrderStatusUpdateEmail: customer has no email for order {}", order.getMaHoaDon());
                return;
            }
            String subject = "UrbanSteps - Cập nhật trạng thái đơn " + (order.getMaHoaDon() != null ? order.getMaHoaDon() : order.getId());
            String statusText = order.getTrangThaiText();
            String html = "<div style='font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#333'>" +
                    "<p>Xin chào,</p>" +
                    "<p>Đơn hàng <b>" + (order.getMaHoaDon() != null ? order.getMaHoaDon() : order.getId()) +
                    "</b> đã được cập nhật trạng thái: <b>" + statusText + "</b>.</p>" +
                    "<p>Tổng tiền: <b>" + (order.getTongThanhToan()!=null?order.getTongThanhToan().toPlainString():"0") + " VNĐ</b></p>" +
                    "<p>Bạn có thể xem chi tiết đơn hàng tại khu vực tài khoản.</p>" +
                    "<p>UrbanSteps</p>" +
                    "</div>";
            String plain = "Xin chào,\nĐơn hàng " + (order.getMaHoaDon() != null ? order.getMaHoaDon() : order.getId()) +
                    " đã được cập nhật trạng thái: " + statusText +
                    ".\nTổng tiền: " + (order.getTongThanhToan()!=null?order.getTongThanhToan().toPlainString():"0") + " VNĐ\nUrbanSteps";
            sendOrderConfirmationHtml(to, subject, html, plain);
        } catch (Exception e) {
            log.error("sendOrderStatusUpdateEmail failed: {}", e.getMessage());
        }
    }

    /**
     * Send an email when an invoice is marked as paid (admin confirms QR transfer).
     */
    public void sendInvoicePaidEmail(HoaDon invoice) {
        try {
            if (invoice == null || invoice.getKhachHang() == null) {
                log.warn("sendInvoicePaidEmail: missing customer info for invoice {}", invoice != null ? invoice.getId() : null);
                return;
            }
            String to = invoice.getKhachHang().getEmail();
            if (!StringUtils.hasText(to)) {
                log.warn("sendInvoicePaidEmail: customer has no email for invoice {}", invoice.getMaHoaDon());
                return;
            }
            String subject = "UrbanSteps - Xác nhận thanh toán thành công " + invoice.getMaHoaDon();
            String html = "" +
                    "<div style='font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#333'>" +
                    "<h2 style='color:#d33;margin:0 0 12px'>Cảm ơn bạn đã thanh toán!</h2>" +
                    "<p>Mã đơn/hóa đơn: <b>" + (invoice.getMaHoaDon()!=null?invoice.getMaHoaDon():invoice.getId()) + "</b></p>" +
                    "<p>Tổng thanh toán: <b>" + (invoice.getTongThanhToan()!=null?invoice.getTongThanhToan().toPlainString():"0") + " VNĐ</b></p>" +
                    "<p>Trạng thái: <b>Đã thanh toán</b></p>" +
                    "<hr style='border:none;border-top:1px solid #eee;margin:16px 0'/>" +
                    "<p>Bạn có thể xem đơn hàng tại khu vực tài khoản trên website UrbanSteps.</p>" +
                    "<p>Nếu có thắc mắc, vui lòng phản hồi email này.</p>" +
                    "<p>UrbanSteps</p>" +
                    "</div>";

            String plain = "Cảm ơn bạn đã thanh toán!\n" +
                    "Mã đơn/hóa đơn: " + (invoice.getMaHoaDon()!=null?invoice.getMaHoaDon():invoice.getId()) + "\n" +
                    "Tổng thanh toán: " + (invoice.getTongThanhToan()!=null?invoice.getTongThanhToan().toPlainString():"0") + " VNĐ\n" +
                    "Trạng thái: Đã thanh toán\n" +
                    "Bạn có thể xem đơn hàng tại khu vực tài khoản trên website UrbanSteps.";

            sendOrderConfirmationHtml(to, subject, html, plain);
        } catch (Exception e) {
            log.error("sendInvoicePaidEmail failed: {}", e.getMessage());
        }
    }
}
