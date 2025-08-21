package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "ma_hoa_don", nullable = false, unique = true, length = 50)
    private String maHoaDon;

    @Column(name = "tong_tien", nullable = false)
    private BigDecimal tongTien;

    @Column(name = "tien_giam")
    private BigDecimal tienGiam = BigDecimal.ZERO;

    @Column(name = "tong_thanh_toan", nullable = false)
    private BigDecimal tongThanhToan;

    @Column(name = "tien_mat")
    private BigDecimal tienMat = BigDecimal.ZERO;

    @Column(name = "tien_chuyen_khoan")
    private BigDecimal tienChuyenKhoan = BigDecimal.ZERO;

    @Column(name = "phuong_thuc_thanh_toan", nullable = false)
    private Byte phuongThucThanhToan; // 1: Tiền mặt, 2: Chuyển khoản, 3: Cả hai

    @Column(name = "trang_thai")
    private Byte trangThai = (byte) 0; // 0: Chờ xử lý, 1: Đã xác nhận, 2: Đang giao, 3: Hoàn thành, 4: Đã hủy, 5: Đã thanh toán, 6: Trả hàng, 7: Xử lý trả hàng

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "dia_chi_giao_hang", length = 500)
    private String diaChiGiaoHang;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> hoaDonChiTietList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if (tienGiam == null) tienGiam = BigDecimal.ZERO;
        if (tienMat == null) tienMat = BigDecimal.ZERO;
        if (tienChuyenKhoan == null) tienChuyenKhoan = BigDecimal.ZERO;
        if (trangThai == null) trangThai = (byte) 0;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }

    // Enum for order status
    public enum TrangThaiHoaDon {
        CHO_XU_LY(0, "Chờ xử lý"),
        DA_XAC_NHAN(1, "Đã xác nhận"),
        DANG_GIAO(2, "Đang giao"),
        HOAN_THANH(3, "Hoàn thành"),
        DA_HUY(4, "Đã hủy"),
        DA_THANH_TOAN(5, "Đã thanh toán"),
        TRA_HANG(6, "Trả hàng"),
        XU_LY_TRA_HANG(7, "Xử lý trả hàng"),
        TRA_HANG_THAT_BAI(8, "Trả hàng thất bại");

        private final int value;
        private final String description;

        TrangThaiHoaDon(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() { return value; }
        public String getDescription() { return description; }

        public static TrangThaiHoaDon fromValue(int value) {
            for (TrangThaiHoaDon status : values()) {
                if (status.value == value) return status;
            }
            return CHO_XU_LY;
        }
    }

    // Enum for payment method
    public enum PhuongThucThanhToan {
        TIEN_MAT(1, "Tiền mặt"),
        CHUYEN_KHOAN(2, "Chuyển khoản"),
        CA_HAI(3, "Cả hai");

        private final int value;
        private final String description;

        PhuongThucThanhToan(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() { return value; }
        public String getDescription() { return description; }

        public static PhuongThucThanhToan fromValue(int value) {
            for (PhuongThucThanhToan method : values()) {
                if (method.value == value) return method;
            }
            return TIEN_MAT;
        }
    }

    // Utility methods
    public String getTrangThaiText() {
        return TrangThaiHoaDon.fromValue(trangThai != null ? trangThai.intValue() : 0).getDescription();
    }

    public String getPhuongThucThanhToanText() {
        return PhuongThucThanhToan.fromValue(phuongThucThanhToan != null ? phuongThucThanhToan.intValue() : 1).getDescription();
    }
    public boolean isHoanThanh() {
        return this.trangThai != null && this.trangThai == 3;
    }


    public Boolean isPending() {
        return trangThai != null && trangThai == 0;
    }

    public Boolean isCompleted() {
        return trangThai != null && trangThai == 3;
    }
    public Boolean isReturned() {
        return trangThai != null && trangThai == 6;
    }

    public Boolean isCancelled() {
        return trangThai != null && trangThai == 4;
    }

    public Boolean canCancel() {
        return trangThai != null && trangThai <= 1; // Chỉ có thể hủy khi chờ xử lý hoặc đã xác nhận
    }
    // Deprecated inner enum removed to avoid conflicting values (kept only TrangThaiHoaDon above)
}
