package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.ReturnRequestItem;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.repository.ReturnRequestRepository;
import vn.urbansteps.service.ReturnRequestService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
public class ReturnRequestController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private ReturnRequestService returnRequestService;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    // Hiển thị form yêu cầu trả hàng
    @GetMapping("/don-hang/{orderId}/return-request")
    public String showReturnRequestForm(@PathVariable Integer orderId, Model model, 
                                       RedirectAttributes redirectAttributes, Locale locale) {
        try {
            HoaDon order = hoaDonService.getOrderByIdWithDetails(orderId);
            if (order == null) {
                String errorMsg = messageSource.getMessage("order.not.found", null, "Không tìm thấy đơn hàng!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang";
            }
            
            // Chỉ cho phép trả hàng khi đơn hàng đã hoàn thành
            if (order.getTrangThai() != 3) {
                String errorMsg = messageSource.getMessage("return.order.status.error", null, 
                    "Chỉ có thể yêu cầu trả hàng khi đơn hàng đã hoàn thành!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/chi-tiet/" + orderId;
            }
            
            // Kiểm tra xem đơn hàng này đã có yêu cầu trả hàng đang chờ xử lý chưa
            long pendingCount = returnRequestRepository.countByOrderIdAndStatus(orderId, vn.urbansteps.model.ReturnRequest.Status.NEW)
                + returnRequestRepository.countByOrderIdAndStatus(orderId, vn.urbansteps.model.ReturnRequest.Status.PROCESSING);
            if (pendingCount > 0) {
                String errorMsg = messageSource.getMessage("return.request.duplicate", null,
                    "Đơn hàng này đã có yêu cầu trả/đổi đang được xử lý.", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/chi-tiet/" + orderId;
            }

            model.addAttribute("order", order);
            
            String title = messageSource.getMessage("return.request.title", null, "Yêu cầu trả hàng", locale) 
                         + " - " + messageSource.getMessage("order.code", null, "Đơn", locale) + " " + order.getMaHoaDon();
            model.addAttribute("title", title);
            
            return "return-request";
        } catch (Exception e) {
            String errorMsg = messageSource.getMessage("system.error", new Object[]{e.getMessage()}, 
                "Có lỗi xảy ra: " + e.getMessage(), locale);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/don-hang";
        }
    }

    // Xử lý submit form yêu cầu trả hàng
    @PostMapping("/don-hang/{orderId}/return-request")
    public String submitReturnRequest(
            @PathVariable Integer orderId,
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam(required = false) String returnType,
            @RequestParam String lyDo,
            @RequestParam(required = false) String ghiChu,
            @RequestParam(value = "returnItems", required = false) List<Integer> returnItemIds,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        try {
            HoaDon order = hoaDonService.getOrderById(orderId);
            if (order == null) {
                String errorMsg = messageSource.getMessage("order.not.found", null, "Không tìm thấy đơn hàng!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang";
            }

            // Kiểm tra trạng thái đơn hàng
            if (order.getTrangThai() != 3) {
                String errorMsg = messageSource.getMessage("return.order.status.error", null, 
                    "Chỉ có thể yêu cầu trả hàng khi đơn hàng đã hoàn thành!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/chi-tiet/" + orderId;
            }

            // Kiểm tra có sản phẩm được chọn không
            if (returnItemIds == null || returnItemIds.isEmpty()) {
                String errorMsg = messageSource.getMessage("return.products.required", null, 
                    "Vui lòng chọn ít nhất một sản phẩm để trả hàng!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/" + orderId + "/return-request";
            }
            
            // Kiểm tra loại yêu cầu
            if (returnType == null || returnType.trim().isEmpty()) {
                String errorMsg = messageSource.getMessage("return.type.required", null, 
                    "Vui lòng chọn loại yêu cầu!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/" + orderId + "/return-request";
            }
            
            // Kiểm tra lý do
            if (lyDo == null || lyDo.trim().length() < 10) {
                String errorMsg = messageSource.getMessage("return.reason.invalid", null, 
                    "Lý do trả hàng phải có ít nhất 10 ký tự!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/" + orderId + "/return-request";
            }

            // Xử lý upload ảnh với validation cải thiện
            List<String> imageUrls = new ArrayList<>();
            if (images != null && !images.isEmpty()) {
                // Kiểm tra số lượng ảnh tối đa 5
                long validImageCount = images.stream().filter(img -> !img.isEmpty()).count();
                if (validImageCount > 5) {
                    String errorMsg = messageSource.getMessage("return.image.max.error", null,
                            "Chỉ có thể tải lên tối đa 5 hình ảnh", locale);
                    redirectAttributes.addFlashAttribute("error", errorMsg);
                    return "redirect:/don-hang/" + orderId + "/return-request";
                }
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        try {
                            // Kiểm tra kích thước file (5MB)
                            if (image.getSize() > 5 * 1024 * 1024) {
                                String errorMsg = messageSource.getMessage("return.image.size.error", null, 
                                    "Kích thước file ảnh không được vượt quá 5MB!", locale);
                                redirectAttributes.addFlashAttribute("error", errorMsg);
                                return "redirect:/don-hang/" + orderId + "/return-request";
                            }
                            
                            // Kiểm tra content type
                            String contentType = image.getContentType();
                            if (contentType == null || !isValidImageType(contentType)) {
                                String errorMsg = messageSource.getMessage("return.image.invalid.error", null,
                                        "Chỉ chấp nhận file hình ảnh (JPG, PNG, GIF)", locale);
                                redirectAttributes.addFlashAttribute("error", errorMsg);
                                return "redirect:/don-hang/" + orderId + "/return-request";
                            }
                            
                            String imagePath = saveImage(image);
                            imageUrls.add(imagePath);
                        } catch (IOException e) {
                            String errorMsg = messageSource.getMessage("return.image.upload.error", 
                                new Object[]{e.getMessage()}, "Lỗi khi tải lên hình ảnh: " + e.getMessage(), locale);
                            redirectAttributes.addFlashAttribute("error", errorMsg);
                            return "redirect:/don-hang/" + orderId + "/return-request";
                        }
                    }
                }
            }

            // Tạo danh sách sản phẩm trả hàng với validation chi tiết (trả toàn bộ số lượng đã mua)
            List<ReturnRequestItem> returnItems = new ArrayList<>();
            // Map chi tiết đơn để kiểm tra số lượng tối đa
            java.util.Map<Integer, vn.urbansteps.model.HoaDonChiTiet> detailMap = new java.util.HashMap<>();
            if (order.getHoaDonChiTietList() != null) {
                for (vn.urbansteps.model.HoaDonChiTiet d : order.getHoaDonChiTietList()) {
                    detailMap.put(d.getId(), d);
                }
            }
            
            for (Integer itemId : returnItemIds) {
                // Validate qty <= đã mua
                vn.urbansteps.model.HoaDonChiTiet d = detailMap.get(itemId);
                if (d == null) {
                    String errorMsg = messageSource.getMessage("return.products.required", null,
                            "Sản phẩm được chọn không hợp lệ", locale);
                    redirectAttributes.addFlashAttribute("error", errorMsg);
                    return "redirect:/don-hang/" + orderId + "/return-request";
                }
                
                int maxQty = d.getSoLuong() != null ? d.getSoLuong() : 0;
                int qty = maxQty; // trả toàn bộ

                ReturnRequestItem returnItem = new ReturnRequestItem();
                returnItem.setOrderItemId(itemId);
                returnItem.setQty(qty);
                returnItems.add(returnItem);
            }
            
                            // Tạo lý do đầy đủ
            String fullReason = returnType + ": " + lyDo;
            if (ghiChu != null && !ghiChu.trim().isEmpty()) {
                fullReason += " - " + ghiChu;
            }
            
            // Lấy email từ đơn hàng
            String customerEmail = "";
            if (order.getKhachHang() != null && order.getKhachHang().getEmail() != null) {
                customerEmail = order.getKhachHang().getEmail();
            } else if (order.getKhachHang() != null && order.getKhachHang().getTaiKhoan() != null) {
                customerEmail = order.getKhachHang().getTaiKhoan().getEmail();
            }
            
            // Tạo yêu cầu trả hàng
            returnRequestService.createRequest(
                orderId,
                order.getMaHoaDon(),
                order.getKhachHang() != null ? order.getKhachHang().getId() : null,
                hoTen,
                customerEmail,
                soDienThoai,
                fullReason,
                returnItems,
                imageUrls
            );
            // Sau khi gửi yêu cầu: chuyển đơn hàng sang trạng thái Xử lý trả hàng (tách biệt với chờ xử lý chung)
            order.setTrangThai((byte) vn.urbansteps.model.HoaDon.TrangThaiHoaDon.XU_LY_TRA_HANG.getValue());
            hoaDonService.save(order);
            
            String successMsg = messageSource.getMessage("return.request.success", null, 
                "Yêu cầu trả hàng đã được gửi thành công! Chúng tôi sẽ xem xét và phản hồi sớm nhất có thể.", locale);
            redirectAttributes.addFlashAttribute("success", successMsg);
            return "redirect:/don-hang/chi-tiet/" + orderId;
            
        } catch (Exception e) {
            String errorMsg = messageSource.getMessage("return.request.error", new Object[]{e.getMessage()}, 
                "Có lỗi xảy ra khi gửi yêu cầu: " + e.getMessage(), locale);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/don-hang/" + orderId + "/return-request";
        }
    }
    
    private String saveImage(MultipartFile image) throws IOException {
        // Tạo thư mục upload nếu chưa tồn tại - sử dụng đường dẫn tương đối với static folder
        String uploadDir = "src/main/resources/static/uploads/returns/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Tạo tên file unique với timestamp để tránh trùng lặp
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "unnamed.jpg";
        }
        
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        } else {
            extension = ".jpg"; // Default extension
        }
        
        String newFilename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;

        // Lưu file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về đường dẫn web-accessible
        return "/uploads/returns/" + newFilename;
    }
    
    /**
     * Kiểm tra xem file có phải là image hợp lệ không
     */
    private boolean isValidImageType(String contentType) {
        if (contentType == null) return false;
        
        String lowerType = contentType.toLowerCase();
        return lowerType.equals("image/jpeg") || 
               lowerType.equals("image/jpg") || 
               lowerType.equals("image/png") || 
               lowerType.equals("image/gif") ||
               lowerType.equals("image/webp");
    }
    
    // Hiển thị form yêu cầu trả hàng mới (với template mới)
    @GetMapping("/don-hang/{orderId}/return-request-new")
    public String showNewReturnRequestForm(@PathVariable Integer orderId, Model model, 
                                       RedirectAttributes redirectAttributes, Locale locale) {
        try {
            HoaDon order = hoaDonService.getOrderById(orderId);
            if (order == null) {
                String errorMsg = messageSource.getMessage("order.not.found", null, "Không tìm thấy đơn hàng!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang";
            }
            
            // Chỉ cho phép trả hàng khi đơn hàng đã hoàn thành
            if (order.getTrangThai() != 3) {
                String errorMsg = messageSource.getMessage("return.order.status.error", null, 
                    "Chỉ có thể yêu cầu trả hàng khi đơn hàng đã hoàn thành!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/chi-tiet/" + orderId;
            }
            
            // Kiểm tra xem đơn hàng này đã có yêu cầu trả hàng đang chờ xử lý chưa
            long pendingCount = returnRequestRepository.countByOrderIdAndStatus(orderId, vn.urbansteps.model.ReturnRequest.Status.NEW)
                + returnRequestRepository.countByOrderIdAndStatus(orderId, vn.urbansteps.model.ReturnRequest.Status.PROCESSING);
            if (pendingCount > 0) {
                String errorMsg = messageSource.getMessage("return.request.duplicate", null,
                    "Đơn hàng này đã có yêu cầu trả/đổi đang được xử lý.", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/chi-tiet/" + orderId;
            }

            model.addAttribute("order", order);
            
            String title = messageSource.getMessage("return.request.title", null, "Yêu cầu trả hàng", locale) 
                         + " - " + messageSource.getMessage("order.code", null, "Đơn", locale) + " " + order.getMaHoaDon();
            model.addAttribute("title", title);
            
            return "return-request-new";
        } catch (Exception e) {
            String errorMsg = messageSource.getMessage("system.error", new Object[]{e.getMessage()}, 
                "Có lỗi xảy ra: " + e.getMessage(), locale);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/don-hang";
        }
    }
    
    // Hiển thị form yêu cầu trả hàng đã sửa lỗi
    @GetMapping("/don-hang/{orderId}/return-request-fixed")
    public String showFixedReturnRequestForm(@PathVariable Integer orderId, Model model, 
                                       RedirectAttributes redirectAttributes, Locale locale) {
        try {
            HoaDon order = hoaDonService.getOrderById(orderId);
            if (order == null) {
                String errorMsg = messageSource.getMessage("order.not.found", null, "Không tìm thấy đơn hàng!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang";
            }
            
            // Chỉ cho phép trả hàng khi đơn hàng đã hoàn thành
            if (order.getTrangThai() != 3) {
                String errorMsg = messageSource.getMessage("return.order.status.error", null, 
                    "Chỉ có thể yêu cầu trả hàng khi đơn hàng đã hoàn thành!", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/chi-tiet/" + orderId;
            }
            
            // Kiểm tra xem đơn hàng này đã có yêu cầu trả hàng đang chờ xử lý chưa
            long pendingCount = returnRequestRepository.countByOrderIdAndStatus(orderId, vn.urbansteps.model.ReturnRequest.Status.NEW)
                + returnRequestRepository.countByOrderIdAndStatus(orderId, vn.urbansteps.model.ReturnRequest.Status.PROCESSING);
            if (pendingCount > 0) {
                String errorMsg = messageSource.getMessage("return.request.duplicate", null,
                    "Đơn hàng này đã có yêu cầu trả/đổi đang được xử lý.", locale);
                redirectAttributes.addFlashAttribute("error", errorMsg);
                return "redirect:/don-hang/chi-tiet/" + orderId;
            }

            model.addAttribute("order", order);
            
            String title = messageSource.getMessage("return.request.title", null, "Yêu cầu trả hàng", locale) 
                         + " - " + messageSource.getMessage("order.code", null, "Đơn", locale) + " " + order.getMaHoaDon();
            model.addAttribute("title", title);
            
            return "return-request-fixed";
        } catch (Exception e) {
            String errorMsg = messageSource.getMessage("system.error", new Object[]{e.getMessage()}, 
                "Có lỗi xảy ra: " + e.getMessage(), locale);
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/don-hang";
        }
    }
    
    // Xử lý submit form yêu cầu trả hàng từ template mới
    @PostMapping("/don-hang/{orderId}/return-request-new")
    public String submitNewReturnRequest(
            @PathVariable Integer orderId,
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam(required = false) String returnType,
            @RequestParam String lyDo,
            @RequestParam(required = false) String ghiChu,
            @RequestParam(value = "returnItems", required = false) List<Integer> returnItemIds,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        // Reuse the same logic as the original method
    return submitReturnRequest(orderId, hoTen, soDienThoai, returnType, lyDo, ghiChu, 
                  returnItemIds, images, redirectAttributes, locale);
    }
    
    // Xử lý submit form yêu cầu trả hàng từ template sửa lỗi
    @PostMapping("/don-hang/{orderId}/return-request-fixed")
    public String submitFixedReturnRequest(
            @PathVariable Integer orderId,
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam(required = false) String returnType,
            @RequestParam String lyDo,
            @RequestParam(required = false) String ghiChu,
            @RequestParam(value = "returnItems", required = false) List<Integer> returnItemIds,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        // Reuse the same logic as the original method
    return submitReturnRequest(orderId, hoTen, soDienThoai, returnType, lyDo, ghiChu, 
                  returnItemIds, images, redirectAttributes, locale);
    }
}
