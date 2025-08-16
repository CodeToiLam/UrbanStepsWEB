package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import vn.urbansteps.service.SanPhamService;
import vn.urbansteps.service.ImageService;
import vn.urbansteps.model.SanPham;
import java.util.List;

@Controller
public class TimKiemController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam(required = false) String keyword, 
                         @RequestParam(name = "q", required = false) String legacyQ,
                         @RequestParam(required = false) String thuongHieu,
                         @RequestParam(required = false) String sapXep,
                         Model model) {
        
        List<SanPham> products = null;
        
        try {
            // Hỗ trợ cả tham số 'q' từ header cũ
            if ((keyword == null || keyword.trim().isEmpty()) && legacyQ != null && !legacyQ.trim().isEmpty()) {
                keyword = legacyQ;
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                // Tìm kiếm sản phẩm theo tên
                products = sanPhamService.timKiemTheoTen(keyword.trim());
                model.addAttribute("keyword", keyword);
                System.out.println("Search results for '" + keyword + "': " + products.size() + " products");
            } else {
                // Hiển thị tất cả sản phẩm nếu không có từ khóa
                products = sanPhamService.getAllProducts();
                System.out.println("All products: " + products.size() + " products");
            }
            
            // products luôn != null ở các nhánh phía trên; không cần kiểm tra null tại đây
            
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
        
        // Xử lý ảnh cho tất cả sản phẩm
        imageService.processProductListImages(products);
        System.out.println("=== PROCESSED SEARCH IMAGES ===");
        
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

    // API gợi ý tìm kiếm nhanh cho dropdown (tối đa 8 mục)
    @GetMapping(value = "/api/search/suggest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<java.util.Map<String, Object>> suggest(@RequestParam(name = "q", required = false) String q) {
        String keyword = q == null ? "" : q.trim();
        List<SanPham> results;
        try {
            results = sanPhamService.timKiemTheoTen(keyword);
        } catch (Exception e) {
            results = java.util.Collections.emptyList();
        }
        return results.stream()
                .filter(sp -> sp != null)
                .limit(8)
                .map(sp -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", sp.getId());
                    m.put("name", sp.getTenSanPham());
                    m.put("brand", sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : null);
                    m.put("price", sp.getGiaBan());
                    m.put("image", sp.getIdHinhAnhDaiDien() != null ? sp.getIdHinhAnhDaiDien().getDuongDan() : null);
                    m.put("url", "/san-pham/chi-tiet/" + sp.getId());
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
