package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuGiamGia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phieu_giam_gia", nullable = false, unique = true, length = 50)
    private String maPhieuGiamGia;

    @Column(name = "ten_phieu_giam_gia", nullable = false, length = 255)
    private String tenPhieuGiamGia;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "so_luong_da_su_dung")
    private Integer soLuongDaSuDung = 0;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "giam_theo_phan_tram")
    private Boolean giamTheoPhanTram = false;

    @Column(name = "gia_tri_giam", nullable = false)
    private BigDecimal giaTriGiam;

    @Column(name = "giam_toi_da")
    private BigDecimal giamToiDa;

    @Column(name = "don_toi_thieu")
    private BigDecimal donToiThieu = BigDecimal.ZERO;

    @Column(name = "ap_dung_cho_tat_ca")
    private Boolean apDungChoTatCa = true;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if (soLuongDaSuDung == null) soLuongDaSuDung = 0;
        if (giamTheoPhanTram == null) giamTheoPhanTram = false;
        if (donToiThieu == null) donToiThieu = BigDecimal.ZERO;
        if (apDungChoTatCa == null) apDungChoTatCa = true;
        if (trangThai == null) trangThai = true;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }

    // Utility methods
    public Boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return trangThai != null && trangThai &&
               ngayBatDau != null && ngayKetThuc != null &&
               now.isAfter(ngayBatDau) && now.isBefore(ngayKetThuc) &&
               (soLuong == null || soLuongDaSuDung < soLuong);
    }

    public Boolean isAvailable() {
        return soLuong == null || soLuongDaSuDung < soLuong;
    }

    public BigDecimal calculateDiscount(BigDecimal donHangTotal) {
        if (!isActive() || donHangTotal.compareTo(donToiThieu) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (giamTheoPhanTram != null && giamTheoPhanTram) {
            discount = donHangTotal.multiply(giaTriGiam).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (giamToiDa != null && discount.compareTo(giamToiDa) > 0) {
                discount = giamToiDa;
            }
        } else {
            discount = giaTriGiam;
        }

        return discount.min(donHangTotal);
    }
}
