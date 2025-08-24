package vn.urbansteps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.urbansteps.model.DanhGiaSanPham;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.model.HoaDonChiTiet;
import vn.urbansteps.model.KhachHang;
import vn.urbansteps.service.DanhGiaSanPhamService;
import vn.urbansteps.repository.HoaDonChiTietRepository;
import vn.urbansteps.repository.DanhGiaSanPhamRepository;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.service.SanPhamService;
import vn.urbansteps.service.TaiKhoanService;

import java.util.List;
import java.util.Map;

@RestController
public class DanhGiaController {
    private final DanhGiaSanPhamService danhGiaService;
    private final SanPhamService sanPhamService;
    private final TaiKhoanService taiKhoanService;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final DanhGiaSanPhamRepository danhGiaSanPhamRepository;
    private final KhachHangRepository khachHangRepository;

    public DanhGiaController(DanhGiaSanPhamService danhGiaService, SanPhamService sanPhamService, TaiKhoanService taiKhoanService, HoaDonChiTietRepository hoaDonChiTietRepository, DanhGiaSanPhamRepository danhGiaSanPhamRepository, KhachHangRepository khachHangRepository) {
        this.danhGiaService = danhGiaService;
        this.sanPhamService = sanPhamService;
        this.taiKhoanService = taiKhoanService;
        this.hoaDonChiTietRepository = hoaDonChiTietRepository;
        this.danhGiaSanPhamRepository = danhGiaSanPhamRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @GetMapping("/api/san-pham/{id}/danh-gia")
    public List<DanhGiaSanPham> list(@PathVariable Integer id) {
        return danhGiaService.getReviews(id);
    }

    @PostMapping("/api/san-pham/{id}/danh-gia")
    public ResponseEntity<?> create(@PathVariable Integer id, @RequestBody Map<String,Object> body, Authentication auth) {
        var opt = sanPhamService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body("Product not found");
        SanPham sp = opt.get();

        // must be authenticated
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).body(Map.of("error","Bạn cần đăng nhập để đánh giá"));
        }

        TaiKhoan tk = taiKhoanService.findByTaiKhoan(auth.getName());
        if (tk == null) return ResponseEntity.status(401).body(Map.of("error","Tài khoản không hợp lệ"));

        // find customer's KhachHang record
        var khOpt = khachHangRepository.findByTaiKhoan(tk);
        if (khOpt.isEmpty()) {
            return ResponseEntity.status(403).body(Map.of("error","Bạn chỉ có thể đánh giá sản phẩm sau khi đơn hàng hoàn thành"));
        }
        KhachHang kh = khOpt.get();

        // find any completed order item (HoaDonChiTiet) for this customer and product with hoaDon.trangThai == 3
        List<HoaDonChiTiet> matches = hoaDonChiTietRepository.findByCustomerId(kh.getId());
        Integer matchedHdctId = null;
        for (HoaDonChiTiet hdct : matches) {
            if (hdct.getSanPhamChiTiet() != null && hdct.getSanPhamChiTiet().getSanPham() != null && hdct.getSanPhamChiTiet().getSanPham().getId().equals(id)) {
                // check order status == 3 (Hoàn thành)
                if (hdct.getHoaDon() != null && hdct.getHoaDon().getTrangThai() != null && hdct.getHoaDon().getTrangThai() == 3) {
                    matchedHdctId = hdct.getId();
                    break;
                }
            }
        }

        if (matchedHdctId == null) {
            return ResponseEntity.status(403).body(Map.of("error","Bạn chỉ có thể đánh giá sản phẩm sau khi đơn hàng hoàn thành"));
        }

        // prevent duplicate review for same order item
        if (danhGiaSanPhamRepository.existsByTaiKhoan_IdAndHoaDonChiTiet_Id(tk.getId(), matchedHdctId)) {
            return ResponseEntity.status(409).body(Map.of("error","Bạn đã gửi đánh giá cho sản phẩm này"));
        }

        DanhGiaSanPham dg = new DanhGiaSanPham();
        dg.setSanPham(sp);
        dg.setTaiKhoan(tk);
        var hdctOpt = hoaDonChiTietRepository.findById(matchedHdctId);
        hdctOpt.ifPresent(dg::setHoaDonChiTiet);

        Object nd = body.get("noiDung");
        Object diem = body.get("diemDanhGia");
        dg.setNoiDung(nd != null ? nd.toString() : "");
        try { dg.setDiemDanhGia(diem != null ? Byte.parseByte(diem.toString()) : (byte)5); } catch(Exception e) { dg.setDiemDanhGia((byte)5); }

        danhGiaService.addReview(dg);
        return ResponseEntity.ok(Map.of("message","Đã gửi đánh giá"));
    }
}
