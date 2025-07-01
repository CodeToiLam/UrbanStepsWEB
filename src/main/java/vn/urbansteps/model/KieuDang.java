package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "KieuDang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KieuDang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_kieu_dang", nullable = false)
    private String tenKieuDang;
}
