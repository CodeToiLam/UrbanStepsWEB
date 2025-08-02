package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.*;
import vn.urbansteps.repository.HoaDonChiTietRepository;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.repository.PhieuGiamGiaRepository;
import vn.urbansteps.repository.TaiKhoanRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class HoaDonServiceImpl implements HoaDonService {

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
    private EmailService emailService;

    @Transactional
    @Override
    public HoaDon taoHoaDon(String hoTen, String sdt, String email, String diaChiGiaoHang,
                            int phuongThucThanhToan, String ghiChu, List<GioHangItem> gioHangItems,
                            Integer taiKhoanId, boolean laKhachVangLai, String voucherCode,
                            BigDecimal tongThanhToan) {

        KhachHang khachHang = null;

        if (taiKhoanId != null) {
            TaiKhoan taiKhoan = taiKhoanRepository.findById(taiKhoanId).orElse(null);
            if (taiKhoan == null) {
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
            }
        } else {
            // Khách vãng lai
            khachHang = new KhachHang();
            khachHang.setHoTenKhachHang(hoTen);
            khachHang.setSdt(sdt);
            khachHang.setEmail(email);
            khachHang.setLaKhachVangLai(true);
            khachHangRepository.save(khachHang);
        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setKhachHang(khachHang);
        hoaDon.setMaHoaDon(generateMaHoaDon());
        hoaDon.setPhuongThucThanhToan((byte) phuongThucThanhToan);
        hoaDon.setGhiChu(ghiChu);
        hoaDon.setDiaChiGiaoHang(diaChiGiaoHang);
        hoaDon.setTrangThai((byte) 0); // Chờ xử lý
        hoaDon.setCreateAt(LocalDateTime.now());

        // Tìm phiếu giảm giá nếu có
        PhieuGiamGia phieuGiamGia = null;
        if (voucherCode != null && !voucherCode.isEmpty()) {
            phieuGiamGia = phieuGiamGiaRepository.findValidVoucherByCode(voucherCode, LocalDateTime.now())
                    .orElse(null);
        }
        hoaDon.setPhieuGiamGia(phieuGiamGia);

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;
        for (GioHangItem item : gioHangItems) {
            BigDecimal thanhTien = item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong()));
            tongTien = tongTien.add(thanhTien);
        }
        hoaDon.setTongTien(tongTien);
        hoaDon.setTienGiam(phieuGiamGia != null ? tongTien.subtract(tongThanhToan) : BigDecimal.ZERO);
        hoaDon.setTongThanhToan(tongThanhToan);

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
        }

        // Gửi email xác nhận nếu có email
        if (email != null && !email.isEmpty()) {
            String subject = "Xác nhận đơn hàng UrbanSteps";
            String text = "Cảm ơn bạn đã đặt hàng tại UrbanSteps! Mã đơn hàng: " + hoaDon.getMaHoaDon()
                    + "\nTổng tiền: " + hoaDon.getTongThanhToan() + " VNĐ\n";
            if (phieuGiamGia != null) {
                text += "Mã khuyến mãi: " + voucherCode + "\n";
            }
            text += "Chúng tôi sẽ liên hệ và giao hàng sớm nhất!";
            emailService.sendOrderConfirmation(email, subject, text);
        }

        return hoaDon;
    }

    @Transactional
    @Override
    public HoaDon createOrderPOS(String hoTen, String sdt, String ghiChu, List<GioHangItem> items,
                                 BigDecimal tienMat, BigDecimal tienChuyenKhoan, int phuongThucThanhToan,
                                 String voucherCode, BigDecimal tongThanhToan) {
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
        hoaDon.setTrangThai((byte) 5); // Đã thanh toán
        hoaDon.setCreateAt(LocalDateTime.now());
        hoaDon.setTienMat(tienMat != null ? tienMat : BigDecimal.ZERO);
        hoaDon.setTienChuyenKhoan(tienChuyenKhoan != null ? tienChuyenKhoan : BigDecimal.ZERO);

        // Tìm phiếu giảm giá nếu có
        PhieuGiamGia phieuGiamGia = null;
        if (voucherCode != null && !voucherCode.isEmpty()) {
            phieuGiamGia = phieuGiamGiaRepository.findValidVoucherByCode(voucherCode, LocalDateTime.now())
                    .orElse(null);
        }
        hoaDon.setPhieuGiamGia(phieuGiamGia);

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;
        for (GioHangItem item : items) {
            BigDecimal thanhTien = item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong()));
            tongTien = tongTien.add(thanhTien);
        }
        hoaDon.setTongTien(tongTien);
        hoaDon.setTienGiam(phieuGiamGia != null ? tongTien.subtract(tongThanhToan) : BigDecimal.ZERO);
        hoaDon.setTongThanhToan(tongThanhToan);

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
        }

        return hoaDon;
    }

    @Override
    public List<HoaDon> getOrdersByKhachHangId(Integer khachHangId) {
        return hoaDonRepository.findByKhachHang_IdOrderByCreateAtDesc(khachHangId);
    }

    @Override
    public HoaDon getOrderById(Integer orderId) {
        return hoaDonRepository.findById(orderId).orElse(null);
    }

    @Override
    public HoaDon save(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    private String generateMaHoaDon() {
        String prefix = "HD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "_" + random;
    }
}