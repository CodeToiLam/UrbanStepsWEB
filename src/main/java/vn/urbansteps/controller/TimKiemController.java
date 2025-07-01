package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.urbansteps.service.SanPhamService;
import vn.urbansteps.model.SanPham;
import java.util.List;

@Controller
public class TimKiemController {

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam(required = false) String keyword, 
                         @RequestParam(required = false) String thuongHieu,
                         @RequestParam(required = false) String sapXep,
                         Model model) {
        
        List<SanPham> products = null;
        
        try {
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Tìm kiếm sản phẩm theo tên
                products = sanPhamService.timKiemTheoTen(keyword.trim());
                model.addAttribute("keyword", keyword);
                System.out.println("Search results for '" + keyword + "': " + products.size() + " products");
            } else {
                // Hiển thị tất cả sản phẩm nếu không có từ khóa
                products = sanPhamService.layTatCaSanPham();
                System.out.println("All products: " + products.size() + " products");
            }
            
            // Kiểm tra null safety
            if (products == null) {
                products = new java.util.ArrayList<>();
                System.out.println("Products was null, created empty list");
            }
            
            // Log thông tin sản phẩm để debug
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                SanPham product = products.get(i);
                if (product != null) {
                    System.out.println("Product " + i + ": ID=" + product.getId() + 
                                     ", Name=" + product.getTenSanPham() + 
                                     ", Price=" + product.getGiaBan());
                } else {
                    System.out.println("Product " + i + " is NULL!");
                }
            }
            
            // Lọc theo thương hiệu nếu có
            if (thuongHieu != null && !thuongHieu.trim().isEmpty() && !thuongHieu.equals("all")) {
                products = products.stream()
                    .filter(sp -> sp != null && sp.getThuongHieu() != null && 
                                 sp.getThuongHieu().getTenThuongHieu().toLowerCase().contains(thuongHieu.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Sắp xếp nếu có
            if (sapXep != null && !sapXep.isEmpty()) {
                switch (sapXep) {
                    case "price-asc":
                        products.sort((a, b) -> a.getGiaBan().compareTo(b.getGiaBan()));
                        break;
                    case "price-desc":
                        products.sort((a, b) -> b.getGiaBan().compareTo(a.getGiaBan()));
                        break;
                    case "name-asc":
                        products.sort((a, b) -> a.getTenSanPham().compareToIgnoreCase(b.getTenSanPham()));
                        break;
                    case "name-desc":
                        products.sort((a, b) -> b.getTenSanPham().compareToIgnoreCase(a.getTenSanPham()));
                        break;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error in search controller: " + e.getMessage());
            e.printStackTrace();
            products = new java.util.ArrayList<>();
        }
        
        model.addAttribute("products", products);
        model.addAttribute("thuongHieu", thuongHieu);
        model.addAttribute("sapXep", sapXep);
        
        // Thêm danh sách thương hiệu để hiển thị filter
        try {
            List<String> danhSachThuongHieu = sanPhamService.layDanhSachThuongHieu();
            model.addAttribute("danhSachThuongHieu", danhSachThuongHieu);
        } catch (Exception e) {
            System.err.println("Error getting brands: " + e.getMessage());
            model.addAttribute("danhSachThuongHieu", new java.util.ArrayList<>());
        }
        
        return "tim-kiem";
    }
}
