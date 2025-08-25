package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "TaiKhoan_PhieuGiamGia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanPhieuGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_khoan_id")
    private TaiKhoan taiKhoan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phieu_giam_gia_id")
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt = LocalDateTime.now();

    @Column(name = "used")
    private Boolean used = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
