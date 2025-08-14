package vn.urbansteps.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.urbansteps.model.SanPham;
import vn.urbansteps.service.SanPhamService;

import java.util.List;
import java.util.Optional;

import vn.urbansteps.service.SanPhamChiTietService;
import java.util.Collections;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    // Aspect-based logging in place; no direct service injection needed here

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "redirect:/admin/products";
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public String getProductList(Model model, @RequestParam(required = false) Boolean trangThai) {
        try {
            List<SanPham> products;
            if (trangThai != null) {
                products = sanPhamService.findByTrangThai(trangThai).subList(0, Math.min(50, sanPhamService.findByTrangThai(trangThai).size()));
            } else {
                products = sanPhamService.getAllProducts().subList(0, Math.min(50, sanPhamService.getAllProducts().size()));
            }
            model.addAttribute("products", products);
            model.addAttribute("activeFilter", trangThai);
            System.out.println("Debug: Số sản phẩm trả về = " + products.size());
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
            // log by aspect
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
            if (sanPham.getMaSanPham() == null || sanPham.getMaSanPham().trim().isEmpty()) {
                sanPham.setMaSanPham("SP_DEFAULT_" + id);
            }
            sanPham.setId(id);
            sanPhamService.save(sanPham);
            // log by aspect
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
            // log by aspect
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/admin/products?error=true";
        }
    }

    @GetMapping("/products/chi-tiet/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showProductChiTiet(@PathVariable Integer id, Model model) {
        try {
            Optional<SanPham> sanPham = sanPhamService.findById(id);
            if (sanPham.isPresent()) {
                model.addAttribute("sanPham", sanPham.get());
                return "redirect:/admin/product-management?selectedSanPhamId=" + id;
            } else {
                model.addAttribute("error", "Sản phẩm không tồn tại");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải chi tiết sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/product-management")
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable("productManagement")
    public String showProductManagement(Model model, @RequestParam(required = false) Integer selectedSanPhamId) {
        try {
            List<SanPham> sanPhams = sanPhamService.findByTrangThai(true).subList(0, Math.min(50, sanPhamService.findByTrangThai(true).size()));
            model.addAttribute("sanPhams", sanPhams);
            SanPham sanPham = new SanPham();
            if (!sanPhams.isEmpty()) {
                if (selectedSanPhamId == null && sanPhams.size() > 0) {
                    selectedSanPhamId = sanPhams.get(0).getId();
                }
                model.addAttribute("selectedSanPhamId", selectedSanPhamId);
                Optional<SanPham> sanPhamOpt = sanPhamService.findById(selectedSanPhamId != null ? selectedSanPhamId : sanPhams.get(0).getId());
                sanPham = sanPhamOpt.orElse(new SanPham());
                model.addAttribute("chiTiets", sanPhamChiTietService.getBySanPhamId(selectedSanPhamId != null ? selectedSanPhamId : sanPhams.get(0).getId()));
                model.addAttribute("kichCos", sanPhamChiTietService.getKichCosBySanPhamId(selectedSanPhamId != null ? selectedSanPhamId : sanPhams.get(0).getId()));
                model.addAttribute("mauSacs", sanPhamChiTietService.getMauSacsBySanPhamId(selectedSanPhamId != null ? selectedSanPhamId : sanPhams.get(0).getId()));
                model.addAttribute("tonKho", sanPhamChiTietService.getTonKhoBySanPhamId(selectedSanPhamId != null ? selectedSanPhamId : sanPhams.get(0).getId()));
            } else {
                model.addAttribute("chiTiets", Collections.emptyList());
                model.addAttribute("kichCos", Collections.emptyList());
                model.addAttribute("mauSacs", Collections.emptyList());
                model.addAttribute("tonKho", Collections.emptyMap());
            }
            model.addAttribute("sanPham", sanPham);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải trang quản lý sản phẩm: " + e.getMessage());
        }
        return "admin/product-management";
    }

    @PostMapping("/product-management/select")
    @PreAuthorize("hasRole('ADMIN')")
    public String selectProductForManagement(@RequestParam(required = false) Integer sanPhamId, Model model) {
        try {
            if (sanPhamId == null) {
                model.addAttribute("error", "Vui lòng chọn một sản phẩm.");
                return "admin/product-management";
            }
            Optional<SanPham> sanPhamOpt = sanPhamService.findById(sanPhamId);
            SanPham sanPham = sanPhamOpt.orElse(new SanPham());
            model.addAttribute("sanPhams", sanPhamService.findByTrangThai(true).subList(0, Math.min(50, sanPhamService.findByTrangThai(true).size())));
            model.addAttribute("sanPham", sanPham);
            model.addAttribute("selectedSanPhamId", sanPhamId);
            model.addAttribute("chiTiets", sanPhamChiTietService.getBySanPhamId(sanPhamId));
            model.addAttribute("kichCos", sanPhamChiTietService.getKichCosBySanPhamId(sanPhamId));
            model.addAttribute("mauSacs", sanPhamChiTietService.getMauSacsBySanPhamId(sanPhamId));
            model.addAttribute("tonKho", sanPhamChiTietService.getTonKhoBySanPhamId(sanPhamId));
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi chọn sản phẩm: " + e.getMessage());
        }
        return "admin/product-management";
    }
}