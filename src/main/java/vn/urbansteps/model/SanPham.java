package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SanPham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_anh_dai_dien")
    private HinhAnh idHinhAnhDaiDien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu")
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_san_pham")
    private LoaiSanPham loaiSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc")
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_xuat_xu")
    private XuatXu xuatXu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_dang")
    private KieuDang kieuDang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieu chatLieu;

    @Column(name = "ma_san_pham", nullable = false, unique = true)
    private String maSanPham;

    @Column(name = "ten_san_pham", nullable = false)
    private String tenSanPham;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "gia_nhap", nullable = false)
    private BigDecimal giaNhap;

    @Column(name = "gia_ban", nullable = false)
    private BigDecimal giaBan;

    @Column(name = "la_hot")
    private Boolean laHot = false;

    @Column(name = "la_sale")
    private Boolean laSale = false;

    @Column(name = "phan_tram_giam")
    private BigDecimal phanTramGiam = BigDecimal.ZERO;

    @Column(name = "luot_xem")
    private Integer luotXem = 0;

    @Column(name = "luot_ban")
    private Integer luotBan = 0;

    @Column(name = "diem_danh_gia")
    private BigDecimal diemDanhGia = BigDecimal.ZERO;

    @Column(name = "so_luong_danh_gia")
    private Integer soLuongDanhGia = 0;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    // Optional external marketplace URLs (transient until DB migration is added)
    @Transient
    private String shopeeUrl;

    @Transient
    private String lazadaUrl;

    @Transient
    private String facebookShopUrl;

    // Utility methods
    public BigDecimal getGiaSauGiam() {
        if (phanTramGiam == null || phanTramGiam.compareTo(BigDecimal.ZERO) == 0) {
            return giaBan;
        }
        BigDecimal phanTramConLai = BigDecimal.valueOf(100).subtract(phanTramGiam);
        return giaBan.multiply(phanTramConLai).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTienTietKiem() {
        if (phanTramGiam == null || phanTramGiam.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return giaBan.multiply(phanTramGiam).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public Boolean isHot() {
        return laHot != null && laHot;
    }

    public Boolean isSale() {
        return laSale != null && laSale;
    }

    public String getDiemDanhGiaFormatted() {
        if (diemDanhGia == null || diemDanhGia.compareTo(BigDecimal.ZERO) == 0) {
            return "0.0";
        }
        return String.format("%.1f", diemDanhGia);
    }

    @PrePersist
    public void prePersist() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        if (laHot == null) laHot = false;
        if (laSale == null) laSale = false;
        if (phanTramGiam == null) phanTramGiam = BigDecimal.ZERO;
        if (luotXem == null) luotXem = 0;
        if (luotBan == null) luotBan = 0;
        if (diemDanhGia == null) diemDanhGia = BigDecimal.ZERO;
        if (soLuongDanhGia == null) soLuongDanhGia = 0;
        if (trangThai == null) trangThai = true;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }
}