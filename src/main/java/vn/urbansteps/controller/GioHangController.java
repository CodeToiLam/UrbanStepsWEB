package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.dto.CartOperationResult;
import vn.urbansteps.service.GioHangService;
import vn.urbansteps.service.TaiKhoanService;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
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
            
            // Đảm bảo session có ID và ổn định
            session.setAttribute("dummy", "ensure-session-created");
            System.out.println("Session ID trong gioHang: " + session.getId());
            
            // Kiểm tra user đã đăng nhập chưa
            String username = (String) session.getAttribute("username");
            if (username == null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                    username = auth.getName();
                    session.setAttribute("username", username);
                }
            }
            if (username != null) {
                // User đã đăng nhập
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    System.out.println("User đã đăng nhập với ID: " + taiKhoan.getId());
                    gioHang = gioHangService.getGioHangWithItemsByUserId(taiKhoan.getId());
                    
                    // Kiểm tra có giỏ hàng từ session trước khi đăng nhập không
                    String previousSessionId = (String) session.getAttribute("cart_session_id");
                    if (previousSessionId != null) {
                        System.out.println("Hợp nhất giỏ hàng từ session cũ: " + previousSessionId);
                        gioHangService.mergeSessionCartToUserCart(previousSessionId, taiKhoan.getId());
                        session.removeAttribute("cart_session_id");
                        
                        // Lấy giỏ hàng sau khi hợp nhất
                        gioHang = gioHangService.getGioHangWithItemsByUserId(taiKhoan.getId());
                    }
                }
            } else {
                // Guest user
                String sessionId = session.getId();
                System.out.println("Guest với sessionId: " + sessionId);
                session.setAttribute("cart_session_id", sessionId);
                gioHang = gioHangService.getGioHangWithItemsBySessionId(sessionId);
            }
            
            // Nếu vẫn không có giỏ hàng, tạo một giỏ hàng trống
            if (gioHang == null) {
                System.out.println("Tạo giỏ hàng trống mới");
                gioHang = new GioHang();
                gioHang.setItems(new java.util.ArrayList<>());
            }

            model.addAttribute("gioHang", gioHang);
            return "gio-hang";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi tải giỏ hàng: " + e.getMessage());
            
            // Khi có lỗi, tạo giỏ hàng trống để trang vẫn hiển thị
            GioHang emptyCart = new GioHang();
            emptyCart.setItems(new java.util.ArrayList<>());
            model.addAttribute("gioHang", emptyCart);
            
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
            // Đảm bảo session có ID và ổn định
            session.setAttribute("dummy", "ensure-session-created");
            
            System.out.println("=== ADD TO CART DEBUG ===");
            System.out.println("San pham chi tiet ID: " + sanPhamChiTietId);
            System.out.println("So luong: " + soLuong);
            System.out.println("Session ID: " + session.getId());
            
            // Kiểm tra user đã đăng nhập chưa
            String username = (String) session.getAttribute("username");
            if (username == null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                    username = auth.getName();
                    session.setAttribute("username", username);
                }
            }
            GioHang gioHang = null;
            if (username != null) {
                // User đã đăng nhập
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                if (taiKhoan != null) {
                    System.out.println("Đang tìm giỏ hàng cho user ID: " + taiKhoan.getId());
                    gioHang = gioHangService.getGioHangByUserId(taiKhoan.getId());
                }
            } else {
                // Guest user - giỏ hàng theo session
                String sessionId = session.getId();
                // Lưu sessionId để sử dụng sau
                session.setAttribute("cart_session_id", sessionId);
                System.out.println("Using session ID for cart: " + sessionId);
                gioHang = gioHangService.getGioHangBySessionId(sessionId);
            }

            if (gioHang == null) {
                response.put("success", false);
                response.put("message", "Không thể tạo giỏ hàng");
                return ResponseEntity.badRequest().body(response);
            }

            System.out.println("Calling addToCart service...");
            CartOperationResult result = gioHangService.addToCart(gioHang, sanPhamChiTietId, soLuong);
            System.out.println("AddToCart result success: " + result.isSuccess());
            
                if (result.isSuccess()) {
                    // Lưu session ID cho cart
                    if (username == null) {
                        session.setAttribute("cart_session_id", session.getId());
                    }
                    
                    response.put("success", true);
                    response.put("message", "Thêm sản phẩm vào giỏ hàng thành công");
                    response.put("cartCount", result.getCartCount());
                    response.put("cartTotal", result.getCartTotal());
                } else {
                    System.out.println("ERROR: AddToCart service returned: " + result.getMessage());
                    response.put("success", false);
                    response.put("message", result.getMessage());
                    response.put("errorType", result.getErrorType().name());
                    if (result.getRelatedProductId() != null) {
                        response.put("productId", result.getRelatedProductId());
                    }
                }            return ResponseEntity.ok(response);
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
            
            CartOperationResult result = gioHangService.updateQuantity(itemId, quantity);
            
            if (result.isSuccess()) {
                response.put("success", true);
                response.put("message", "Cập nhật số lượng thành công");
                response.put("cartTotal", result.getCartTotal());
                response.put("cartCount", result.getCartCount());
            } else {
                response.put("success", false);
                response.put("message", result.getMessage());
                response.put("errorType", result.getErrorType().name());
                if (result.getRelatedProductId() != null) {
                    response.put("productId", result.getRelatedProductId());
                }
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
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer itemId = Integer.parseInt(request.get("itemId").toString());
            
            CartOperationResult result = gioHangService.removeFromCart(itemId);
            
            if (result.isSuccess()) {
                response.put("success", true);
                response.put("message", "Sản phẩm đã được xóa khỏi giỏ hàng");
                response.put("cartTotal", result.getCartTotal());
                response.put("cartCount", result.getCartCount());
            } else {
                response.put("success", false);
                response.put("message", result.getMessage());
                response.put("errorType", result.getErrorType().name());
                if (result.getRelatedProductId() != null) {
                    response.put("productId", result.getRelatedProductId());
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi xóa sản phẩm");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/api/cart/update-quantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartQuantity(
            @RequestParam Integer gioHangChiTietId,
            @RequestParam int soLuong,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== UPDATE CART DEBUG ===");
            System.out.println("GioHangChiTiet ID: " + gioHangChiTietId);
            System.out.println("So luong moi: " + soLuong);
            
            CartOperationResult result = gioHangService.updateQuantity(gioHangChiTietId, soLuong);
            
            if (result.isSuccess()) {
                // Get item total
                BigDecimal itemTotal = gioHangService.getItemTotal(gioHangChiTietId);
                
                response.put("success", true);
                response.put("itemTotal", itemTotal.doubleValue());
                response.put("cartTotal", result.getCartTotal().doubleValue());
                response.put("cartCount", result.getCartCount());
                response.put("message", "Cập nhật thành công");
            } else {
                response.put("success", false);
                response.put("message", result.getMessage());
                response.put("errorType", result.getErrorType().name());
                if (result.getRelatedProductId() != null) {
                    response.put("productId", result.getRelatedProductId());
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi cập nhật");
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
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
    
    @GetMapping("/api/cart/get")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            GioHang gioHang = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;
            
            // Kiểm tra user đã đăng nhập chưa
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
            
            if (gioHang == null) {
                // Return empty cart data
                response.put("success", true);
                response.put("cartCount", 0);
                response.put("cartTotal", 0);
                response.put("items", java.util.Collections.emptyList());
            } else {
                response.put("success", true);
                response.put("cartCount", gioHang.getItems().size());
                response.put("cartTotal", gioHang.getTongTien());
                
                // Format items for the response
                java.util.List<Map<String, Object>> formattedItems = new java.util.ArrayList<>();
                for (var item : gioHang.getItems()) {
                    Map<String, Object> formattedItem = new HashMap<>();
                    formattedItem.put("id", item.getId());
                    formattedItem.put("quantity", item.getSoLuong());
                    formattedItem.put("price", item.getGiaTaiThoidiem());
                    formattedItem.put("total", item.getTongGia());
                    formattedItems.add(formattedItem);
                }
                
                response.put("items", formattedItems);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi lấy dữ liệu giỏ hàng");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
