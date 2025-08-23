package vn.urbansteps.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.HoaDonChiTiet;
import vn.urbansteps.model.KhachHang;
import vn.urbansteps.model.PhieuGiamGia;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.HoaDonChiTietRepository;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.repository.PhieuGiamGiaRepository;
import vn.urbansteps.repository.TaiKhoanRepository;
import vn.urbansteps.repository.SanPhamChiTietRepository;
import vn.urbansteps.model.SanPhamChiTiet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class HoaDonServiceImpl implements HoaDonService {

    private static final Logger logger = LoggerFactory.getLogger(HoaDonServiceImpl.class);

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private vn.urbansteps.repository.SanPhamRepository sanPhamRepository;

    @Override
    public Page<HoaDon> searchOrders(String keyword, Byte status, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            if (status != null) {
                return hoaDonRepository.findByMaHoaDonContainingIgnoreCaseOrKhachHang_HoTenKhachHangContainingIgnoreCaseOrKhachHang_SdtContainingAndTrangThai(
                        keyword, keyword, keyword, status, pageable);
            }
            return hoaDonRepository.findByMaHoaDonContainingIgnoreCaseOrKhachHang_HoTenKhachHangContainingIgnoreCaseOrKhachHang_SdtContaining(
                    keyword, keyword, keyword, pageable);
        }
        if (status != null) {
            return hoaDonRepository.findByTrangThai(status, pageable);
        }
        return hoaDonRepository.findAll(pageable);
    }

    @Override
    public Page<HoaDon> getAllOrders(Pageable pageable) {
        return hoaDonRepository.findAll(pageable);
    }

    @Override
    public Optional<HoaDon> findByMaHoaDonAndSdt(String maHoaDon, String sdt) {
        return hoaDonRepository.findByMaHoaDonAndKhachHang_Sdt(maHoaDon, sdt);
    }

    @Override
    @Transactional
    public HoaDon createOrderPOS(String hoTen, String sdt, String ghiChu, List<GioHangItem> items,
                                 BigDecimal tienMat, BigDecimal tienChuyenKhoan, int phuongThucThanhToan,
                                 String voucherCode, BigDecimal tongThanhToan) {
        logger.info("Tạo hóa đơn POS: hoTen={}, sdt={}, phuongThucThanhToan={}", hoTen, sdt, phuongThucThanhToan);
        // Tạo khách vãng lai
        KhachHang khachHang = new KhachHang();
        khachHang.setHoTenKhachHang(hoTen);
        khachHang.setSdt(sdt);
        khachHang.setLaKhachVangLai(true);
        khachHangRepository.save(khachHang);

        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setMaHoaDon(generateMaHoaDon());
        hoaDon.setPhuongThucThanhToan((byte) phuongThucThanhToan);
        hoaDon.setGhiChu(ghiChu);
    hoaDon.setTrangThai((byte) HoaDon.TrangThaiHoaDon.HOAN_THANH.getValue()); // Hoàn thành
        hoaDon.setCreateAt(LocalDateTime.now());
        hoaDon.setTienMat(tienMat != null ? tienMat : BigDecimal.ZERO);
        hoaDon.setTienChuyenKhoan(tienChuyenKhoan != null ? tienChuyenKhoan : BigDecimal.ZERO);

        // Gán phiếu giảm giá nếu có
        if (voucherCode != null && !voucherCode.isEmpty()) {
            Optional<PhieuGiamGia> phieuGiamGiaOpt = phieuGiamGiaRepository.findValidVoucherByCode(voucherCode, LocalDateTime.now());
            phieuGiamGiaOpt.ifPresent(phieu -> {
                hoaDon.setPhieuGiamGia(phieu);
                phieuGiamGiaRepository.incrementUsageCount(phieu.getId());
                logger.info("Áp dụng phiếu giảm giá: {}", voucherCode);
            });
        }

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;
        for (GioHangItem item : items) {
            BigDecimal thanhTien = item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong()));
            tongTien = tongTien.add(thanhTien);
        }
        hoaDon.setTongTien(tongTien);
        hoaDon.setTienGiam(tongTien.subtract(tongThanhToan));
        hoaDon.setTongThanhToan(tongThanhToan);

        logger.info("Lưu hóa đơn POS: maHoaDon={}", hoaDon.getMaHoaDon());
        hoaDonRepository.save(hoaDon);

        // Lưu chi tiết hóa đơn
        int index = 1;
        for (GioHangItem item : items) {
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setSanPhamChiTiet(item.getSanPhamChiTiet());
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setGiaBan(item.getGiaTaiThoidiem());
            chiTiet.setGiaNhap(item.getSanPhamChiTiet().getSanPham().getGiaNhap());
            chiTiet.setThanhTien(item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong())));
            chiTiet.setMaHoaDonChiTiet(hoaDon.getMaHoaDon() + "_POS_" + index++);
            chiTiet.setCreateAt(LocalDateTime.now());
            hoaDonChiTietRepository.save(chiTiet);
            logger.info("Lưu chi tiết hóa đơn: maHoaDonChiTiet={}", chiTiet.getMaHoaDonChiTiet());

            // Cập nhật lượt bán cho sản phẩm
            try {
                vn.urbansteps.repository.SanPhamRepository sanPhamRepo =
                        org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext()
                                .getBean(vn.urbansteps.repository.SanPhamRepository.class);
                Integer spId = item.getSanPhamChiTiet() != null && item.getSanPhamChiTiet().getSanPham() != null
                        ? item.getSanPhamChiTiet().getSanPham().getId() : null;
                if (spId != null && item.getSoLuong() != null && item.getSoLuong() > 0) {
                    sanPhamRepo.incrementLuotBan(spId, item.getSoLuong());
                }
            } catch (Exception ignore) {}

            // Cộng lượt bán cho sản phẩm cha (POS là hoàn thành ngay)
            try {
                SanPhamChiTiet spct = item.getSanPhamChiTiet();
                if (spct != null && spct.getSanPham() != null) {
                    Integer spId = spct.getSanPham().getId();
                    Integer qty = item.getSoLuong();
                    if (spId != null && qty != null && qty > 0) sanPhamRepository.incrementLuotBan(spId, qty);
                }
            } catch (Exception ignore) {}
        }

        logger.info("Tạo hóa đơn POS thành công: maHoaDon={}", hoaDon.getMaHoaDon());
        return hoaDon;
    }

    @Override
    @Transactional
    public HoaDon taoHoaDon(String hoTen, String sdt, String email, String diaChiGiaoHang,
                            int phuongThucThanhToan, String ghiChu, List<GioHangItem> gioHangItems,
                            Integer taiKhoanId, boolean laKhachVangLai, String appliedVoucherCode,
                            BigDecimal tongThanhToan) {
        logger.info("Tạo hóa đơn: hoTen={}, sdt={}, taiKhoanId={}, laKhachVangLai={}",
                hoTen, sdt, taiKhoanId, laKhachVangLai);
        KhachHang khachHang = null;

        if (taiKhoanId != null) {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(taiKhoanId).orElse(null);
            if (taiKhoan == null) {
                logger.error("Tài khoản không tồn tại: taiKhoanId={}", taiKhoanId);
                throw new RuntimeException("Tài khoản không tồn tại");
            }
            khachHang = khachHangRepository.findByTaiKhoan(taiKhoan).orElse(null);
            if (khachHang == null) {
                khachHang = new KhachHang();
                khachHang.setTaiKhoan(taiKhoan);
                khachHang.setHoTenKhachHang(hoTen);
                khachHang.setSdt(sdt);
                khachHang.setEmail(email);
                khachHang.setLaKhachVangLai(false);
                khachHangRepository.save(khachHang);
                logger.info("Tạo khách hàng mới cho tài khoản: taiKhoanId={}", taiKhoanId);
            }
        } else {
            // Khách vãng lai
            khachHang = new KhachHang();
            khachHang.setHoTenKhachHang(hoTen);
            khachHang.setSdt(sdt);
            khachHang.setEmail(email);
            khachHang.setLaKhachVangLai(true);
            khachHangRepository.save(khachHang);
            logger.info("Tạo khách hàng vãng lai: hoTen={}, sdt={}", hoTen, sdt);
        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setMaHoaDon(generateMaHoaDon());
        hoaDon.setPhuongThucThanhToan((byte) phuongThucThanhToan);
        hoaDon.setGhiChu(ghiChu);
        hoaDon.setDiaChiGiaoHang(diaChiGiaoHang);
    hoaDon.setTrangThai((byte) HoaDon.TrangThaiHoaDon.CHO_XU_LY.getValue()); // Chờ xử lý
        hoaDon.setCreateAt(LocalDateTime.now());

        // Gán phiếu giảm giá nếu có
        if (appliedVoucherCode != null && !appliedVoucherCode.isEmpty()) {
            Optional<PhieuGiamGia> phieuGiamGiaOpt = phieuGiamGiaRepository.findValidVoucherByCode(appliedVoucherCode, LocalDateTime.now());
            phieuGiamGiaOpt.ifPresent(phieu -> {
                hoaDon.setPhieuGiamGia(phieu);
                phieuGiamGiaRepository.incrementUsageCount(phieu.getId());
                logger.info("Áp dụng phiếu giảm giá: {}", appliedVoucherCode);
            });
        }

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;
        for (GioHangItem item : gioHangItems) {
            BigDecimal thanhTien = item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong()));
            tongTien = tongTien.add(thanhTien);
        }
        hoaDon.setTongTien(tongTien);
        hoaDon.setTienGiam(tongTien.subtract(tongThanhToan));
        hoaDon.setTongThanhToan(tongThanhToan);

        logger.info("Lưu hóa đơn: maHoaDon={}", hoaDon.getMaHoaDon());
        hoaDonRepository.save(hoaDon);

        // Lưu chi tiết hóa đơn
        int index = 1;
        for (GioHangItem item : gioHangItems) {
            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setSanPhamChiTiet(item.getSanPhamChiTiet());
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setGiaBan(item.getGiaTaiThoidiem());
            chiTiet.setGiaNhap(item.getSanPhamChiTiet().getSanPham().getGiaNhap());
            chiTiet.setThanhTien(item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong())));
            chiTiet.setMaHoaDonChiTiet(hoaDon.getMaHoaDon() + "_" + index++);
            chiTiet.setCreateAt(LocalDateTime.now());
            hoaDonChiTietRepository.save(chiTiet);
            logger.info("Lưu chi tiết hóa đơn: maHoaDonChiTiet={}", chiTiet.getMaHoaDonChiTiet());
        }

        logger.info("Tạo hóa đơn thành công: maHoaDon={}", hoaDon.getMaHoaDon());
        return hoaDon;
    }

    @Override
    public HoaDon getOrderById(Integer orderId) {
        return hoaDonRepository.findById(orderId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDon getOrderByIdWithDetails(Integer orderId) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId).orElse(null);
        if (hoaDon != null) {
            // Force lazy loading of hoaDonChiTietList
            hoaDon.getHoaDonChiTietList().size();
            
            // Force loading of sanPhamChiTiet relationships
            for (var item : hoaDon.getHoaDonChiTietList()) {
                if (item.getSanPhamChiTiet() != null) {
                    item.getSanPhamChiTiet().getSanPham().getTenSanPham();
                    if (item.getSanPhamChiTiet().getMauSac() != null) {
                        item.getSanPhamChiTiet().getMauSac().getTenMauSac();
                    }
                    if (item.getSanPhamChiTiet().getKichCo() != null) {
                        item.getSanPhamChiTiet().getKichCo().getTenKichCo();
                    }
                }
            }
        }
        return hoaDon;
    }

    @Override
    @Transactional
    public HoaDon save(HoaDon hoaDon) {
        logger.info("Lưu hóa đơn: maHoaDon={}", hoaDon.getMaHoaDon());
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public List<HoaDon> getOrdersByKhachHangId(Integer khachHangId) {
        return hoaDonRepository.findByKhachHang_IdOrderByCreateAtDesc(khachHangId);
    }

    @Override
    public List<HoaDon> getOrdersByPhone(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            return List.of();
        }
        return hoaDonRepository.findByKhachHang_SdtOrderByCreateAtDesc(sdt.trim());
    }

    @Override
    @Transactional
    public HoaDon refundOrderItem(Integer orderId, Integer orderItemId, Integer quantity, String note) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        HoaDonChiTiet chiTiet = hoaDonChiTietRepository.findById(orderItemId).orElseThrow(() -> new RuntimeException("Không tìm thấy dòng hàng"));
        if (!chiTiet.getHoaDon().getId().equals(orderId)) {
            throw new RuntimeException("Dòng hàng không thuộc đơn này");
        }
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Số lượng hoàn không hợp lệ");
        }
        if (chiTiet.getSoLuong() < quantity) {
            throw new RuntimeException("Số lượng hoàn vượt quá đã mua");
        }
        // Restock
    SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
    spct.setSoLuong((spct.getSoLuong() == null ? 0 : spct.getSoLuong()) + quantity);
    sanPhamChiTietRepository.save(spct);
        // Update item quantity and totals
        chiTiet.setSoLuong(chiTiet.getSoLuong() - quantity);
        // preUpdate hook recalculates thanhTien
        hoaDonChiTietRepository.save(chiTiet);

        // Recompute order totals from details
        var details = hoaDonChiTietRepository.findByHoaDon_Id(orderId);
        java.math.BigDecimal tongTien = java.math.BigDecimal.ZERO;
        for (HoaDonChiTiet d : details) {
            if (d.getThanhTien() != null)
                tongTien = tongTien.add(d.getThanhTien());
        }
        hoaDon.setTongTien(tongTien);
        // Keep existing tienGiam, recalc payable
        java.math.BigDecimal tienGiam = hoaDon.getTienGiam() == null ? java.math.BigDecimal.ZERO : hoaDon.getTienGiam();
        hoaDon.setTongThanhToan(tongTien.subtract(tienGiam));

        // Append note
        String existing = hoaDon.getGhiChu();
        String append = (note != null && !note.isBlank()) ? ("Hoàn trả: " + note) : "Hoàn trả một phần";
        hoaDon.setGhiChu((existing == null || existing.isBlank()) ? append : (existing + " | " + append));

        return hoaDonRepository.save(hoaDon);
    }

    @Override
    @Transactional
    public HoaDon returnAllItems(Integer orderId, String note) {
        HoaDon hoaDon = hoaDonRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        var details = hoaDonChiTietRepository.findByHoaDon_Id(orderId);
        for (HoaDonChiTiet ct : details) {
            int qty = ct.getSoLuong() == null ? 0 : ct.getSoLuong();
            if (qty > 0) {
                refundOrderItem(orderId, ct.getId(), qty, note);
            }
        }
        return hoaDonRepository.findById(orderId).orElse(hoaDon);
    }

    private String generateMaHoaDon() {
        String prefix = "HD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "_" + random;
    }

    @Transactional
    public void xacNhanHoaDon(Integer hoaDonId) {
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        for (HoaDonChiTiet chiTiet : hoaDon.getHoaDonChiTietList()) {
            SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
            int soLuongDat = chiTiet.getSoLuong();
            if (spct.getSoLuong() < soLuongDat) {
                throw new RuntimeException("Sản phẩm " +
                        spct.getSanPham().getTenSanPham() +
                        " không đủ số lượng tồn kho!");
            }
            spct.setSoLuong(spct.getSoLuong() - soLuongDat);
            sanPhamChiTietRepository.save(spct);
        }
        // Đổi từ enum sang byte value khi set trạng thái
        hoaDon.setTrangThai((byte) HoaDon.TrangThaiHoaDon.DA_XAC_NHAN.getValue());; // Cập nhật trạng thái hóa đơn
        hoaDonRepository.save(hoaDon);
    }

}