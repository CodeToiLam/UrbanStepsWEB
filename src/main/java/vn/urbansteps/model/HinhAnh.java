package vn.urbansteps.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HinhAnh")
public class HinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "duong_dan", nullable = false, length = 255)
    private String duongDan;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "thu_tu")
    private Integer thuTu;

    @Column(name = "la_anh_chinh")
    private Boolean laAnhChinh;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    public String getFullImagePath() {
        if (this.duongDan == null) return "/images/no-image.jpg";
        String p = this.duongDan.trim();
        // Pass-through for known static roots
    if (p.startsWith("/images/") || p.startsWith("/uploads/")) {
            return p;
        }
    if (p.startsWith("images/") || p.startsWith("uploads/")) {
            return "/" + p;
        }
        // Default legacy fallback
        return "/images/" + p;
    }

}
