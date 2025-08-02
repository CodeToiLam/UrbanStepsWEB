
package vn.urbansteps.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;

import java.util.List;
import java.util.Optional;

public interface HoaDonService {
    HoaDon taoHoaDon(String hoTen, String sdt, String email, String diaChiGiaoHang,
                     int phuongThucThanhToan, String ghiChu, List<GioHangItem> gioHangItems,
                     Integer taiKhoanId, boolean laKhachVangLai);

    // Lấy lịch sử đơn hàng theo id khách hàng
    List<HoaDon> getOrdersByKhachHangId(Integer khachHangId);

    HoaDon getOrderById(Integer orderId);
    HoaDon save(HoaDon hoaDon);
    
    // Admin methods
    Page<HoaDon> getAllOrders(Pageable pageable);
    Page<HoaDon> searchOrders(String search, Byte status, Pageable pageable);
    
    // Customer tracking methods
    Optional<HoaDon> findByMaHoaDonAndSdt(String maHoaDon, String sdt);
}
