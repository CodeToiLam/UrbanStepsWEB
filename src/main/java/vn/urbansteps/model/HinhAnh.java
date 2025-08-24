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

        // Kiểm tra và đảm bảo đường dẫn đúng định dạng
        try {
            String p = this.duongDan.trim();
            if (p.startsWith("/uploads/")) {
                // try prefering static images copy if exists
                String rest = p.substring("/uploads/".length());
                java.nio.file.Path imagesPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
                        .resolve("src/main/resources/static/images").resolve(rest.replace('/', java.io.File.separatorChar));
                if (java.nio.file.Files.exists(imagesPath)) {
                    return "/images/" + rest;
                }
                    java.nio.file.Path projectRoot = java.nio.file.Paths.get(System.getProperty("user.dir"));
                    java.nio.file.Path[] candidates = new java.nio.file.Path[] {
                            projectRoot.resolve("src/main/resources/static/images").resolve(rest.replace('/', java.io.File.separatorChar)),
                            projectRoot.resolve("target/classes/static/images").resolve(rest.replace('/', java.io.File.separatorChar)),
                            projectRoot.resolve("uploads").resolve(rest.replace('/', java.io.File.separatorChar))
                    };
                    for (java.nio.file.Path candidate : candidates) {
                        if (java.nio.file.Files.exists(candidate)) {
                            if (candidate.toString().contains(java.io.File.separator + "images" + java.io.File.separator) || candidate.toString().contains("/images/")) {
                                return "/images/" + rest;
                            }
                            return p; // return original uploads path if that's the one that exists
                        }
                    }
            }
            if (p.startsWith("/images/")) return p;
            if (p.startsWith("images/")) return "/" + p;
            return "/images/" + p;
        } catch (Exception ignored) {
            if (this.duongDan.startsWith("/images/")) return this.duongDan;
            if (this.duongDan.startsWith("images/")) return "/" + this.duongDan;
            return "/images/" + this.duongDan;
        }
    }

}
