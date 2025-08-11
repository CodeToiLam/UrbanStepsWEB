package vn.urbansteps.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.GioHangService;
import vn.urbansteps.service.TaiKhoanService;

@ControllerAdvice
public class GlobalModelAttributeAdvice {
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private TaiKhoanService taiKhoanService;

    @ModelAttribute
    public void addCartToModel(Model model, HttpSession session) {
        try {
            GioHang gioHang = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
            if (username != null) {
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangWithItemsByUserId(taiKhoan.getId());
                }
            } else {
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangWithItemsBySessionId(sessionId);
            }
            model.addAttribute("gioHang", gioHang);
        } catch (Exception e) {
            model.addAttribute("gioHang", null);
        }
    }
}
