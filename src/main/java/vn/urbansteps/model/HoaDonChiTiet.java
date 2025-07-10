package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDonChiTiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_chi_tiet", nullable = false)
    private SanPhamChiTiet sanPhamChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "ma_hoa_don_chi_tiet", nullable = false, unique = true, length = 50)
    private String maHoaDonChiTiet;

    @Column(name = "gia_nhap", nullable = false)
    private BigDecimal giaNhap;

    @Column(name = "gia_ban", nullable = false)
    private BigDecimal giaBan;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "thanh_tien", nullable = false)
    private BigDecimal thanhTien;

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
        calculateThanhTien();
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
        calculateThanhTien();
    }

    private void calculateThanhTien() {
        if (giaBan != null && soLuong != null) {
            thanhTien = giaBan.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    // Utility methods
    public BigDecimal getLoiNhuan() {
        if (giaBan != null && giaNhap != null) {
            return giaBan.subtract(giaNhap);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTongLoiNhuan() {
        return getLoiNhuan().multiply(BigDecimal.valueOf(soLuong != null ? soLuong : 0));
    }

    public String getTenSanPham() {
        return sanPhamChiTiet != null && sanPhamChiTiet.getSanPham() != null ?
               sanPhamChiTiet.getSanPham().getTenSanPham() : "";
    }

    public String getTenMauSac() {
        return sanPhamChiTiet != null && sanPhamChiTiet.getMauSac() != null ?
               sanPhamChiTiet.getMauSac().getTenMauSac() : "";
    }

    public String getTenKichCo() {
        return sanPhamChiTiet != null && sanPhamChiTiet.getKichCo() != null ?
               sanPhamChiTiet.getKichCo().getTenKichCo() : "";
    }
}
