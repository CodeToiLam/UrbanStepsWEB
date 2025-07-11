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
import vn.urbansteps.service.ImageService;
import vn.urbansteps.service.SanPhamChiTietService;
import vn.urbansteps.service.SanPhamService;

import java.util.*;

@Controller
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private SanPhamChiTietService sanPhamChiTietService;
    @Autowired
    private HinhAnhService hinhAnhService;
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
    public String chiTiet(@PathVariable Integer id, Model model) {
        try {
            Optional<SanPham> product = sanPhamService.findById(id);

            if (product.isPresent()) {
                SanPham sanPham = product.get();
                
                // Xử lý ảnh đại diện
                imageService.processProductImage(sanPham);
                
                model.addAttribute("product", sanPham);

                // Log thông tin sản phẩm để kiểm tra
                System.out.println("Hiển thị chi tiết sản phẩm ID: " + id + ", Tên: " + sanPham.getTenSanPham());

                // Tạo gallery ảnh đơn giản
                List<HinhAnh> productImages = imageService.createSimpleGallery(sanPham);
                model.addAttribute("productImages", productImages);
                
                System.out.println("Đã tạo gallery với " + productImages.size() + " ảnh cho sản phẩm ID: " + id);

                try {
                    // Lấy thông tin chi tiết sản phẩm - đặt trong try-catch riêng
                    List<String> kichCos = sanPhamChiTietService.getKichCosBySanPhamId(id);
                    List<String> mauSacs = sanPhamChiTietService.getMauSacsBySanPhamId(id);
                    Map<String, Map<String, Integer>> tonKhoMap = sanPhamChiTietService.getTonKhoBySanPhamId(id);
                    
                    // Lấy danh sách variants để truyền vào JavaScript
                    List<SanPhamChiTiet> variants = sanPhamChiTietService.getBySanPhamId(id);

                    // Tạo danh sách variants đơn giản cho JavaScript (tránh lỗi Jackson serialization)
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
