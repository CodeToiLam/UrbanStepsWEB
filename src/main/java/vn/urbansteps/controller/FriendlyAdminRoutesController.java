package vn.urbansteps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

@Controller
@RequestMapping("/admin")
public class FriendlyAdminRoutesController {

    // Helper: redirect và bảo toàn toàn bộ query params (hỗ trợ param lặp)
    private String redirectWithParams(String base, MultiValueMap<String, String> params) {
        if (params == null || params.isEmpty()) return "redirect:" + base;

        StringJoiner sj = new StringJoiner("&");
        params.forEach((keyRaw, values) -> {
            if (keyRaw == null || keyRaw.isBlank()) return;
            String key = URLEncoder.encode(keyRaw, StandardCharsets.UTF_8);
            if (values == null || values.isEmpty()) {
                sj.add(key + "=");
            } else {
                for (String v : values) {
                    String val = v == null ? "" : URLEncoder.encode(v, StandardCharsets.UTF_8);
                    sj.add(key + "=" + val);
                }
            }
        });

        String qs = sj.toString();
        return qs.isEmpty() ? ("redirect:" + base) : ("redirect:" + base + "?" + qs);
    }

    // Products
    @GetMapping({"/quan-ly-san-pham", "/san-pham"})
    public String products(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/products", params);
    }

    @GetMapping({"/quan-ly-san-pham-chi-tiet", "/san-pham/quan-ly"})
    public String productManagement(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/product-management", params);
    }

    @GetMapping({"/chi-tiet-san-pham/{id}", "/san-pham/{id}"})
    public String productDetail(@PathVariable Integer id,
                                @RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/products/" + id, params);
    }

    // Discounts
    @GetMapping({"/quan-ly-phieu-giam-gia", "/phieu-giam-gia"})
    public String discountManagement(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/discount-management", params);
    }

    // Orders
    @GetMapping({"/quan-ly-don-hang", "/don-hang"})
    public String orderManagement(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/order-management", params);
    }

    @GetMapping({"/chi-tiet-don-hang/{id}", "/don-hang/{id}"})
    public String orderDetail(@PathVariable Integer id,
                              @RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/order-detail/" + id, params);
    }

    // Invoices
    @GetMapping({"/quan-ly-hoa-don", "/hoa-don"})
    public String invoiceManagement(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/invoice-management", params);
    }

    @GetMapping({"/chi-tiet-hoa-don/{id}", "/hoa-don/{id}"})
    public String invoiceDetail(@PathVariable Integer id,
                                @RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/invoice-detail/" + id, params);
    }

    // POS
    @GetMapping({"/ban-hang-tai-quay", "/ban-hang"})
    public String pos(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/pos", params);
    }

    // Logs
    @GetMapping({"/nhat-ky-admin", "/nhat-ky"})
    public String adminLogs(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/admin-logs", params);
    }

    // Dashboard aliases
    @GetMapping({"/dashboard", "/tong-quan", "/trang-chu"})
    public String dashboard(@RequestParam MultiValueMap<String, String> params) {
        return redirectWithParams("/admin/thong-ke", params);
    }
}