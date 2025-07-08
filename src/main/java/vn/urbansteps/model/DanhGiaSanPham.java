package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "DanhGiaSanPham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhGiaSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan", nullable = false)
    private TaiKhoan taiKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don_chi_tiet")
    private HoaDonChiTiet hoaDonChiTiet;

    @Column(name = "diem_danh_gia", nullable = false, columnDefinition = "TINYINT")
    private Byte diemDanhGia; // 1-5 stars

    @Column(name = "tieu_de", length = 255)
    private String tieuDe;

    @Column(name = "noi_dung", length = 1000)
    private String noiDung;

    @Column(name = "hinh_anh_1", length = 255)
    private String hinhAnh1;

    @Column(name = "hinh_anh_2", length = 255)
    private String hinhAnh2;

    @Column(name = "hinh_anh_3", length = 255)
    private String hinhAnh3;

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
        if (trangThai == null) trangThai = true;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }

    // Utility methods
    public Boolean hasImages() {
        return (hinhAnh1 != null && !hinhAnh1.trim().isEmpty()) ||
               (hinhAnh2 != null && !hinhAnh2.trim().isEmpty()) ||
               (hinhAnh3 != null && !hinhAnh3.trim().isEmpty());
    }

    public String getStarDisplay() {
        if (diemDanhGia == null) return "";
        
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= diemDanhGia.intValue()) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public Boolean isPositiveReview() {
        return diemDanhGia != null && diemDanhGia.intValue() >= 4;
    }

    public Boolean isNegativeReview() {
        return diemDanhGia != null && diemDanhGia.intValue() <= 2;
    }
}
