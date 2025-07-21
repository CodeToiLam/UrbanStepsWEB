package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.*;
import vn.urbansteps.repository.HoaDonChiTietRepository;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.repository.TaiKhoanRepository;
import vn.urbansteps.service.HoaDonService;

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

    @Transactional
    @Override
    public HoaDon taoHoaDon(String hoTen, String sdt, String email, String diaChiGiaoHang,
                            int phuongThucThanhToan, String ghiChu, List<GioHangItem> gioHangItems,
                            Integer taiKhoanId, boolean laKhachVangLai) {

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
        hoaDon.setTrangThai((byte) 0); // chờ xử lý
        hoaDon.setCreateAt(LocalDateTime.now());

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;
        for (GioHangItem item : gioHangItems) {
            BigDecimal thanhTien = item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong()));
            tongTien = tongTien.add(thanhTien);
        }
        hoaDon.setTongTien(tongTien);
        hoaDon.setTienGiam(BigDecimal.ZERO);
        hoaDon.setTongThanhToan(tongTien);

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

        return hoaDon;
    }

    private String generateMaHoaDon() {
        // Ví dụ: HD20240612_ABC123
        String prefix = "HD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "_" + random;
    }
}
