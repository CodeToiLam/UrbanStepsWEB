package vn.urbansteps.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeImageService {
    
    private static final String HOME_IMAGES_PATH = "static/images/Home";
    
    /**
     * Lấy danh sách tất cả ảnh sản phẩm từ thư mục Home (loại trừ banner)
     */
    public List<String> getProductImages() {
        try {
            Resource resource = new ClassPathResource(HOME_IMAGES_PATH);
            Path homePath = Paths.get(resource.getURI());
            
            return Files.list(homePath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(filename -> !filename.equals("2.png") && !filename.equals("3.png")) // Loại trừ banner
                    .filter(filename -> !filename.toLowerCase().contains("adidas-chinh-hang-tai-sneaker-daily")) // Loại trừ logo adidas
                    .filter(filename -> !filename.toLowerCase().contains("mlb-chinh-hang-tai-sneaker-daily")) // Loại trừ logo mlb
                    .filter(filename -> filename.toLowerCase().endsWith(".png") || 
                                      filename.toLowerCase().endsWith(".jpg") || 
                                      filename.toLowerCase().endsWith(".jpeg") ||
                                      filename.toLowerCase().endsWith(".webp"))
                    .map(filename -> "images/Home/" + filename)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Fallback: trả về list ảnh sản phẩm tĩnh (không có logo thương hiệu)
            return List.of(
                "images/Home/Giay-Converse-Chuck-Taylor-All-Star-Cruise-OX-Black-White-A08789C-200x200.jpeg",
                "images/Home/giay-mlb-chunky-liner-new-york-yankees-grey-3asxca12n-50grl-200x200.png",
                "images/Home/Giay-Adidas-Barricade-13-Tennis-Black-IF0466-200x200.jpeg",
                "images/Home/giay-adifom-superstar-core-black-hq8752-11-200x200.png",
                "images/Home/Giay-Converse-Chuck-Taylor-All-Star-EVA-Lift-Platform-Y2K-Heart-High-Top-Black-A09121C-200x200.jpeg",
                "images/Home/Giay-MLB-Chunky-Liner-SL-Saffiano-Boston-Red-Sox-3ASXCLS4N-43BGS-200x200.jpeg",
                "images/Home/adidas-superstar-white-black-c77124-1-200x200.png",
                "images/Home/Giay-Converse-Chuck-70-Hi-Wonder-Mauve-A07977C-200x200.jpeg",
                "images/Home/Giay-MLB-Chunky-Liner-Mid-Classic-Monogram-Los-Angeles-Dodgers-3ASXLM13N-07CBL-200x200.jpeg",
                "images/Home/Giay-adidas-Yeezy-Boost-350-V2-Steel-Grey-IF3219-200x200.jpeg",
                "images/Home/Giay-Adidas-adiFOM-Supernova-Triple-Black-IF3915-200x200.jpeg",
                "images/Home/Giay-adidas-Samba-OG-White-Black-Gum-B75806-200x200.jpeg"
            );
        }
    }
    
    /**
     * Lấy danh sách ảnh banner
     */
    public List<String> getBannerImages() {
        return List.of(
            "images/Home/2.png",
            "images/Home/3.png"
        );
    }
    
    /**
     * Lấy ảnh Flash Deal (từ thư mục Home)
     */
    public List<String> getFlashDealImages() {
        List<String> allImages = getProductImages();
        // Lấy 6 ảnh đầu tiên cho Flash Deal
        return allImages.stream().limit(6).collect(Collectors.toList());
    }
    
    /**
     * Lấy ảnh Top Popular (từ thư mục Home)
     */
    public List<String> getTopPopularImages() {
        List<String> allImages = getProductImages();
        // Lấy 6 ảnh từ giữa danh sách cho Top Popular
        return allImages.stream().skip(6).limit(6).collect(Collectors.toList());
    }
}
