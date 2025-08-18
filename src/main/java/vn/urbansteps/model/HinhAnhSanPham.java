package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HinhAnh_SanPham")
public class HinhAnhSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham")
    private SanPham sanPham;

    // Do NOT cascade REMOVE to parent image to avoid accidental deletions when unlinking
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_anh")
    private HinhAnh hinhAnh;

    @Column(name = "la_anh_chinh")
    private Boolean laAnhChinh;

    @Column(name = "thu_tu")
    private Integer thuTu;
}
