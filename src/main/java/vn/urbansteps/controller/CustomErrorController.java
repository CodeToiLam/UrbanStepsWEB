package vn.urbansteps.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("status", statusCode);
            
            // Add specific error messages
            switch (statusCode) {
                case 404:
                    model.addAttribute("error", "Not Found");
                    model.addAttribute("message", "Trang không tìm thấy");
                    break;
                case 500:
                    model.addAttribute("error", "Internal Server Error");
                    model.addAttribute("message", "Lỗi hệ thống");
                    break;
                case 403:
                    model.addAttribute("error", "Forbidden");
                    model.addAttribute("message", "Không có quyền truy cập");
                    break;
                default:
                    model.addAttribute("error", "Unknown Error");
                    model.addAttribute("message", "Lỗi không xác định");
                    break;
            }
        }

        if (message != null) {
            model.addAttribute("originalMessage", message.toString());
        }

        if (requestUri != null) {
            model.addAttribute("path", requestUri.toString());
        }

        model.addAttribute("timestamp", java.time.LocalDateTime.now());

        return "error";
    }
}
