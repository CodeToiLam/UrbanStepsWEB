package vn.urbansteps.service;

import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;

import java.math.BigDecimal;
import java.util.List;

public interface HoaDonService {
    HoaDon taoHoaDon(String hoTen, String sdt, String email, String diaChiGiaoHang,
                     int phuongThucThanhToan, String ghiChu, List<GioHangItem> gioHangItems,
                     Integer taiKhoanId, boolean laKhachVangLai, String voucherCode,
                     BigDecimal tongThanhToan);

    HoaDon createOrderPOS(String hoTen, String sdt, String ghiChu, List<GioHangItem> items,
                          BigDecimal tienMat, BigDecimal tienChuyenKhoan, int phuongThucThanhToan,
                          String voucherCode, BigDecimal tongThanhToan);

    List<HoaDon> getOrdersByKhachHangId(Integer khachHangId);

    HoaDon getOrderById(Integer orderId);
    HoaDon save(HoaDon hoaDon);
}