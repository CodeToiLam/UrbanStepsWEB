package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SanPhamChiTiet")
public class SanPhamChiTiet {
    // Enum for stock status
    public enum StockStatus {
        IN_STOCK,        // Có sẵn
        LOW_STOCK,       // Sắp hết hàng (dưới ngưỡng cảnh báo)
        OUT_OF_STOCK,    // Hết hàng
        DISCONTINUED,    // Ngừng kinh doanh
        COMING_SOON      // Sắp có hàng
    }
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
    
    @Column(name = "gia_cu")
    private BigDecimal giaCu;

    @Column(name = "ngay_thay_doi_gia")
    private LocalDateTime ngayThayDoiGia;

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
    
    public StockStatus getStockStatus() {
        if (this.deleteAt != null) {
            return StockStatus.DISCONTINUED;
        }
        
        if (this.soLuong == null || this.soLuong <= 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        
        // Ngưỡng cảnh báo sắp hết hàng (có thể cấu hình theo từng danh mục)
        int lowStockThreshold = 5;
        if (this.soLuong <= lowStockThreshold) {
            return StockStatus.LOW_STOCK;
        }
        
        return StockStatus.IN_STOCK;
    }
    
    public boolean isOnSale() {
        return giaCu != null && giaCu.compareTo(giaBanLe) > 0;
    }

    public BigDecimal getDiscountPercentage() {
        if (!isOnSale()) return BigDecimal.ZERO;
        
        BigDecimal discount = giaCu.subtract(giaBanLe);
        return discount.multiply(new BigDecimal("100")).divide(giaCu, 0, RoundingMode.HALF_UP);
    }

    public void updatePrice(BigDecimal newPrice) {
        if (this.giaBanLe != null && !this.giaBanLe.equals(newPrice)) {
            this.giaCu = this.giaBanLe;
            this.giaBanLe = newPrice;
            this.ngayThayDoiGia = LocalDateTime.now();
        } else {
            this.giaBanLe = newPrice;
        }
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