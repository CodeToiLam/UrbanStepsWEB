package vn.urbansteps.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.HoaDonChiTiet;
import vn.urbansteps.model.ReturnRequestItem;
import vn.urbansteps.service.HoaDonService;
import vn.urbansteps.service.ReturnRequestService;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrderReturnController {
    private final ReturnRequestService service;
    private final HoaDonService hoaDonService;

    @PostMapping(path = "/orders/{orderCode}/return-request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> createReturnRequest(@PathVariable String orderCode,
                                      @RequestParam Integer orderId,
                                      @RequestParam(required = false) Integer accountId,
                                      @RequestParam String customerName,
                                      @RequestParam String customerEmail,
                                      @RequestParam String customerPhone,
                                      @RequestParam String reason,
                                      @RequestParam("items[]") List<Integer> orderItemIds,
                                      @RequestParam("qtys[]") List<Integer> qtys,
                                      @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        // Basic field validations
        if (orderItemIds == null || qtys == null || orderItemIds.isEmpty() || qtys.isEmpty() || orderItemIds.size() != qtys.size()) {
            return ResponseEntity.badRequest().body("Dữ liệu dòng hàng không hợp lệ");
        }
        int totalQty = qtys.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        if (totalQty <= 0) {
            return ResponseEntity.badRequest().body("Vui lòng chọn số lượng trả hợp lệ");
        }
        if (images == null || images.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng tải lên ít nhất 1 ảnh sản phẩm");
        }

        // Load and verify order
        HoaDon hoaDon = hoaDonService.getOrderById(orderId);
        if (hoaDon == null || hoaDon.getMaHoaDon() == null || !hoaDon.getMaHoaDon().equals(orderCode)) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
        }

        // Eligibility: allow return only if completed (3); not cancelled (4)
        byte st = hoaDon.getTrangThai() == null ? 0 : hoaDon.getTrangThai();
        if (st != 3) {
            return ResponseEntity.badRequest().body("Đơn hàng chỉ có thể yêu cầu trả khi đã Hoàn thành");
        }

        // Ownership verification: either account matches, or phone matches with code
        String orderPhone = hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getSdt() : null;
        boolean byAccount = false;
        if (accountId != null && hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getTaiKhoan() != null) {
            byAccount = Objects.equals(hoaDon.getKhachHang().getTaiKhoan().getId(), accountId);
        }
    String normOrderPhone = orderPhone == null ? "" : orderPhone.replaceAll("[^0-9]", "");
    String normInputPhone = customerPhone == null ? "" : customerPhone.replaceAll("[^0-9]", "");
    boolean byPhone = !normOrderPhone.isEmpty() && normOrderPhone.equals(normInputPhone);
        if (!(byAccount || byPhone)) {
            return ResponseEntity.badRequest().body("Xác thực đơn hàng không hợp lệ (số điện thoại hoặc tài khoản không khớp)");
        }

        // Validate items belong to order and qty <= so luong
        Map<Integer, HoaDonChiTiet> detailsById = new HashMap<>();
        if (hoaDon.getHoaDonChiTietList() != null) {
            for (HoaDonChiTiet d : hoaDon.getHoaDonChiTietList()) {
                detailsById.put(d.getId(), d);
            }
        }
        for (int i = 0; i < orderItemIds.size(); i++) {
            Integer itemId = orderItemIds.get(i);
            Integer qty = qtys.get(i);
            HoaDonChiTiet d = detailsById.get(itemId);
            if (d == null) {
                return ResponseEntity.badRequest().body("Mục hàng không tồn tại trong đơn");
            }
            if (qty == null || qty < 0 || qty > (d.getSoLuong() != null ? d.getSoLuong() : 0)) {
                return ResponseEntity.badRequest().body("Số lượng trả không hợp lệ cho một mục hàng");
            }
        }

        // Build items and image names
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile f : images) imageUrls.add(f.getOriginalFilename());
        }
        List<ReturnRequestItem> items = new ArrayList<>();
        for (int i = 0; i < orderItemIds.size(); i++) {
            items.add(ReturnRequestItem.builder().orderItemId(orderItemIds.get(i)).qty(qtys.get(i)).build());
        }

        var saved = service.createRequest(orderId, orderCode, accountId, customerName, customerEmail, customerPhone, reason, items, imageUrls);
        return ResponseEntity.ok(Map.of("id", saved.getId()));
    }
}
