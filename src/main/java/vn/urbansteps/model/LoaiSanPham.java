package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LoaiSanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_loai_san_pham", nullable = false)
    private String tenLoaiSanPham;
}
