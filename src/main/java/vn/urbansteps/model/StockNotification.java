package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "StockNotification")
public class StockNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private TaiKhoan taiKhoan;
    
    @Column(name = "email")
    private String email;
    
    @ManyToOne
    @JoinColumn(name = "san_pham_chi_tiet_id")
    private SanPhamChiTiet sanPhamChiTiet;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "notified")
    private boolean notified;
    
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        notified = false;
    }
}
