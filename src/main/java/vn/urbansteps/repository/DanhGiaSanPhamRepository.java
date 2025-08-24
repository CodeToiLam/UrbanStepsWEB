package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.DanhGiaSanPham;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DanhGiaSanPhamRepository extends JpaRepository<DanhGiaSanPham, Integer> {

    // Tìm đánh giá theo sản phẩm
    List<DanhGiaSanPham> findBySanPham_IdAndTrangThaiOrderByCreateAtDesc(Integer sanPhamId, Boolean trangThai);

    // Tìm đánh giá theo tài khoản
    List<DanhGiaSanPham> findByTaiKhoan_IdOrderByCreateAtDesc(Integer taiKhoanId);

    // Tìm đánh giá theo điểm
    List<DanhGiaSanPham> findBySanPham_IdAndDiemDanhGiaAndTrangThaiOrderByCreateAtDesc(Integer sanPhamId, Integer diemDanhGia, Boolean trangThai);

    // Đếm số đánh giá theo sản phẩm
    long countBySanPham_IdAndTrangThai(Integer sanPhamId, Boolean trangThai);

    // Tính điểm trung bình theo sản phẩm
    @Query("SELECT AVG(dg.diemDanhGia) FROM DanhGiaSanPham dg WHERE dg.sanPham.id = :sanPhamId AND dg.trangThai = true")
    BigDecimal getAverageRatingBySanPham(@Param("sanPhamId") Integer sanPhamId);

    // Thống kê đánh giá theo điểm
    @Query("SELECT dg.diemDanhGia, COUNT(dg) FROM DanhGiaSanPham dg " +
            "WHERE dg.sanPham.id = :sanPhamId AND dg.trangThai = true " +
            "GROUP BY dg.diemDanhGia ORDER BY dg.diemDanhGia DESC")
    List<Object[]> getRatingDistribution(@Param("sanPhamId") Integer sanPhamId);

    // Tìm đánh giá có hình ảnh
    @Query("SELECT dg FROM DanhGiaSanPham dg WHERE dg.sanPham.id = :sanPhamId AND dg.trangThai = true " +
            "AND (dg.hinhAnh1 IS NOT NULL OR dg.hinhAnh2 IS NOT NULL OR dg.hinhAnh3 IS NOT NULL) " +
            "ORDER BY dg.createAt DESC")
    List<DanhGiaSanPham> findReviewsWithImages(@Param("sanPhamId") Integer sanPhamId);

    // Tìm đánh giá tích cực (4-5 sao)
    @Query("SELECT dg FROM DanhGiaSanPham dg WHERE dg.sanPham.id = :sanPhamId AND dg.trangThai = true " +
            "AND dg.diemDanhGia >= 4 ORDER BY dg.createAt DESC")
    List<DanhGiaSanPham> findPositiveReviews(@Param("sanPhamId") Integer sanPhamId);

    // Tìm đánh giá tiêu cực (1-2 sao)
    @Query("SELECT dg FROM DanhGiaSanPham dg WHERE dg.sanPham.id = :sanPhamId AND dg.trangThai = true " +
            "AND dg.diemDanhGia <= 2 ORDER BY dg.createAt DESC")
    List<DanhGiaSanPham> findNegativeReviews(@Param("sanPhamId") Integer sanPhamId);

    // Kiểm tra đã đánh giá chưa
    boolean existsByTaiKhoan_IdAndHoaDonChiTiet_Id(Integer taiKhoanId, Integer hoaDonChiTietId);

    // Top sản phẩm được đánh giá cao
    @Query("SELECT dg.sanPham, AVG(dg.diemDanhGia) as avgRating, COUNT(dg) as reviewCount " +
            "FROM DanhGiaSanPham dg WHERE dg.trangThai = true " +
            "GROUP BY dg.sanPham HAVING COUNT(dg) >= :minReviews " +
            "ORDER BY AVG(dg.diemDanhGia) DESC, COUNT(dg) DESC")
    List<Object[]> getTopRatedProducts(@Param("minReviews") Long minReviews);

    // Đánh giá mới nhất
    List<DanhGiaSanPham> findTop10ByTrangThaiOrderByCreateAtDesc(Boolean trangThai);
    
       // Find reviews for a product without filtering by trangThai (fallback)
       List<DanhGiaSanPham> findBySanPham_IdOrderByCreateAtDesc(Integer sanPhamId);
}