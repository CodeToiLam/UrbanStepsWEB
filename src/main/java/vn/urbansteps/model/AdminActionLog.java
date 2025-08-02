package vn.urbansteps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "ThongKeHanhDongAdmin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminActionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "admin_id")
    private Integer adminId;

    @Column(name = "hanh_dong")
    private String hanhDong;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian = LocalDateTime.now();

    public AdminActionLog(Integer adminId, String hanhDong, String moTa) {
        this.adminId = adminId;
        this.hanhDong = hanhDong;
        this.moTa = moTa;
        this.thoiGian = LocalDateTime.now();
    }

}
