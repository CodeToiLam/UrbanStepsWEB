package vn.urbansteps.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.model.HinhAnh;
// removed direct type usage after refactor: KichCo, MauSac
import vn.urbansteps.service.SanPhamService;
import vn.urbansteps.service.SanPhamChiTietService;
import vn.urbansteps.repository.HinhAnhRepository;
import vn.urbansteps.repository.KichCoRepository;
import vn.urbansteps.repository.MauSacRepository;
import vn.urbansteps.repository.HinhAnhSanPhamChiTietRepository;
import vn.urbansteps.repository.HinhAnhSanPhamRepository;
import vn.urbansteps.service.ImageStorageService;
import vn.urbansteps.model.HinhAnhSanPham;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
// removed UUID import

import java.util.Collections;
// removed File import
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private HinhAnhRepository hinhAnhRepository;
    @Autowired
    private KichCoRepository kichCoRepository;
    @Autowired
    private MauSacRepository mauSacRepository;
    @Autowired
    private HinhAnhSanPhamChiTietRepository hinhAnhSanPhamChiTietRepository;
    @Autowired
    private HinhAnhSanPhamRepository hinhAnhSanPhamRepository;
    @Autowired
    private ImageStorageService imageStorageService;
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
            // Tính tổng tồn kho theo sản phẩm để hiển thị ở danh sách
            Map<Integer, Integer> stockTotals = new java.util.HashMap<>();
            for (SanPham p : products) {
                int sum = 0;
                try {
                    List<SanPhamChiTiet> cts = sanPhamChiTietService.getBySanPhamId(p.getId());
                    for (SanPhamChiTiet ct : cts) sum += (ct.getSoLuong() == null ? 0 : ct.getSoLuong());
                } catch (Exception ignored) {}
                stockTotals.put(p.getId(), sum);
            }
            model.addAttribute("stockTotals", stockTotals);
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
        // Bổ sung danh sách kích cỡ và màu sắc để chọn trước khi tạo biến thể
        model.addAttribute("kichCos", kichCoRepository.findAll());
        model.addAttribute("mauSacs", mauSacRepository.findAll());
        return "admin/product-add";
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public String addProduct(@ModelAttribute SanPham sanPham,
                             @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                             @RequestParam(value = "detailImages", required = false) MultipartFile[] detailImages,
                             @RequestParam(value = "preKcIds", required = false) java.util.List<Integer> preKcIds,
                             @RequestParam(value = "preMsIds", required = false) java.util.List<Integer> preMsIds,
                             @RequestParam(value = "defaultVariantQty", required = false) Integer defaultVariantQty,
                             Model model) {
        try {
            // 1) Lưu sản phẩm trước để có ID
            sanPhamService.save(sanPham);

            // 2) Xử lý upload ảnh đại diện nếu có
            if (mainImage != null && !mainImage.isEmpty()) {
                String url = imageStorageService.save(mainImage, "products");
                // Tạo bản ghi HinhAnh và gán cho sản phẩm
                HinhAnh hinh = new HinhAnh();
                hinh.setDuongDan(url);
                hinh.setLaAnhChinh(true);
                hinh = hinhAnhRepository.save(hinh);

                sanPham.setIdHinhAnhDaiDien(hinh);
                sanPhamService.save(sanPham); // cập nhật lại sản phẩm với ảnh đại diện

                // thêm ảnh đại diện vào gallery (tùy chọn)
                HinhAnhSanPham link = new HinhAnhSanPham();
                link.setSanPham(sanPham);
                link.setHinhAnh(hinh);
                link.setLaAnhChinh(true);
                link.setThuTu(0);
                hinhAnhSanPhamRepository.save(link);
            }

            // 2b) Ảnh chi tiết sản phẩm (gallery)
            if (detailImages != null && detailImages.length > 0) {
                System.out.println("[ADD PRODUCT] detailImages length = " + detailImages.length);
                int order = 1; // 0 đã dùng cho ảnh đại diện nếu có
                int saved = 0;
                int idx = 0;
                for (MultipartFile f : detailImages) {
                    if (f == null || f.isEmpty()) { idx++; continue; }
                    String url = imageStorageService.save(f, "products");
                    HinhAnh img = new HinhAnh();
                    img.setDuongDan(url);
                    img.setLaAnhChinh(false);
                    img = hinhAnhRepository.save(img);

                    boolean makePrimary = (sanPham.getIdHinhAnhDaiDien() == null && idx == 0 && (mainImage == null || mainImage.isEmpty()));

                    HinhAnhSanPham link = new HinhAnhSanPham();
                    link.setSanPham(sanPham);
                    link.setHinhAnh(img);
                    link.setLaAnhChinh(makePrimary);
                    link.setThuTu(makePrimary ? 0 : order++);
                    hinhAnhSanPhamRepository.save(link);

                    if (makePrimary) {
                        sanPham.setIdHinhAnhDaiDien(img);
                        sanPhamService.save(sanPham);
                    }
                    saved++;
                    idx++;
                }
                System.out.println("[ADD PRODUCT] saved detail images = " + saved);
            }

            // 3) Nếu admin đã chọn sẵn kích cỡ/màu sắc ở form, tự động tạo các biến thể (soLuong=0 hoặc default)
            if (preKcIds != null && !preKcIds.isEmpty() && preMsIds != null && !preMsIds.isEmpty()) {
                int created = 0;
                int initQty = (defaultVariantQty == null || defaultVariantQty < 0) ? 0 : defaultVariantQty;
                for (Integer kcId : preKcIds) {
                    for (Integer msId : preMsIds) {
                        if (kcId == null || msId == null) continue;
                        // Tránh tạo trùng
                        boolean exists = sanPhamChiTietService
                                .findBySanPhamIdAndKichCoIdAndMauSacId(sanPham.getId(), kcId, msId)
                                .isPresent();
                        if (exists) continue;
                        SanPhamChiTiet ct = new SanPhamChiTiet();
                        ct.setSanPham(sanPham);
                        ct.setKichCo(kichCoRepository.findById(kcId).orElse(null));
                        ct.setMauSac(mauSacRepository.findById(msId).orElse(null));
                        ct.setSoLuong(initQty);
                        ct.setTrangThai(true);
                        sanPhamChiTietService.save(ct);
                        created++;
                    }
                }
                System.out.println("[ADD PRODUCT] auto created variants: " + created);
                // Sau khi tạo biến thể, đồng bộ soLuong tổng = tổng biến thể (nếu có)
                try {
                    List<SanPhamChiTiet> cts = sanPhamChiTietService.getBySanPhamId(sanPham.getId());
                    int sum = 0;
                    for (SanPhamChiTiet ct : cts) sum += ct.getSoLuong() == null ? 0 : ct.getSoLuong();
                    sanPham.setSoLuong(sum);
                    sanPhamService.save(sanPham);
                } catch (Exception ignored) {}
            }

            // Hoàn tất tạo sản phẩm: chuyển hướng sang trang quản lý biến thể riêng
            // Nếu người dùng đã chọn sẵn kích cỡ/màu sắc, đính kèm vào query để tự động chọn
            StringBuilder redirectUrl = new StringBuilder("redirect:/admin/products/")
                    .append(sanPham.getId()).append("/variants");
            java.util.List<String> qs = new java.util.ArrayList<>();
            if (preKcIds != null && !preKcIds.isEmpty()) {
                qs.add("kc=" + preKcIds.stream().map(String::valueOf).reduce((a,b)->a+","+b).orElse(""));
            }
            if (preMsIds != null && !preMsIds.isEmpty()) {
                qs.add("ms=" + preMsIds.stream().map(String::valueOf).reduce((a,b)->a+","+b).orElse(""));
            }
            if (!qs.isEmpty()) {
                redirectUrl.append("?").append(String.join("&", qs));
            }
            return redirectUrl.toString();
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
                model.addAttribute("kichCos", kichCoRepository.findAll());
                model.addAttribute("mauSacs", mauSacRepository.findAll());
                // Load gallery links ordered by thuTu for drag-drop UI
                List<vn.urbansteps.model.HinhAnhSanPham> gallery = hinhAnhSanPhamRepository.findBySanPham_IdOrderByThuTuAsc(id);
                model.addAttribute("gallery", gallery);
            } else {
                model.addAttribute("error", "Sản phẩm không tồn tại");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin sản phẩm: " + e.getMessage());
        }
        return "admin/product-add";
    }

    // Reorder gallery images by link IDs order
    @PostMapping("/products/{id}/gallery/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> reorderGallery(@PathVariable Integer id, @RequestBody Map<String, List<Integer>> body) {
        try {
            List<Integer> ids = body.get("ids");
            if (ids == null) return ResponseEntity.badRequest().body("Missing ids");
            int order = 0;
            for (Integer linkId : ids) {
                Optional<vn.urbansteps.model.HinhAnhSanPham> opt = hinhAnhSanPhamRepository.findById(linkId);
                if (opt.isPresent() && opt.get().getSanPham() != null && opt.get().getSanPham().getId().equals(id)) {
                    vn.urbansteps.model.HinhAnhSanPham link = opt.get();
                    link.setThuTu(order++);
                    hinhAnhSanPhamRepository.save(link);
                }
            }
            Map<String, Object> res = new HashMap<>();
            res.put("status", "ok");
            res.put("count", ids.size());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Set primary image within product gallery and update product's main image
    @PostMapping("/products/{id}/gallery/set-primary")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> setPrimaryGalleryImage(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        try {
            Integer linkId = body.get("linkId");
            if (linkId == null) return ResponseEntity.badRequest().body("Missing linkId");
            Optional<vn.urbansteps.model.HinhAnhSanPham> targetOpt = hinhAnhSanPhamRepository.findById(linkId);
            if (targetOpt.isEmpty() || !targetOpt.get().getSanPham().getId().equals(id)) {
                return ResponseEntity.badRequest().body("Invalid linkId");
            }
            // Unset others
            List<vn.urbansteps.model.HinhAnhSanPham> links = hinhAnhSanPhamRepository.findBySanPham_Id(id);
            for (vn.urbansteps.model.HinhAnhSanPham l : links) {
                l.setLaAnhChinh(l.getId().equals(linkId));
                hinhAnhSanPhamRepository.save(l);
            }
            // Update product's representative image
            SanPham sp = targetOpt.get().getSanPham();
            sp.setIdHinhAnhDaiDien(targetOpt.get().getHinhAnh());
            sanPhamService.save(sp);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Remove image from gallery (only unlink; keep HinhAnh entity intact)
    @DeleteMapping("/products/{id}/gallery/{linkId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteGalleryLink(@PathVariable Integer id, @PathVariable Integer linkId) {
        try {
            Optional<vn.urbansteps.model.HinhAnhSanPham> linkOpt = hinhAnhSanPhamRepository.findById(linkId);
            if (linkOpt.isEmpty() || !linkOpt.get().getSanPham().getId().equals(id)) {
                return ResponseEntity.badRequest().body("Invalid linkId");
            }
            hinhAnhSanPhamRepository.deleteById(linkId);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProduct(@PathVariable Integer id,
                                @ModelAttribute SanPham sanPham,
                                @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                                @RequestParam(value = "detailImages", required = false) MultipartFile[] detailImages,
                                Model model) {
        try {
            // Merge to avoid losing fields not present in form (e.g., main image)
            Optional<SanPham> existingOpt = sanPhamService.findById(id);
            if (existingOpt.isEmpty()) {
                model.addAttribute("error", "Sản phẩm không tồn tại");
                return "admin/product-add";
            }
            SanPham existing = existingOpt.get();

            // Basic fields from form
            existing.setTenSanPham(sanPham.getTenSanPham());
            existing.setMoTa(sanPham.getMoTa());
            existing.setGiaNhap(sanPham.getGiaNhap());
            existing.setGiaBan(sanPham.getGiaBan());
            // Số lượng tổng sản phẩm cấp SP (độc lập với biến thể)
            existing.setSoLuong(sanPham.getSoLuong());
            existing.setTrangThai(sanPham.getTrangThai());
            if (existing.getMaSanPham() == null || existing.getMaSanPham().trim().isEmpty()) {
                existing.setMaSanPham("SP_DEFAULT_" + id);
            }

            sanPhamService.save(existing);

            // Handle new main image upload (if provided)
            if (mainImage != null && !mainImage.isEmpty()) {
                String url = imageStorageService.save(mainImage, "products");
                HinhAnh hinh = new HinhAnh();
                hinh.setDuongDan(url);
                hinh.setLaAnhChinh(true);
                hinh = hinhAnhRepository.save(hinh);

                // Update product's main image
                existing.setIdHinhAnhDaiDien(hinh);
                sanPhamService.save(existing);

                // Insert into gallery and mark as primary
                HinhAnhSanPham link = new HinhAnhSanPham();
                link.setSanPham(existing);
                link.setHinhAnh(hinh);
                link.setLaAnhChinh(true);
                link.setThuTu(0);
                hinhAnhSanPhamRepository.save(link);

                // Unset other gallery links primary flag
                List<vn.urbansteps.model.HinhAnhSanPham> links = hinhAnhSanPhamRepository.findBySanPham_Id(id);
                for (vn.urbansteps.model.HinhAnhSanPham l : links) {
                    if (!l.getId().equals(link.getId())) {
                        l.setLaAnhChinh(false);
                        hinhAnhSanPhamRepository.save(l);
                    }
                }
            }

            // Handle new detail images upload (append)
            if (detailImages != null && detailImages.length > 0) {
                int order = 1;
                List<vn.urbansteps.model.HinhAnhSanPham> current = hinhAnhSanPhamRepository.findBySanPham_IdOrderByThuTuAsc(id);
                if (!current.isEmpty()) {
                    Integer max = current.stream().map(vn.urbansteps.model.HinhAnhSanPham::getThuTu).filter(java.util.Objects::nonNull).max(Integer::compareTo).orElse(0);
                    order = max + 1;
                }
                for (MultipartFile f : detailImages) {
                    if (f == null || f.isEmpty()) continue;
                    String url = imageStorageService.save(f, "products");
                    HinhAnh img = new HinhAnh();
                    img.setDuongDan(url);
                    img.setLaAnhChinh(false);
                    img = hinhAnhRepository.save(img);

                    HinhAnhSanPham link = new HinhAnhSanPham();
                    link.setSanPham(existing);
                    link.setHinhAnh(img);
                    link.setLaAnhChinh(false);
                    link.setThuTu(order++);
                    hinhAnhSanPhamRepository.save(link);
                }
            }
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

    // Variants management page for a specific product (bulk create like big platforms)
    @GetMapping("/products/{id}/variants")
    @PreAuthorize("hasRole('ADMIN')")
    public String showVariantsPage(@PathVariable Integer id, Model model) {
        try {
            SanPham sp = sanPhamService.findById(id).orElse(null);
            if (sp == null) {
                model.addAttribute("error", "Sản phẩm không tồn tại");
                return "redirect:/admin/products";
            }
            model.addAttribute("sanPham", sp);
            model.addAttribute("kichCos", kichCoRepository.findAll());
            model.addAttribute("mauSacs", mauSacRepository.findAll());
            return "admin/product-variants";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    // Bulk create variants: rows of kcId+msId+qty+price and multiple images per row
    @PostMapping("/products/{id}/variants/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public String bulkCreateVariants(@PathVariable Integer id,
                                     @RequestParam("rowCount") Integer rowCount,
                                     @RequestParam("rowKcIds") List<Integer> rowKcIds,
                                     @RequestParam("rowMsIds") List<Integer> rowMsIds,
                                     @RequestParam("rowQtys") List<Integer> rowQtys,
                                     @RequestParam(value = "rowPrices", required = false) List<java.math.BigDecimal> rowPrices,
                                     @RequestParam Map<String, Object> params,
                                     org.springframework.web.multipart.MultipartHttpServletRequest request) {
        try {
            SanPham sp = sanPhamService.findById(id).orElse(null);
            if (sp == null) return "redirect:/admin/products";

            for (int i = 0; i < rowCount; i++) {
                Integer kcId = rowKcIds.get(i);
                Integer msId = rowMsIds.get(i);
                Integer qty = rowQtys.get(i);
                java.math.BigDecimal price = (rowPrices != null && rowPrices.size() > i) ? rowPrices.get(i) : null;

                // Nếu biến thể đã tồn tại => cập nhật số lượng và giá như các nền tảng lớn
                java.util.Optional<SanPhamChiTiet> existedOpt = sanPhamChiTietService
                        .findBySanPhamIdAndKichCoIdAndMauSacId(sp.getId(), kcId, msId);
                SanPhamChiTiet ct;
                if (existedOpt.isPresent()) {
                    ct = existedOpt.get();
                    ct.setSoLuong(qty == null ? (ct.getSoLuong() == null ? 0 : ct.getSoLuong()) : qty);
                    if (price != null) {
                        // dùng updatePrice để lưu lịch sử giá cũ
                        ct.updatePrice(price);
                    }
                    ct.setTrangThai(true);
                    ct = sanPhamChiTietService.save(ct);
                } else {
                    ct = new SanPhamChiTiet();
                    ct.setSanPham(sp);
                    ct.setKichCo(kichCoRepository.findById(kcId).orElse(null));
                    ct.setMauSac(mauSacRepository.findById(msId).orElse(null));
                    ct.setSoLuong(qty == null ? 0 : qty);
                    if (price != null) ct.setGiaBanLe(price);
                    ct.setTrangThai(true);
                    ct = sanPhamChiTietService.save(ct);
                }

                // handle images for this row: input name rowImages_i
                String field = "rowImages_" + i;
                if (request != null) {
                    List<org.springframework.web.multipart.MultipartFile> files = request.getFiles(field);
                    int vOrder = 0;
                    for (org.springframework.web.multipart.MultipartFile f : files) {
                        if (f == null || f.isEmpty()) continue;
                        String url = imageStorageService.save(f, "variants");
                        HinhAnh img = new HinhAnh();
                        img.setDuongDan(url);
                        img.setLaAnhChinh(vOrder == 0);
                        img = hinhAnhRepository.save(img);

                        vn.urbansteps.model.HinhAnh_SanPhamChiTiet link = new vn.urbansteps.model.HinhAnh_SanPhamChiTiet();
                        link.setSanPhamChiTiet(ct);
                        link.setHinhAnh(img);
                        hinhAnhSanPhamChiTietRepository.save(link);
                        vOrder++;
                    }
                }
            }
            return "redirect:/admin/products/edit/" + id;
        } catch (Exception e) {
            return "redirect:/admin/products/edit/" + id + "?error=true";
        }
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