package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GioHangItem")
public class GioHangItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gio_hang")
    private GioHang gioHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_chi_tiet")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia_tai_thoi_diem")
    private BigDecimal giaTaiThoidiem;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    // Utility methods
    public BigDecimal getTongGia() {
        if (giaTaiThoidiem == null || soLuong == null) {
            return BigDecimal.ZERO;
        }
        return giaTaiThoidiem.multiply(BigDecimal.valueOf(soLuong));
    }

    public String getTenSanPham() {
        return sanPhamChiTiet != null && sanPhamChiTiet.getSanPham() != null 
            ? sanPhamChiTiet.getSanPham().getTenSanPham() 
            : "";
    }

    public String getKichCo() {
        return sanPhamChiTiet != null && sanPhamChiTiet.getKichCo() != null 
            ? sanPhamChiTiet.getKichCo().getTenKichCo() 
            : "";
    }

    public String getMauSac() {
        return sanPhamChiTiet != null && sanPhamChiTiet.getMauSac() != null 
            ? sanPhamChiTiet.getMauSac().getTenMauSac() 
            : "";
    }

    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }
}
