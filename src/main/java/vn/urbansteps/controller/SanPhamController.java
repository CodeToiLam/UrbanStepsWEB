package vn.urbansteps.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.service.SanPhamService;

import java.util.List;
import java.util.Optional;

@Controller
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @GetMapping("/san-pham")
    public String danhSach(
            @RequestParam(required = false) String thuongHieu,
            @RequestParam(required = false) Boolean sale,
            @RequestParam(required = false) Boolean hot,
            Model model) {
        
        List<SanPham> products;
        
        if (thuongHieu != null) {
            products = sanPhamService.findByThuongHieu(thuongHieu);
            model.addAttribute("title", "Sản phẩm " + thuongHieu);
        } else if (sale != null && sale) {
            products = sanPhamService.getSaleProducts();
            model.addAttribute("title", "Sản phẩm SALE");
        } else if (hot != null && hot) {
            products = sanPhamService.getHotProducts();
            model.addAttribute("title", "Sản phẩm HOT");
        } else {
            products = sanPhamService.getAllProducts();
            model.addAttribute("title", "Tất cả sản phẩm");
        }
        
        model.addAttribute("products", products);
        return "san-pham/danh-sach";
    }

    @GetMapping("/san-pham/chi-tiet/{id}")
    public String chiTiet(@PathVariable Integer id, Model model) {
        Optional<SanPham> product = sanPhamService.findById(id);
        
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            // Lấy sản phẩm liên quan
            model.addAttribute("relatedProducts", sanPhamService.getRelatedProducts(id));
            return "san-pham/chi-tiet";
        } else {
            return "redirect:/san-pham";
        }
    }
}
