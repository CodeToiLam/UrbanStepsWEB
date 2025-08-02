
package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.*;
import vn.urbansteps.repository.HoaDonChiTietRepository;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.repository.KhachHangRepository;
import vn.urbansteps.repository.TaiKhoanRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Override
    public HoaDon getOrderById(Integer orderId) {
        return hoaDonRepository.findById(orderId).orElse(null);
    }

    @Override
    public HoaDon save(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    @Override
    public List<HoaDon> getOrdersByKhachHangId(Integer khachHangId) {
        return hoaDonRepository.findByKhachHang_IdOrderByCreateAtDesc(khachHangId);
    }
    
    @Override
    public Page<HoaDon> getAllOrders(Pageable pageable) {
        return hoaDonRepository.findAll(pageable);
    }
    
    @Override
    public Page<HoaDon> searchOrders(String search, Byte status, Pageable pageable) {
        if (search != null && !search.isEmpty() && status != null) {
            // Tìm kiếm theo mã hóa đơn, tên khách hàng hoặc SĐT và trạng thái
            return hoaDonRepository.findByMaHoaDonContainingIgnoreCaseOrKhachHang_HoTenKhachHangContainingIgnoreCaseOrKhachHang_SdtContainingAndTrangThai(
                search, search, search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            // Chỉ tìm kiếm theo từ khóa
            return hoaDonRepository.findByMaHoaDonContainingIgnoreCaseOrKhachHang_HoTenKhachHangContainingIgnoreCaseOrKhachHang_SdtContaining(
                search, search, search, pageable);
        } else if (status != null) {
            // Chỉ lọc theo trạng thái
            return hoaDonRepository.findByTrangThai(status, pageable);
        } else {
            return hoaDonRepository.findAll(pageable);
        }
    }
    
    @Override
    public Optional<HoaDon> findByMaHoaDonAndSdt(String maHoaDon, String sdt) {
        return hoaDonRepository.findByMaHoaDonAndKhachHang_Sdt(maHoaDon, sdt);
    }

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

    /**
     * Tạo đơn POS cho bán hàng tại quầy
     * @param hoTen Tên khách hàng (có thể null nếu khách vãng lai)
     * @param sdt Số điện thoại khách hàng (có thể null)
     * @param ghiChu Ghi chú đơn hàng
     * @param items Danh sách sản phẩm bán ra
     * @param tienMat Số tiền mặt khách trả
     * @param tienChuyenKhoan Số tiền chuyển khoản khách trả
     * @param phuongThucThanhToan 1: Tiền mặt, 2: Chuyển khoản, 3: Cả hai
     * @return HoaDon đã tạo
     */
    @Transactional
    public HoaDon createOrderPOS(String hoTen, String sdt, String ghiChu, List<GioHangItem> items,
                                 BigDecimal tienMat, BigDecimal tienChuyenKhoan, int phuongThucThanhToan) {
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

        // Tính tổng tiền
        BigDecimal tongTien = BigDecimal.ZERO;
        for (GioHangItem item : items) {
            BigDecimal thanhTien = item.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(item.getSoLuong()));
            tongTien = tongTien.add(thanhTien);
        }
        hoaDon.setTongTien(tongTien);
        hoaDon.setTienGiam(BigDecimal.ZERO);
        hoaDon.setTongThanhToan(tongTien);

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
}
