package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.KhachHang;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.repository.TaiKhoanRepository;
import vn.urbansteps.service.HoaDonService;

import java.util.List;
import java.util.Optional;

@Controller
public class OrderTrackingController {

    @Autowired
    private HoaDonService hoaDonService;
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    
    @Autowired
    private KhachHangRepository khachHangRepository;

    @GetMapping("/don-hang")
    public String donHang(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            // User đã đăng nhập - hiển thị đơn hàng của họ
            String username = auth.getName();
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByEmail(username);
            
            if (taiKhoanOpt.isPresent()) {
                TaiKhoan taiKhoan = taiKhoanOpt.get();
                Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
                
                if (khachHangOpt.isPresent()) {
                    KhachHang khachHang = khachHangOpt.get();
                    List<HoaDon> orders = hoaDonService.getOrdersByKhachHangId(khachHang.getId());
                    model.addAttribute("orders", orders);
                    model.addAttribute("isLoggedIn", true);
                    model.addAttribute("customerName", khachHang.getHoTenKhachHang());
                } else {
                    model.addAttribute("orders", List.of());
                    model.addAttribute("isLoggedIn", true);
                    model.addAttribute("customerName", taiKhoan.getEmail());
                }
            }
        } else {
            // User chưa đăng nhập - hiển thị form tra cứu
            model.addAttribute("isLoggedIn", false);
        }
        
        model.addAttribute("title", "Theo dõi đơn hàng");
        return "don-hang";
    }
    
    @PostMapping("/don-hang/tra-cuu")
    public String traCuuDonHang(@RequestParam String maHoaDon, 
                               @RequestParam String sdt,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            // Tìm đơn hàng theo mã và số điện thoại
            Optional<HoaDon> hoaDonOpt = hoaDonService.findByMaHoaDonAndSdt(maHoaDon, sdt);
            
            if (hoaDonOpt.isPresent()) {
                HoaDon hoaDon = hoaDonOpt.get();
                model.addAttribute("order", hoaDon);
                model.addAttribute("isLoggedIn", false);
                model.addAttribute("title", "Chi tiết đơn hàng #" + hoaDon.getMaHoaDon());
                return "chi-tiet-don-hang";
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng với mã và số điện thoại này!");
                return "redirect:/don-hang";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tra cứu đơn hàng!");
            return "redirect:/don-hang";
        }
    }
    
    @GetMapping("/don-hang/chi-tiet/{id}")
    public String chiTietDonHang(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để xem chi tiết đơn hàng!");
            return "redirect:/dang-nhap";
        }
        
        try {
            String username = auth.getName();
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByEmail(username);
            
            if (taiKhoanOpt.isPresent()) {
                TaiKhoan taiKhoan = taiKhoanOpt.get();
                Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
                
                if (khachHangOpt.isPresent()) {
                    KhachHang khachHang = khachHangOpt.get();
                    HoaDon hoaDon = hoaDonService.getOrderById(id);
                    
                    // Kiểm tra đơn hàng có thuộc về khách hàng này không
                    if (hoaDon != null && hoaDon.getKhachHang().getId().equals(khachHang.getId())) {
                        model.addAttribute("order", hoaDon);
                        model.addAttribute("isLoggedIn", true);
                        model.addAttribute("title", "Chi tiết đơn hàng #" + hoaDon.getMaHoaDon());
                        return "chi-tiet-don-hang";
                    }
                }
            }
            
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng hoặc bạn không có quyền xem đơn hàng này!");
            return "redirect:/don-hang";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xem chi tiết đơn hàng!");
            return "redirect:/don-hang";
        }
    }
    
    @PostMapping("/don-hang/huy/{id}")
    public String huyDonHang(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để hủy đơn hàng!");
            return "redirect:/dang-nhap";
        }
        
        try {
            String username = auth.getName();
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByEmail(username);
            
            if (taiKhoanOpt.isPresent()) {
                TaiKhoan taiKhoan = taiKhoanOpt.get();
                Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
                
                if (khachHangOpt.isPresent()) {
                    KhachHang khachHang = khachHangOpt.get();
                    HoaDon hoaDon = hoaDonService.getOrderById(id);
                    
                    // Kiểm tra đơn hàng có thuộc về khách hàng này không và có thể hủy không
                    if (hoaDon != null && hoaDon.getKhachHang().getId().equals(khachHang.getId())) {
                        if (hoaDon.canCancel()) {
                            hoaDon.setTrangThai((byte) 4); // 4: Đã hủy
                            hoaDonService.save(hoaDon);
                            redirectAttributes.addFlashAttribute("success", "Hủy đơn hàng thành công!");
                        } else {
                            redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng này!");
                        }
                        
                        return "redirect:/don-hang/chi-tiet/" + id;
                    }
                }
            }
            
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng hoặc bạn không có quyền hủy đơn hàng này!");
            return "redirect:/don-hang";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi hủy đơn hàng!");
            return "redirect:/don-hang";
        }
    }
}
