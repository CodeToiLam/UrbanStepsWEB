package vn.urbansteps.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.urbansteps.model.SanPham;
import vn.urbansteps.service.SanPhamService;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private SanPhamService sanPhamService;

    // Main admin dashboard - redirect to product list
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "redirect:/admin/products";
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public String getProductList(Model model, @RequestParam(required = false) Boolean trangThai) {
        try {
            if (trangThai != null) {
                model.addAttribute("products", sanPhamService.findByTrangThai(trangThai));
            } else {
                model.addAttribute("products", sanPhamService.getAllProducts());
            }
            model.addAttribute("activeFilter", trangThai);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách sản phẩm: " + e.getMessage());
        }
        return "admin/product-list";
    }

    @GetMapping("/products/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddProductForm(Model model) {
        model.addAttribute("sanPham", new SanPham());
        return "admin/product-add";
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public String addProduct(@ModelAttribute SanPham sanPham, Model model) {
        try {
            sanPhamService.save(sanPham);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return "admin/product-add";
        }
    }

    @GetMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getProductDetail(@PathVariable Integer id, Model model) {
        try {
            Optional<SanPham> sanPham = sanPhamService.findById(id);
            if (sanPham.isPresent()) {
                model.addAttribute("sanPham", sanPham.get());
            } else {
                model.addAttribute("error", "Sản phẩm không tồn tại");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải chi tiết sản phẩm: " + e.getMessage());
        }
        return "admin/product-detail";
    }

    @GetMapping("/products/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        try {
            Optional<SanPham> sanPham = sanPhamService.findById(id);
            if (sanPham.isPresent()) {
                model.addAttribute("sanPham", sanPham.get());
            } else {
                model.addAttribute("error", "Sản phẩm không tồn tại");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin sản phẩm: " + e.getMessage());
        }
        return "admin/product-add";
    }

    @PostMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute SanPham sanPham, Model model) {
        try {
            // Kiểm tra và gán giá trị mặc định nếu maSanPham null
            if (sanPham.getMaSanPham() == null || sanPham.getMaSanPham().trim().isEmpty()) {
                sanPham.setMaSanPham("SP_DEFAULT_" + id); // Giá trị mặc định, có thể tùy chỉnh
            }
            sanPham.setId(id); // Đảm bảo ID được gán
            sanPhamService.save(sanPham);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            model.addAttribute("title", "Sửa sản phẩm");
            return "admin/product-add";
        }
    }

    @GetMapping("/products/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(@PathVariable Integer id, Model model) {
        try {
            sanPhamService.softDelete(id);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/admin/products?error=true";
        }
    }
}