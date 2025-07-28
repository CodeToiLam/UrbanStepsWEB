package vn.urbansteps.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoPagesController {

    @GetMapping("/ho-tro")
    public String hoTro() {
        return "ho-tro-khach-hang/ho-tro";
    }

    @GetMapping("/van-don")
    public String vanDon() {
        return "ho-tro-khach-hang/van-don";
    }

    @GetMapping("/huong-dan-tra-gop")
    public String huongDanTraGop() {
        return "ho-tro-khach-hang/huong-dan-tra-gop";
    }

    @GetMapping("/huong-dan-doi-tra")
    public String huongDanDoiTra() {
        return "ho-tro-khach-hang/huong-dan-doi-tra";
    }
    @GetMapping("/chinh-sach-doi-tra")
    public String chinhSachDoiTra() {
        return "ho-tro-khach-hang/chinh-sach-doi-tra";
    }

    @GetMapping("/chinh-sach-bao-hanh")
    public String chinhSachBaoHanh() {
        return "ho-tro-khach-hang/chinh-sach-bao-hanh";
    }

    @GetMapping("/gioi-thieu")
    public String gioiThieu() {
        return "ho-tro-khach-hang/gioi-thieu";
    }

    @GetMapping("/cau-hoi-thuong-gap")
    public String cauHoiThuongGap() {
        return "ho-tro-khach-hang/cau-hoi-thuong-gap";
    }

    @GetMapping("/tuyen-dung")
    public String tuyenDung() {
        return "ho-tro-khach-hang/tuyen-dung";
    }

    @GetMapping("/dang-ky-ban-hang")
    public String dangKyBanHangForm() {
        return "ho-tro-khach-hang/dang-ky-ban-hang";
    }

    @GetMapping("/lien-he")
    public String lienHeForm() {
        return "ho-tro-khach-hang/lien-he";
    }
}
