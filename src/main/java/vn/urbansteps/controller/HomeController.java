package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import vn.urbansteps.service.HomeImageService;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private HomeImageService homeImageService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            // Sử dụng ảnh từ thư mục Home thay vì database
            List<String> flashDealImages = homeImageService.getFlashDealImages();
            List<String> topPopularImages = homeImageService.getTopPopularImages();
            List<String> bannerImages = homeImageService.getBannerImages();
            
            System.out.println("=== DEBUG INFO ===");
            System.out.println("Flash Deal Images count: " + flashDealImages.size());
            System.out.println("Top Popular Images count: " + topPopularImages.size());
            System.out.println("Banner Images count: " + bannerImages.size());
            
            // Thêm ảnh vào model
            model.addAttribute("flashDealImages", flashDealImages);
            model.addAttribute("topPopularImages", topPopularImages);
            model.addAttribute("bannerImages", bannerImages);
            
            // Nếu không có dữ liệu, tạo dữ liệu mẫu
            if (flashDealImages.isEmpty() && topPopularImages.isEmpty()) {
                System.out.println("No images found in Home folder");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
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

    @PostMapping("/dang-nhap")
    public String processLogin() {

        return "redirect:/";
    }

    @PostMapping("/dang-ky")
    public String processRegister() {
        return "redirect:/";
    }
}