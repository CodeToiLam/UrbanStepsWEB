package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.urbansteps.service.HomeImageService;
import vn.urbansteps.service.SanPhamService;
import vn.urbansteps.service.ImageService;
import vn.urbansteps.model.SanPham;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private HomeImageService homeImageService;
    
    @Autowired
    private SanPhamService sanPhamService;
    
    @Autowired
    private ImageService imageService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            // Lấy banner images từ thư mục Home
            List<String> bannerImages = homeImageService.getBannerImages();
            model.addAttribute("bannerImages", bannerImages);
            
            // Lấy sản phẩm Flash Deal (sản phẩm giá thấp)
            List<SanPham> flashDealProducts = sanPhamService.getFlashDealProducts();
            // Giới hạn số lượng hiển thị
            if (flashDealProducts.size() > 8) {
                flashDealProducts = flashDealProducts.subList(0, 8);
            }
            
            // Xử lý ảnh cho Flash Deal products
            imageService.processProductListImages(flashDealProducts);
            System.out.println("=== PROCESSED FLASH DEAL IMAGES ===");
            
            model.addAttribute("flashDealProducts", flashDealProducts);
            
            // Lấy sản phẩm Top Popular (sản phẩm giá cao)
            List<SanPham> topPopularProducts = sanPhamService.getTopPopularProducts();
            // Giới hạn số lượng hiển thị
            if (topPopularProducts.size() > 8) {
                topPopularProducts = topPopularProducts.subList(0, 8);
            }
            
            // Xử lý ảnh cho Top Popular products
            imageService.processProductListImages(topPopularProducts);
            System.out.println("=== PROCESSED TOP POPULAR IMAGES ===");
            
            model.addAttribute("topPopularProducts", topPopularProducts);
            
            System.out.println("=== HOME PAGE DEBUG INFO ===");
            System.out.println("Banner Images count: " + bannerImages.size());
            System.out.println("Flash Deal Products count: " + flashDealProducts.size());
            System.out.println("Top Popular Products count: " + topPopularProducts.size());
            
        } catch (Exception e) {
            System.err.println("Error loading home page data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "index";
    }

    @GetMapping("/dang-nhap")
    public String login() {
        return "dang-nhap";
    }

    @GetMapping("/dang-ky")
    public String register() {
        return "dang-ky";
    }

    @Autowired
    private vn.urbansteps.service.TaiKhoanService taiKhoanService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/dang-nhap")
    public String processLogin(@RequestParam String taiKhoan,
                               @RequestParam String matKhau,
                               Model model) {
        vn.urbansteps.model.TaiKhoan user = taiKhoanService.findByTaiKhoan(taiKhoan);
        if (user == null) {
            model.addAttribute("loginError", "Tài khoản không tồn tại.");
            return "dang-nhap";
        }
        if (!passwordEncoder.matches(matKhau, user.getMatKhau())) {
            model.addAttribute("loginError", "Mật khẩu không đúng.");
            return "dang-nhap";
        }
        model.addAttribute("loginSuccess", "Đăng nhập thành công!");
        // TODO: Lưu session hoặc chuyển hướng hợp lý như các web lớn
        return "redirect:/";
    }

    @PostMapping("/dang-ky")
    public String processRegister() {
        return "redirect:/";
    }
}