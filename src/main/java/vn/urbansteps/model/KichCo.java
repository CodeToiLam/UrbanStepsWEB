package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "KichCo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KichCo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_kich_co", nullable = false)
    private String tenKichCo;
}