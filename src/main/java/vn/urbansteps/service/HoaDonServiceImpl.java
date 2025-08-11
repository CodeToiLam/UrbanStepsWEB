package vn.urbansteps.service;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        hoaDon.setTrangThai((byte) HoaDon.TrangThai.DA_HOAN_THANH.getValue()); // Đã thanh toán
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
        hoaDon.setTrangThai((byte) HoaDon.TrangThai.CHO_XU_LY.getValue()); // Chờ xử lý
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

    private String generateMaHoaDon() {
        String prefix = "HD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "_" + random;
    }
}