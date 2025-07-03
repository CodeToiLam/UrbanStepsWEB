package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "HinhAnh_SanPhamChiTiet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HinhAnh_SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_hinh_anh")
    private HinhAnh hinhAnh;

    @ManyToOne
    @JoinColumn(name = "id_san_pham_chi_tiet")
    private SanPhamChiTiet sanPhamChiTiet;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

}


