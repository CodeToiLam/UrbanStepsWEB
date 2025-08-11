package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.urbansteps.model.DiaChiGiaoHang;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.service.DiaChiGiaoHangService;
import vn.urbansteps.service.TaiKhoanService;

import java.util.List;

@Controller
@RequestMapping("/tai-khoan/dia-chi")
public class DiaChiGiaoHangController {

    @Autowired
    private DiaChiGiaoHangService diaChiService;
    @Autowired
    private TaiKhoanService taiKhoanService;

    private TaiKhoan currentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())){
            return taiKhoanService.findByTaiKhoan(auth.getName());
        }
        return null;
    }

    @GetMapping("/them")
    public String showAddForm(Model model){
        model.addAttribute("address", new DiaChiGiaoHang());
        return "tai-khoan-dia-chi-form";
    }

    @PostMapping("/them")
    public String add(@ModelAttribute("address") DiaChiGiaoHang addr, RedirectAttributes ra){
        TaiKhoan tk = currentUser();
        if(tk==null){return "redirect:/dang-nhap";}
        addr.setTaiKhoan(tk);
        // Auto-fill recipient info if missing (form no longer asks for these fields)
        if(addr.getTenNguoiNhan()==null || addr.getTenNguoiNhan().isBlank()){
            addr.setTenNguoiNhan(tk.getHoTenTaiKhoan()!=null? tk.getHoTenTaiKhoan(): tk.getTaiKhoan());
        }
        if(addr.getSdtNguoiNhan()==null || addr.getSdtNguoiNhan().isBlank()){
            addr.setSdtNguoiNhan(tk.getSdt()!=null? tk.getSdt(): "");
        }
        if(addr.isDefault()){
            // unset others
            List<DiaChiGiaoHang> all = diaChiService.listByTaiKhoan(tk);
            all.forEach(a->{ if(a.isDefault()){ a.setDefault(false); diaChiService.save(a);} });
        }
        diaChiService.save(addr);
        ra.addFlashAttribute("success","Đã thêm địa chỉ");
        return "redirect:/tai-khoan#address";
    }

    @GetMapping("/sua/{id}")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra){
        TaiKhoan tk = currentUser();
        if(tk==null){return "redirect:/dang-nhap";}
        DiaChiGiaoHang dc = diaChiService.findById(id).orElse(null);
        if(dc==null || !dc.getTaiKhoan().getId().equals(tk.getId())){
            ra.addFlashAttribute("error","Không tìm thấy địa chỉ");
            return "redirect:/tai-khoan#address";
        }
        model.addAttribute("address", dc);
        return "tai-khoan-dia-chi-form";
    }

    @PostMapping("/sua/{id}")
    public String update(@PathVariable Integer id, @ModelAttribute("address") DiaChiGiaoHang form, RedirectAttributes ra){
        TaiKhoan tk = currentUser();
        if(tk==null){return "redirect:/dang-nhap";}
        DiaChiGiaoHang dc = diaChiService.findById(id).orElse(null);
        if(dc==null || !dc.getTaiKhoan().getId().equals(tk.getId())){
            ra.addFlashAttribute("error","Không tìm thấy địa chỉ");
            return "redirect:/tai-khoan#address";
        }
    // Keep existing or overwrite with auto values if form omitted
    String ten = form.getTenNguoiNhan();
    String sdt = form.getSdtNguoiNhan();
    if(ten==null || ten.isBlank()) ten = tk.getHoTenTaiKhoan()!=null? tk.getHoTenTaiKhoan(): tk.getTaiKhoan();
    if(sdt==null || sdt.isBlank()) sdt = tk.getSdt()!=null? tk.getSdt(): dc.getSdtNguoiNhan();
    dc.setTenNguoiNhan(ten);
    dc.setSdtNguoiNhan(sdt);
        dc.setDiaChiChiTiet(form.getDiaChiChiTiet());
        dc.setPhuongXa(form.getPhuongXa());
        dc.setQuanHuyen(form.getQuanHuyen());
        dc.setTinhThanhPho(form.getTinhThanhPho());
        if(form.isDefault()){
            diaChiService.listByTaiKhoan(tk).forEach(a->{ if(a.isDefault()){ a.setDefault(false); diaChiService.save(a);} });
            dc.setDefault(true);
        }
        diaChiService.save(dc);
        ra.addFlashAttribute("success","Cập nhật địa chỉ thành công");
        return "redirect:/tai-khoan#address";
    }

    @PostMapping("/mac-dinh/{id}")
    public String setDefault(@PathVariable Integer id, RedirectAttributes ra){
        TaiKhoan tk = currentUser();
        if(tk==null){return "redirect:/dang-nhap";}
        diaChiService.listByTaiKhoan(tk).forEach(a->{ if(a.isDefault()){ a.setDefault(false); diaChiService.save(a);} });
        DiaChiGiaoHang dc = diaChiService.findById(id).orElse(null);
        if(dc!=null && dc.getTaiKhoan().getId().equals(tk.getId())){ dc.setDefault(true); diaChiService.save(dc); ra.addFlashAttribute("success","Đã đặt làm mặc định"); }
        else ra.addFlashAttribute("error","Không tìm thấy địa chỉ");
        return "redirect:/tai-khoan#address";
    }

    @PostMapping("/xoa/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra){
        TaiKhoan tk = currentUser();
        if(tk==null){return "redirect:/dang-nhap";}
        DiaChiGiaoHang dc = diaChiService.findById(id).orElse(null);
        if(dc!=null && dc.getTaiKhoan().getId().equals(tk.getId())){ diaChiService.delete(id); ra.addFlashAttribute("success","Đã xóa địa chỉ"); }
        else ra.addFlashAttribute("error","Không tìm thấy địa chỉ");
        return "redirect:/tai-khoan#address";
    }
}
