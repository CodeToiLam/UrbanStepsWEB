
        package vn.urbansteps.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.HoaDon;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HoaDonService {
    Page<HoaDon> searchOrders(String keyword, Byte status, Pageable pageable);
    Page<HoaDon> getAllOrders(Pageable pageable);
    Optional<HoaDon> findByMaHoaDonAndSdt(String maHoaDon, String sdt);
    HoaDon createOrderPOS(String hoTen, String sdt, String ghiChu, List<GioHangItem> items,
                          BigDecimal tienMat, BigDecimal tienChuyenKhoan, int phuongThucThanhToan,
                          String voucherCode, BigDecimal tongThanhToan);
    HoaDon taoHoaDon(String hoTen, String sdt, String email, String diaChiGiaoHang,
                     int phuongThucThanhToan, String ghiChu, List<GioHangItem> items,
                     Integer taiKhoanId, boolean laKhachVangLai, String appliedVoucherCode,
                     BigDecimal tongThanhToan);
    HoaDon getOrderById(Integer orderId);
    HoaDon getOrderByIdWithDetails(Integer orderId);
    HoaDon save(HoaDon hoaDon);
    List<HoaDon> getOrdersByKhachHangId(Integer khachHangId);
    List<HoaDon> getOrdersByPhone(String sdt);

    // Hoàn trả một phần: giảm số lượng item và cập nhật tổng tiền, hoàn hàng về kho
    HoaDon refundOrderItem(Integer orderId, Integer orderItemId, Integer quantity, String note);

    // Trả hàng toàn bộ các dòng: hoàn hết số lượng các dòng còn lại
    HoaDon returnAllItems(Integer orderId, String note);

    void xacNhanHoaDon(Integer hoaDonId);

}
