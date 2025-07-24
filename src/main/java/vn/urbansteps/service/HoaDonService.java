

package vn.urbansteps.service;

import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;

import java.util.List;

public interface HoaDonService {
    HoaDon taoHoaDon(String hoTen, String sdt, String email, String diaChiGiaoHang,
                     int phuongThucThanhToan, String ghiChu, List<GioHangItem> gioHangItems,
                     Integer taiKhoanId, boolean laKhachVangLai);

    // Lấy lịch sử đơn hàng theo id khách hàng
    List<HoaDon> getOrdersByKhachHangId(Integer khachHangId);

    HoaDon getOrderById(Integer orderId);
    HoaDon save(HoaDon hoaDon);
}
