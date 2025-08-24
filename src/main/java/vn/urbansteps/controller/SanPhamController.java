package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import vn.urbansteps.model.HinhAnh;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.service.ImageService;
import vn.urbansteps.service.SanPhamChiTietService;
import vn.urbansteps.service.SanPhamService;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private vn.urbansteps.service.TaiKhoanService taiKhoanService;
    @Autowired
    private vn.urbansteps.repository.KhachHangRepository khachHangRepository;
    @Autowired
    private vn.urbansteps.repository.HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private ImageService imageService;

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
    public String chiTiet(@PathVariable Integer id, Model model, HttpSession session) {
        try {
            Optional<SanPham> product = sanPhamService.findById(id);

            if (product.isPresent()) {
                SanPham sanPham = product.get();
                
                // Xử lý ảnh đại diện
                imageService.processProductImage(sanPham);

                // Phân tích URL các sàn bán hàng từ phần mô tả
                // Định dạng hỗ trợ:
                //   - Dòng bắt đầu bằng: "Shopee: <url>", "Lazada: <url>", "Facebook: <url>"
                //   - Hoặc bất kỳ URL nào chứa các domain trên
                try {
                    String desc = sanPham.getMoTa();
                    if (desc != null) {
                        // 1) Dòng có tiền tố (keyed lines)
                        String[] lines = desc.split("\n");
                        for (String line : lines) {
                            String l = line.trim();
                            String lower = l.toLowerCase();
                            if (lower.startsWith("shopee:")) {
                                String url = l.substring(7).trim();
                                if (!url.isEmpty()) sanPham.setShopeeUrl(url);
                            } else if (lower.startsWith("lazada:")) {
                                String url = l.substring(7).trim();
                                if (!url.isEmpty()) sanPham.setLazadaUrl(url);
                            } else if (lower.startsWith("facebook:")) {
                                String url = l.substring(9).trim();
                                if (!url.isEmpty()) sanPham.setFacebookShopUrl(url);
                            }
                        }

                        // 2) Dự phòng: dò tìm bất kỳ URL nào thuộc domain đã biết
                        if (sanPham.getShopeeUrl() == null || sanPham.getLazadaUrl() == null || sanPham.getFacebookShopUrl() == null) {
                            java.util.regex.Matcher matcher = java.util.regex.Pattern
                                    .compile("(https?://[^\\s]+)")
                                    .matcher(desc);
                            while (matcher.find()) {
                                String url = matcher.group(1);
                                String lowerUrl = url.toLowerCase();
                                if (sanPham.getShopeeUrl() == null && (lowerUrl.contains("shopee.vn") || lowerUrl.contains("shopee.com"))) {
                                    sanPham.setShopeeUrl(url);
                                } else if (sanPham.getLazadaUrl() == null && lowerUrl.contains("lazada")) {
                                    sanPham.setLazadaUrl(url);
                                } else if (sanPham.getFacebookShopUrl() == null && (lowerUrl.contains("facebook.com") || lowerUrl.contains("fb.com"))) {
                                    sanPham.setFacebookShopUrl(url);
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}
                
                model.addAttribute("product", sanPham);

                // Ghi log thông tin sản phẩm để kiểm tra
                System.out.println("Hiển thị chi tiết sản phẩm ID: " + id + ", Tên: " + sanPham.getTenSanPham());

                // Tạo gallery ảnh đơn giản
                List<HinhAnh> productImages = imageService.createSimpleGallery(sanPham);
                model.addAttribute("productImages", productImages);

                System.out.println("Đã tạo gallery với " + productImages.size() + " ảnh cho sản phẩm ID: " + id);

                try {
                    // Lấy thông tin chi tiết sản phẩm - đặt trong khối try-catch riêng
                    List<String> kichCos = sanPhamChiTietService.getKichCosBySanPhamId(id);
                    List<String> mauSacs = sanPhamChiTietService.getMauSacsBySanPhamId(id);
                    Map<String, Map<String, Integer>> tonKhoMap = sanPhamChiTietService.getTonKhoBySanPhamId(id);
                    
                    // Lấy danh sách variants để truyền cho JavaScript
                    List<SanPhamChiTiet> variants = sanPhamChiTietService.getBySanPhamId(id);

                    // Tạo danh sách variants đơn giản cho JavaScript (tránh lỗi khi Jackson serialize)
                    List<Map<String, Object>> variantsForJS = new ArrayList<>();
                    for (SanPhamChiTiet variant : variants) {
                        Map<String, Object> variantMap = new HashMap<>();
                        variantMap.put("id", variant.getId());
                        variantMap.put("kichCo", variant.getKichCo());
                        variantMap.put("mauSac", variant.getMauSac());
                        variantMap.put("soLuong", variant.getSoLuong());
                        variantsForJS.add(variantMap);
                    }

                    System.out.println("Đã lấy thông tin chi tiết sản phẩm: " +
                            kichCos.size() + " kích cỡ, " +
                            mauSacs.size() + " màu sắc, " +
                            variants.size() + " variants");

                    model.addAttribute("kichCos", kichCos);
                    model.addAttribute("mauSacs", mauSacs);
                    model.addAttribute("tonKhoMap", tonKhoMap);
                    model.addAttribute("variants", variantsForJS);
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage());
                    e.printStackTrace();

                    // Đặt giá trị mặc định để tránh lỗi ở template
                    model.addAttribute("kichCos", new ArrayList<>());
                    model.addAttribute("mauSacs", new ArrayList<>());
                    model.addAttribute("tonKhoMap", new HashMap<>());
                    model.addAttribute("variants", new ArrayList<>());
                }

                // Lấy sản phẩm liên quan
                try {
                    List<SanPham> relatedProducts = sanPhamService.getRelatedProducts(id);
                    
                    // Xử lý ảnh cho sản phẩm liên quan
                    imageService.processProductListImages(relatedProducts);

                    // Debug thông tin sản phẩm liên quan
                    System.out.println("Đã lấy và xử lý ảnh cho " + relatedProducts.size() + " sản phẩm liên quan");

                    model.addAttribute("relatedProducts", relatedProducts);
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy sản phẩm liên quan: " + e.getMessage());
                    e.printStackTrace();
                    model.addAttribute("relatedProducts", new ArrayList<>());
                }

                // Ghi lại danh sách sản phẩm vừa xem trong session (tối đa 8)
                try {
                    @SuppressWarnings("unchecked")
                    java.util.LinkedList<Integer> recent = (java.util.LinkedList<Integer>) session.getAttribute("recentlyViewed");
                    if (recent == null) recent = new java.util.LinkedList<>();
                    recent.remove(id);
                    recent.addFirst(id);
                    while (recent.size() > 8) recent.removeLast();
                    session.setAttribute("recentlyViewed", recent);
                } catch (Exception ignored) {}

                // Sinh gợi ý: khi đã đăng nhập ưu tiên sản phẩm đã mua cùng thương hiệu;
                // nếu không thì hiển thị sản phẩm HOT. Nếu không có mua hàng thì dùng sản phẩm đã xem, sau đó mới đến HOT.
                try {
                    // xác định username (từ session hoặc SecurityContext)
                    String username = (String) session.getAttribute("username");
                    if (username == null) {
                        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                            username = auth.getName();
                            session.setAttribute("username", username);
                        }
                    }

                    List<SanPham> recs = new ArrayList<>();

                    if (username != null) {
                        // đã đăng nhập: lấy KhachHang và các mục đã mua
                        vn.urbansteps.model.TaiKhoan tk = taiKhoanService.findByTaiKhoan(username);
                        if (tk != null) {
                            var khOpt = khachHangRepository.findByTaiKhoan(tk);
                            if (khOpt.isPresent()) {
                                Integer khId = khOpt.get().getId();
                                List<vn.urbansteps.model.HoaDonChiTiet> purchases = hoaDonChiTietRepository.findByCustomerId(khId);
                                // thu thập sản phẩm đã mua có cùng thương hiệu với sản phẩm hiện tại
                                if (purchases != null && !purchases.isEmpty() && sanPham.getThuongHieu() != null) {
                                    java.util.Set<Integer> seen = new java.util.LinkedHashSet<>();
                                    for (vn.urbansteps.model.HoaDonChiTiet hdct : purchases) {
                                        try {
                                            var sp = hdct.getSanPhamChiTiet() != null ? hdct.getSanPhamChiTiet().getSanPham() : null;
                                            if (sp != null && sp.getId() != null && !sp.getId().equals(id)
                                                    && sp.getThuongHieu() != null
                                                    && sp.getThuongHieu().getId().equals(sanPham.getThuongHieu().getId())) {
                                                seen.add(sp.getId());
                                            }
                                            if (seen.size() >= 8) break;
                                        } catch (Exception ignored) {}
                                    }
                                    if (!seen.isEmpty()) {
                                        recs = sanPhamService.getAllProducts().stream()
                                                .filter(sp -> seen.contains(sp.getId()))
                                                .limit(8)
                                                .collect(java.util.stream.Collectors.toList());
                                    }
                                }
                            }
                        }
                    }

                    // Nếu vẫn rỗng thì trực tiếp dùng sản phẩm HOT (bỏ fallback recent view)
                    if (recs.isEmpty()) {
                        recs = sanPhamService.getHotProducts();
                        // remove current product if present and limit to 8
                        recs = recs.stream().filter(sp -> !sp.getId().equals(id)).limit(8).collect(java.util.stream.Collectors.toList());
                    }

                    imageService.processProductListImages(recs);
                    model.addAttribute("recommendedProducts", recs);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tạo gợi ý sản phẩm: " + e.getMessage());
                    e.printStackTrace();
                    model.addAttribute("recommendedProducts", new ArrayList<>());
                }

                return "san-pham/chi-tiet";
            } else {
                System.out.println("Không tìm thấy sản phẩm với ID: " + id);
                return "redirect:/san-pham";
            }
        } catch (Exception e) {
            System.err.println("Lỗi tổng thể trong controller chi tiết sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/san-pham";
        }
    }

    // API để lấy số lượng tồn kho dựa trên kích cỡ và màu sắc được chọn
    @GetMapping("/api/san-pham/{id}/ton-kho")
    @ResponseBody
    public ResponseEntity<Integer> getProductStock(
            @PathVariable Integer id,
            @RequestParam String kichCo,
            @RequestParam String mauSac) {

        SanPhamChiTiet chiTiet = sanPhamChiTietService.getChiTietByOptions(id, kichCo, mauSac);

        if (chiTiet != null) {
            return ResponseEntity.ok(chiTiet.getSoLuong());
        } else {
            return ResponseEntity.ok(0);
        }
    }

}
