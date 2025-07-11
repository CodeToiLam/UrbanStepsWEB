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
@Table(name = "SanPhamChiTiet")
public class SanPhamChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_co")
    private KichCo kichCo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac")
    private MauSac mauSac;
      @Column(name = "so_luong")
    private Integer soLuong;
    
    @Column(name = "gia_ban_le")
    private BigDecimal giaBanLe;

    @Column(name = "trang_thai")
    private Boolean trangThai;
    
    @Column(name = "create_at")
    private LocalDateTime createAt;
    
    @Column(name = "update_at")
    private LocalDateTime updateAt;
    
    @Column(name = "delete_at")
    private LocalDateTime deleteAt;
    
    // Utility methods
    public BigDecimal getGiaBanThucTe() {
        return giaBanLe != null ? giaBanLe : 
               (sanPham != null ? sanPham.getGiaSauGiam() : BigDecimal.ZERO);
    }
    
    public Boolean isAvailable() {
        return trangThai != null && trangThai && soLuong != null && soLuong > 0;
    }
    
    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if (trangThai == null) trangThai = true;
        if (soLuong == null) soLuong = 0;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }
}