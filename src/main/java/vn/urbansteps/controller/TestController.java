package vn.urbansteps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.model.ThuongHieu;
import vn.urbansteps.repository.SanPhamRepository;
import vn.urbansteps.repository.ThuongHieuRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TestController {

    @Autowired
    private SanPhamRepository sanPhamRepository;
    
    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    @GetMapping("/test/create-sample-data")
    @ResponseBody
    public String createSampleData() {
        try {
            // Tạo thương hiệu mẫu nếu chưa có
            if (thuongHieuRepository.count() == 0) {
                ThuongHieu nike = new ThuongHieu();
                nike.setTenThuongHieu("Nike");
                thuongHieuRepository.save(nike);

                ThuongHieu adidas = new ThuongHieu();
                adidas.setTenThuongHieu("Adidas");
                thuongHieuRepository.save(adidas);

                ThuongHieu converse = new ThuongHieu();
                converse.setTenThuongHieu("Converse");
                thuongHieuRepository.save(converse);
            }

            // Tạo sản phẩm mẫu
            if (sanPhamRepository.count() == 0) {
                List<ThuongHieu> thuongHieus = thuongHieuRepository.findAll();
                
                for (int i = 1; i <= 10; i++) {
                    SanPham sanPham = new SanPham();
                    sanPham.setMaSanPham("SP" + String.format("%03d", i));
                    sanPham.setTenSanPham("Giày thể thao Urban " + i);
                    sanPham.setMoTa("Mô tả sản phẩm " + i);
                    sanPham.setGiaNhap(new BigDecimal(500000 + (i * 50000)));
                    sanPham.setGiaBan(new BigDecimal(800000 + (i * 100000)));
                    sanPham.setTrangThai(true);
                    sanPham.setCreateAt(LocalDateTime.now());
                    
                    // Gán thương hiệu ngẫu nhiên
                    if (!thuongHieus.isEmpty()) {
                        sanPham.setThuongHieu(thuongHieus.get(i % thuongHieus.size()));
                    }
                    
                    sanPhamRepository.save(sanPham);
                }
            }

            long sanPhamCount = sanPhamRepository.count();
            long thuongHieuCount = thuongHieuRepository.count();
            
            return "Sample data created successfully!<br>" +
                   "Thương hiệu: " + thuongHieuCount + "<br>" +
                   "Sản phẩm: " + sanPhamCount + "<br>" +
                   "<a href='/'>Về trang chủ</a>";
                   
        } catch (Exception e) {
            return "Error creating sample data: " + e.getMessage();
        }
    }
    
    @GetMapping("/test/check-data")
    @ResponseBody
    public String checkData() {
        try {
            long sanPhamCount = sanPhamRepository.count();
            long sanPhamActiveCount = sanPhamRepository.countByTrangThai(true);
            long thuongHieuCount = thuongHieuRepository.count();
            
            List<SanPham> allProducts = sanPhamRepository.findAll();
            StringBuilder sb = new StringBuilder();
            sb.append("Database Status:<br>");
            sb.append("Total Sản phẩm: ").append(sanPhamCount).append("<br>");
            sb.append("Active Sản phẩm: ").append(sanPhamActiveCount).append("<br>");
            sb.append("Total Thương hiệu: ").append(thuongHieuCount).append("<br><br>");
            
            sb.append("All Products:<br>");
            for (SanPham sp : allProducts) {
                sb.append("- ").append(sp.getTenSanPham())
                  .append(" (").append(sp.getMaSanPham()).append(")")
                  .append(" - Trạng thái: ").append(sp.getTrangThai())
                  .append(" - Giá: ").append(sp.getGiaBan()).append("<br>");
            }
            
            sb.append("<br><a href='/'>Về trang chủ</a>");
            return sb.toString();
            
        } catch (Exception e) {
            return "Error checking data: " + e.getMessage();
        }
    }
}
