package vn.urbansteps.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.urbansteps.model.*;
import vn.urbansteps.repository.DanhGiaSanPhamRepository;
import vn.urbansteps.repository.HoaDonChiTietRepository;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.repository.SanPhamRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DanhGiaSanPhamService {

    private final DanhGiaSanPhamRepository danhGiaSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final FileUploadService fileUploadService;
    private final HoaDonService hoaDonService;

    public List<DanhGiaSanPham> getDanhGiaTheoSanPham(Integer sanPhamId) {
        return danhGiaSanPhamRepository.findBySanPham_IdAndTrangThaiOrderByCreateAtDesc(sanPhamId, true);
    }

    public List<DanhGiaSanPham> getDanhGiaTheoTaiKhoan(Integer taiKhoanId) {
        return danhGiaSanPhamRepository.findByTaiKhoan_IdOrderByCreateAtDesc(taiKhoanId);
    }

    public BigDecimal getDiemTrungBinhTheoSanPham(Integer sanPhamId) {
        return danhGiaSanPhamRepository.getAverageRatingBySanPham(sanPhamId);
    }

    @Transactional
    public DanhGiaSanPham themDanhGia(
            Integer sanPhamId,
            Integer hoaDonChiTietId,
            TaiKhoan taiKhoan,
            Byte diemDanhGia,
            String tieuDe,
            String noiDung,
            List<MultipartFile> hinhAnhFiles) throws IOException {

        // Kiểm tra điểm đánh giá hợp lệ
        if (diemDanhGia < 1 || diemDanhGia > 5) {
            throw new IllegalArgumentException("Điểm đánh giá phải từ 1 đến 5 sao");
        }

        // Lấy chi tiết hóa đơn
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository.findById(hoaDonChiTietId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi tiết đơn hàng"));

        // Lấy hóa đơn gốc
        HoaDon hoaDon = hoaDonChiTiet.getHoaDon();
        if (hoaDon == null || hoaDon.getTrangThai() != 3) { // 3 = đã hoàn thành
            throw new IllegalStateException("Chỉ được đánh giá sản phẩm khi đơn hàng đã hoàn thành");
        }

        // Kiểm tra đã đánh giá sản phẩm này chưa
        if (danhGiaSanPhamRepository.existsByTaiKhoan_IdAndHoaDonChiTiet_Id(taiKhoan.getId(), hoaDonChiTietId)) {
            throw new IllegalStateException("Bạn đã đánh giá sản phẩm này");
        }

        // Lấy thông tin sản phẩm
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        // Tạo đánh giá mới
        DanhGiaSanPham danhGia = new DanhGiaSanPham();
        danhGia.setSanPham(sanPham);
        danhGia.setTaiKhoan(taiKhoan);
        danhGia.setHoaDonChiTiet(hoaDonChiTiet);
        danhGia.setDiemDanhGia(diemDanhGia);
        danhGia.setTieuDe(tieuDe);
        danhGia.setNoiDung(noiDung);

        // Upload hình ảnh (tối đa 3 ảnh)
        if (hinhAnhFiles != null && !hinhAnhFiles.isEmpty()) {
            for (int i = 0; i < Math.min(hinhAnhFiles.size(), 3); i++) {
                MultipartFile file = hinhAnhFiles.get(i);
                if (file != null && !file.isEmpty()) {
                    String imagePath = fileUploadService.uploadFile(file, "danh-gia");

                    if (i == 0) {
                        danhGia.setHinhAnh1(imagePath);
                    } else if (i == 1) {
                        danhGia.setHinhAnh2(imagePath);
                    } else if (i == 2) {
                        danhGia.setHinhAnh3(imagePath);
                    }
                }
            }
        }

        return danhGiaSanPhamRepository.save(danhGia);
    }


    public Map<Integer, Long> getThongKeDanhGiaTheoSanPham(Integer sanPhamId) {
        List<Object[]> distribution = danhGiaSanPhamRepository.getRatingDistribution(sanPhamId);
        Map<Integer, Long> result = new HashMap<>();

        for (Object[] row : distribution) {
            Integer rating = ((Number) row[0]).intValue();
            Long count = (Long) row[1];
            result.put(rating, count);
        }

        // Đảm bảo tất cả các mức đánh giá đều có giá trị
        for (int i = 1; i <= 5; i++) {
            if (!result.containsKey(i)) {
                result.put(i, 0L);
            }
        }

        return result;
    }

    public boolean kiemTraDaDanhGia(Integer taiKhoanId, Integer hoaDonChiTietId) {
        return danhGiaSanPhamRepository.existsByTaiKhoan_IdAndHoaDonChiTiet_Id(taiKhoanId, hoaDonChiTietId);
    }

    public List<DanhGiaSanPham> getDanhGiaCoHinhAnh(Integer sanPhamId) {
        return danhGiaSanPhamRepository.findReviewsWithImages(sanPhamId);
    }


}