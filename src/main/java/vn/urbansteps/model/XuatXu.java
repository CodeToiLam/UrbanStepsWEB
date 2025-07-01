package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "XuatXu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XuatXu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_xuat_xu", nullable = false)
    private String tenXuatXu;
}
