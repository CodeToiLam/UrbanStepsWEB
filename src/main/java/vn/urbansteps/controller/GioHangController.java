package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.GioHangService;
import vn.urbansteps.service.TaiKhoanService;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("/gio-hang")
    public String gioHang(Model model, HttpSession session) {
        try {
            GioHang gioHang = null;
            
            // Kiểm tra user đã đăng nhập chưa
            String username = (String) session.getAttribute("username");
            if (username != null) {
                // User đã đăng nhập
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangWithItemsByUserId(taiKhoan.getId());
                }
            } else {
                // Guest user
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangWithItemsBySessionId(sessionId);
            }

            model.addAttribute("gioHang", gioHang);
            return "gio-hang";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi tải giỏ hàng");
            return "gio-hang";
        }
    }

    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Integer sanPhamChiTietId,
            @RequestParam(defaultValue = "1") int soLuong,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== ADD TO CART DEBUG ===");
            System.out.println("San pham chi tiet ID: " + sanPhamChiTietId);
            System.out.println("So luong: " + soLuong);
            System.out.println("Session ID: " + session.getId());
            
            // Kiểm tra user đã đăng nhập chưa
            String username = (String) session.getAttribute("username");
            System.out.println("Username from session: " + username);
            

            GioHang gioHang = null;
            if (username != null) {
                // User đã đăng nhập
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangByUserId(taiKhoan.getId());
                }
            } else {
                // Guest user - giỏ hàng theo session
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangBySessionId(sessionId);
            }

            if (gioHang == null) {
                response.put("success", false);
                response.put("message", "Không thể tạo giỏ hàng");
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("Calling addToCart service...");
            boolean success = gioHangService.addToCart(gioHang, sanPhamChiTietId, soLuong);
            System.out.println("AddToCart result: " + success);
            
            if (success) {
                int cartCount = gioHangService.countItems(gioHang);
                System.out.println("Cart count: " + cartCount);
                response.put("success", true);
                response.put("message", "Thêm sản phẩm vào giỏ hàng thành công");
                response.put("cartCount", cartCount);
            } else {
                System.out.println("ERROR: AddToCart service returned false");
                response.put("success", false);
                response.put("message", "Không thể thêm sản phẩm vào giỏ hàng");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR in addToCart controller:");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi thêm sản phẩm vào giỏ hàng");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer itemId = Integer.parseInt(request.get("itemId").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            
            boolean success = gioHangService.updateQuantity(itemId, quantity);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Cập nhật số lượng thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể cập nhật số lượng");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật số lượng");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/cart/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer itemId = Integer.parseInt(request.get("itemId").toString());
            
            boolean success = gioHangService.removeFromCart(itemId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Xóa sản phẩm thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể xóa sản phẩm");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi xóa sản phẩm");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/cart/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartInfo(HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            GioHang gioHang = null;
            
            // Kiểm tra user đã đăng nhập chưa
            String username = (String) session.getAttribute("username");
            if (username != null) {
                // User đã đăng nhập
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangByUserId(taiKhoan.getId());
                }
            } else {
                // Guest user
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangBySessionId(sessionId);
            }

            if (gioHang != null) {
                response.put("success", true);
                response.put("itemCount", gioHangService.countItems(gioHang));
                response.put("totalAmount", gioHangService.calculateTotal(gioHang));
            } else {
                response.put("success", true);
                response.put("itemCount", 0);
                response.put("totalAmount", 0);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi lấy thông tin giỏ hàng");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/cart/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            GioHang gioHang = null;
            
            // Kiểm tra user đã đăng nhập chưa
            String username = (String) session.getAttribute("username");
            if (username != null) {
                // User đã đăng nhập
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    gioHang = gioHangService.getGioHangByUserId(taiKhoan.getId());
                }
            } else {
                // Guest user
                String sessionId = session.getId();
                gioHang = gioHangService.getGioHangBySessionId(sessionId);
            }

            if (gioHang != null) {
                boolean success = gioHangService.clearCart(gioHang);
                
                if (success) {
                    response.put("success", true);
                    response.put("message", "Xóa giỏ hàng thành công");
                } else {
                    response.put("success", false);
                    response.put("message", "Không thể xóa giỏ hàng");
                }
            } else {
                response.put("success", true);
                response.put("message", "Giỏ hàng đã trống");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi xóa giỏ hàng");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
