package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.urbansteps.model.HinhAnh;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.service.HinhAnhService;
import vn.urbansteps.service.SanPhamChiTietService;
import vn.urbansteps.service.SanPhamService;

import java.util.*;

@Controller
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;

    private HinhAnhService hinhAnhService = new HinhAnhService();

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
        try {
            Optional<SanPham> product = sanPhamService.findById(id);

            if (product.isPresent()) {
                SanPham sanPham = product.get();
                model.addAttribute("product", sanPham);

                // Log thông tin sản phẩm để kiểm tra
                System.out.println("Hiển thị chi tiết sản phẩm ID: " + id + ", Tên: " + sanPham.getTenSanPham());

                // Lấy tất cả hình ảnh của sản phẩm
                List<HinhAnh> productImages = hinhAnhService.getAllImagesBySanPhamId(id);
                model.addAttribute("productImages", productImages);

                // Thêm debug cho ảnh sản phẩm
                if (sanPham.getIdHinhAnhDaiDien() != null) {
                    String rawPath = sanPham.getIdHinhAnhDaiDien().getDuongDan();
                    System.out.println("Đường dẫn ảnh gốc: " + rawPath);

                    // Xử lý đường dẫn ảnh để đảm bảo hiển thị đúng
                    String cleanPath = rawPath;
                    if (rawPath.startsWith("images/")) {
                        cleanPath = rawPath.substring(7);
                    }
                    System.out.println("Đường dẫn ảnh đã xử lý: /images/" + cleanPath);

                    // Thêm thông tin đường dẫn ảnh vào model để debug trong template
                    model.addAttribute("imagePath", "/images/" + cleanPath);
                } else {
                    System.out.println("CẢNH BÁO: Sản phẩm không có ảnh đại diện!");
                    model.addAttribute("imagePath", "/images/no-image.jpg");
                }

                try {
                    // Lấy thông tin chi tiết sản phẩm - đặt trong try-catch riêng
                    List<String> kichCos = sanPhamChiTietService.getKichCosBySanPhamId(id);
                    List<String> mauSacs = sanPhamChiTietService.getMauSacsBySanPhamId(id);
                    Map<String, Map<String, Integer>> tonKhoMap = sanPhamChiTietService.getTonKhoBySanPhamId(id);

                    System.out.println("Đã lấy thông tin chi tiết sản phẩm: " +
                            kichCos.size() + " kích cỡ, " +
                            mauSacs.size() + " màu sắc");

                    model.addAttribute("kichCos", kichCos);
                    model.addAttribute("mauSacs", mauSacs);
                    model.addAttribute("tonKhoMap", tonKhoMap);
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage());
                    e.printStackTrace();

                    // Đặt giá trị mặc định để tránh lỗi ở template
                    model.addAttribute("kichCos", new ArrayList<>());
                    model.addAttribute("mauSacs", new ArrayList<>());
                    model.addAttribute("tonKhoMap", new HashMap<>());
                }

                // Lấy sản phẩm liên quan
                try {
                    List<SanPham> relatedProducts = sanPhamService.getRelatedProducts(id);

                    // Debug thông tin sản phẩm liên quan
                    System.out.println("Đã lấy " + relatedProducts.size() + " sản phẩm liên quan");
                    for (SanPham related : relatedProducts) {
                        if (related.getIdHinhAnhDaiDien() != null) {
                            System.out.println("  SP liên quan: " + related.getTenSanPham() +
                                    ", ảnh: " + related.getIdHinhAnhDaiDien().getDuongDan());
                        } else {
                            System.out.println("  SP liên quan: " + related.getTenSanPham() +
                                    ", không có ảnh");
                        }
                    }

                    model.addAttribute("relatedProducts", relatedProducts);
                } catch (Exception e) {
                    System.err.println("Lỗi khi lấy sản phẩm liên quan: " + e.getMessage());
                    e.printStackTrace();
                    model.addAttribute("relatedProducts", new ArrayList<>());
                }

                // Thêm xử lý đường dẫn ảnh
                model.addAttribute("imageHelper", new ImagePathHelper());

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

    public static class ImagePathHelper {
        public String getCleanPath(String originalPath) {
            if (originalPath == null) return "/images/no-image.jpg";

            if (originalPath.startsWith("/")) {
                originalPath = originalPath.substring(1);
            }

            if (originalPath.startsWith("images/")) {
                return "/images/" + originalPath.substring(7);
            } else {
                return "/images/" + originalPath;
            }
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
