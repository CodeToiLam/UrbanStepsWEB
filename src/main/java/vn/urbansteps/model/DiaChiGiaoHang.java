package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "DiaChiGiaoHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiGiaoHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan", nullable = false)
    private TaiKhoan taiKhoan;

    @Column(name = "ten_nguoi_nhan", nullable = false, length = 255)
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan", nullable = false, length = 20)
    private String sdtNguoiNhan;

    @Column(name = "dia_chi_chi_tiet", nullable = false, length = 500)
    private String diaChiChiTiet;

    @Column(name = "phuong_xa", length = 100)
    private String phuongXa;

    @Column(name = "quan_huyen", length = 100)
    private String quanHuyen;

    @Column(name = "tinh_thanh_pho", length = 100)
    private String tinhThanhPho;

    // ...existing code...

    // Utility methods
    public String getDiaChiDayDu() {
        StringBuilder fullAddress = new StringBuilder();
        
        if (diaChiChiTiet != null && !diaChiChiTiet.trim().isEmpty()) {
            fullAddress.append(diaChiChiTiet.trim());
        }
        
        if (phuongXa != null && !phuongXa.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(phuongXa.trim());
        }
        
        if (quanHuyen != null && !quanHuyen.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(quanHuyen.trim());
        }
        
        if (tinhThanhPho != null && !tinhThanhPho.trim().isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(tinhThanhPho.trim());
        }
        
        return fullAddress.toString();
    }

    public String getThongTinNguoiNhan() {
        return tenNguoiNhan + " - " + sdtNguoiNhan;
    }
}
