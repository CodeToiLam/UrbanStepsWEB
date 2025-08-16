package vn.urbansteps.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.KichCo;
import vn.urbansteps.model.MauSac;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.service.SanPhamChiTietService;
import vn.urbansteps.service.SanPhamService;
import vn.urbansteps.service.KichCoService;
import vn.urbansteps.service.MauSacService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminProductDetailController {

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private KichCoService kichCoService;
    @Autowired
    private MauSacService mauSacService;

    @GetMapping("/san-pham-chi-tiet")
    @PreAuthorize("hasRole('ADMIN')")
    public String listSanPhamChiTietDebug(@RequestParam(required = false) Integer sanPhamId, Model model) {
        try {
            SanPham sanPham = new SanPham();
            sanPham.setTenSanPham("Không xác định");
            sanPham.setId(0);
            if (sanPhamId != null) {
                Optional<SanPham> sanPhamOpt = sanPhamService.findById(sanPhamId);
                sanPham = sanPhamOpt.orElse(sanPham);
                List<SanPhamChiTiet> chiTiets = sanPhamChiTietService.getBySanPhamId(sanPhamId) != null
                        ? sanPhamChiTietService.getBySanPhamId(sanPhamId) : Collections.emptyList();
                model.addAttribute("sanPham", sanPham);
                model.addAttribute("chiTiets", chiTiets);
            } else {
                List<SanPhamChiTiet> chiTiets = sanPhamChiTietService.getAllSanPhamChiTiets() != null
                        ? sanPhamChiTietService.getAllSanPhamChiTiets() : Collections.emptyList();
                model.addAttribute("chiTiets", chiTiets);
                model.addAttribute("sanPham", sanPham);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách chi tiết sản phẩm: " + e.getMessage());
            return "admin/product-detail-list";
        }
        return "admin/product-detail-list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(@RequestParam(required = false) Integer sanPhamId, Model model,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            if (sanPhamId == null) {
                ra.addFlashAttribute("error", "Vui lòng chọn một sản phẩm để thêm chi tiết.");
                return "redirect:/admin/product-management";
            }
            Optional<SanPham> sanPhamOpt = sanPhamService.findById(sanPhamId);
            if (!sanPhamOpt.isPresent()) {
                ra.addFlashAttribute("error", "Sản phẩm không tồn tại với ID: " + sanPhamId);
                return "redirect:/admin/product-management";
            }
            SanPham sanPham = sanPhamOpt.get();
            SanPhamChiTiet chiTiet = new SanPhamChiTiet();
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(0); // Khởi tạo soLuong
            chiTiet.setTrangThai(true); // Khởi tạo trangThai
            List<KichCo> kichCos = kichCoService.getAllKichCos() != null ? kichCoService.getAllKichCos() : Collections.emptyList();
            List<MauSac> mauSacs = mauSacService.getAllMauSacs() != null ? mauSacService.getAllMauSacs() : Collections.emptyList();
            if (kichCos.isEmpty() || mauSacs.isEmpty()) {
                ra.addFlashAttribute("error", "Không có kích cỡ hoặc màu sắc để thêm chi tiết.");
                return "redirect:/admin/product-management?selectedSanPhamId=" + sanPhamId;
            }
            model.addAttribute("chiTiet", chiTiet);
            model.addAttribute("sanPham", sanPham);
            model.addAttribute("kichCos", kichCos);
            model.addAttribute("mauSacs", mauSacs);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi tải form thêm chi tiết: " + e.getMessage());
            return "redirect:/admin/product-management?selectedSanPhamId=" + (sanPhamId != null ? sanPhamId : 0);
        }
        return "admin/product-detail-form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveSanPhamChiTiet(@ModelAttribute SanPhamChiTiet chiTiet, BindingResult result, Model model,
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("sanPham", sanPhamService.findById(chiTiet.getSanPham().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại")));
                List<KichCo> kichCos = kichCoService.getAllKichCos() != null ? kichCoService.getAllKichCos() : Collections.emptyList();
                List<MauSac> mauSacs = mauSacService.getAllMauSacs() != null ? mauSacService.getAllMauSacs() : Collections.emptyList();
                model.addAttribute("kichCos", kichCos);
                model.addAttribute("mauSacs", mauSacs);
                model.addAttribute("error", "Dữ liệu không hợp lệ, vui lòng kiểm tra lại.");
                return "admin/product-detail-form";
            }
            if (chiTiet.getSanPham() == null || chiTiet.getSanPham().getId() == null) {
                throw new IllegalArgumentException("Sản phẩm không hợp lệ.");
            }
            if (chiTiet.getSoLuong() == null || chiTiet.getSoLuong() < 0) {
                chiTiet.setSoLuong(0);
            }
            SanPham sanPham = sanPhamService.findById(chiTiet.getSanPham().getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + chiTiet.getSanPham().getId()));
            chiTiet.setSanPham(sanPham);
            String kichCo = chiTiet.getKichCo() != null ? chiTiet.getKichCo().getTenKichCo() : null;
            String mauSac = chiTiet.getMauSac() != null ? chiTiet.getMauSac().getTenMauSac() : null;
            SanPhamChiTiet existing = sanPhamChiTietService.getChiTietByOptions(sanPham.getId(), kichCo, mauSac);
            if (existing != null && !existing.getId().equals(chiTiet.getId())) {
                throw new IllegalStateException("Kích cỡ và màu sắc đã tồn tại cho sản phẩm này.");
            }
            sanPhamChiTietService.save(chiTiet);
            ra.addFlashAttribute("success", "Lưu chi tiết sản phẩm thành công");
            return "redirect:/admin/product-management?selectedSanPhamId=" + chiTiet.getSanPham().getId();
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lưu chi tiết sản phẩm: " + e.getMessage());
            model.addAttribute("chiTiet", chiTiet);
            model.addAttribute("sanPham", sanPhamService.findById(chiTiet.getSanPham().getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại")));
            List<KichCo> kichCos = kichCoService.getAllKichCos() != null ? kichCoService.getAllKichCos() : Collections.emptyList();
            List<MauSac> mauSacs = mauSacService.getAllMauSacs() != null ? mauSacService.getAllMauSacs() : Collections.emptyList();
            model.addAttribute("kichCos", kichCos);
            model.addAttribute("mauSacs", mauSacs);
            return "admin/product-detail-form";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Integer id, Model model,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            Optional<SanPhamChiTiet> chiTiet = sanPhamChiTietService.findById(id);
            if (chiTiet.isPresent()) {
                SanPhamChiTiet ct = chiTiet.get();
                if (ct.getSoLuong() == null) ct.setSoLuong(0);
                if (ct.getTrangThai() == null) ct.setTrangThai(false);
                model.addAttribute("chiTiet", ct);
                model.addAttribute("sanPham", sanPhamService.findById(ct.getSanPham().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + ct.getSanPham().getId())));
                List<KichCo> kichCos = kichCoService.getAllKichCos() != null ? kichCoService.getAllKichCos() : Collections.emptyList();
                List<MauSac> mauSacs = mauSacService.getAllMauSacs() != null ? mauSacService.getAllMauSacs() : Collections.emptyList();
                model.addAttribute("kichCos", kichCos);
                model.addAttribute("mauSacs", mauSacs);
            } else {
                ra.addFlashAttribute("error", "Chi tiết sản phẩm không tồn tại với ID: " + id);
                return "redirect:/admin/san-pham-chi-tiet";
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi tải form chỉnh sửa: " + e.getMessage());
            return "redirect:/admin/san-pham-chi-tiet";
        }
        return "admin/product-detail-form";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteSanPhamChiTiet(@PathVariable Integer id, Model model,
                                       org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            Optional<SanPhamChiTiet> chiTiet = sanPhamChiTietService.findById(id);
            if (chiTiet.isPresent()) {
                SanPhamChiTiet ct = chiTiet.get();
                ct.setTrangThai(false);
                sanPhamChiTietService.save(ct);
                ra.addFlashAttribute("success", "Đã vô hiệu hóa biến thể sản phẩm");
                return "redirect:/admin/product-management?selectedSanPhamId=" + ct.getSanPham().getId();
            } else {
                ra.addFlashAttribute("error", "Chi tiết sản phẩm không tồn tại với ID: " + id);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi xóa chi tiết sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/san-pham-chi-tiet";
    }
}