package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.SanPham;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    // Lấy top 5 sản phẩm (cho compatibility cũ)
    List<SanPham> findTop5ByTrangThaiOrderByGiaBanAsc(Boolean trangThai);
    List<SanPham> findTop5ByTrangThaiOrderByGiaBanDesc(Boolean trangThai);
    // Lấy TẤT CẢ sản phẩm có sắp xếp - TỐI ƯU với JOIN FETCH để tránh N+1 query
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai ORDER BY sp.giaBan ASC")
    List<SanPham> findByTrangThaiOrderByGiaBanAsc(@Param("trangThai") Boolean trangThai);

    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai ORDER BY sp.giaBan DESC")
    List<SanPham> findByTrangThaiOrderByGiaBanDesc(@Param("trangThai") Boolean trangThai);

    // Tất cả sản phẩm - TỐT ƯU với JOIN FETCH
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai")
    List<SanPham> findByTrangThai(@Param("trangThai") Boolean trangThai);

    // Tìm theo thương hiệu
    @Query("SELECT s FROM SanPham s WHERE s.thuongHieu.tenThuongHieu = :tenThuongHieu AND s.trangThai = :trangThai")
    List<SanPham> findByThuongHieuTenThuongHieuAndTrangThai(@Param("tenThuongHieu") String tenThuongHieu, @Param("trangThai") Boolean trangThai);

    // Tìm theo danh mục
    @Query("SELECT s FROM SanPham s WHERE s.danhMuc.tenDanhMuc = :tenDanhMuc AND s.trangThai = :trangThai")
    List<SanPham> findByDanhMucTenDanhMucAndTrangThai(@Param("tenDanhMuc") String tenDanhMuc, @Param("trangThai") Boolean trangThai);

    // Tìm theo loại sản phẩm
    @Query("SELECT s FROM SanPham s WHERE s.loaiSanPham.tenLoaiSanPham = :tenLoaiSanPham AND s.trangThai = :trangThai")
    List<SanPham> findByLoaiSanPhamTenLoaiSanPhamAndTrangThai(@Param("tenLoaiSanPham") String tenLoaiSanPham, @Param("trangThai") Boolean trangThai);

    // Tìm theo xuất xứ
    @Query("SELECT s FROM SanPham s WHERE s.xuatXu.tenXuatXu = :tenXuatXu AND s.trangThai = :trangThai")
    List<SanPham> findByXuatXuTenXuatXuAndTrangThai(@Param("tenXuatXu") String tenXuatXu, @Param("trangThai") Boolean trangThai);

    // Tìm theo kiểu dáng
    @Query("SELECT s FROM SanPham s WHERE s.kieuDang.tenKieuDang = :tenKieuDang AND s.trangThai = :trangThai")
    List<SanPham> findByKieuDangTenKieuDangAndTrangThai(@Param("tenKieuDang") String tenKieuDang, @Param("trangThai") Boolean trangThai);

    // Tìm theo chất liệu
    @Query("SELECT s FROM SanPham s WHERE s.chatLieu.tenChatLieu = :tenChatLieu AND s.trangThai = :trangThai")
    List<SanPham> findByChatLieuTenChatLieuAndTrangThai(@Param("tenChatLieu") String tenChatLieu, @Param("trangThai") Boolean trangThai);

    // Sản phẩm sale (giá thấp) - Sửa thành BigDecimal
    List<SanPham> findByTrangThaiAndGiaBanLessThanOrderByGiaBanAsc(Boolean trangThai, BigDecimal giaBan);

    // Sản phẩm hot (giá cao)
    List<SanPham> findTop10ByTrangThaiOrderByGiaBanDesc(Boolean trangThai);

    // Sản phẩm liên quan (loại trừ sản phẩm hiện tại)
    List<SanPham> findTop4ByTrangThaiAndIdNotOrderByGiaBanAsc(Boolean trangThai, Integer id);

    // Đếm số sản phẩm theo trạng thái
    long countByTrangThai(Boolean trangThai);

    // Tìm kiếm sản phẩm theo tên
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.tenSanPham LIKE %:keyword% AND sp.trangThai = :trangThai")
    List<SanPham> findByTenSanPhamContainingIgnoreCaseAndTrangThai(@Param("keyword") String keyword, @Param("trangThai") Boolean trangThai);

    // Tìm sản phẩm hot
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.laHot = true AND sp.trangThai = :trangThai ORDER BY sp.luotXem DESC, sp.diemDanhGia DESC")
    List<SanPham> findHotProducts(@Param("trangThai") Boolean trangThai);

    // Tìm sản phẩm sale
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.laSale = true AND sp.phanTramGiam > 0 AND sp.trangThai = :trangThai ORDER BY sp.phanTramGiam DESC")
    List<SanPham> findSaleProducts(@Param("trangThai") Boolean trangThai);

    // Sản phẩm bán chạy
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai ORDER BY sp.luotBan DESC, sp.luotXem DESC")
    List<SanPham> findBestSellingProducts(@Param("trangThai") Boolean trangThai);

    // Sản phẩm xem nhiều
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai ORDER BY sp.luotXem DESC")
    List<SanPham> findMostViewedProducts(@Param("trangThai") Boolean trangThai);

    // Sản phẩm đánh giá cao
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.diemDanhGia >= :minRating AND sp.soLuongDanhGia >= :minReviews AND sp.trangThai = :trangThai ORDER BY sp.diemDanhGia DESC, sp.soLuongDanhGia DESC")
    List<SanPham> findHighRatedProducts(@Param("minRating") BigDecimal minRating, @Param("minReviews") Integer minReviews, @Param("trangThai") Boolean trangThai);

    // Tìm theo khoảng giá sau giảm
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai AND " +
           "((sp.phanTramGiam = 0 AND sp.giaBan BETWEEN :minPrice AND :maxPrice) OR " +
           "(sp.phanTramGiam > 0 AND (sp.giaBan * (100 - sp.phanTramGiam) / 100) BETWEEN :minPrice AND :maxPrice))")
    List<SanPham> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("trangThai") Boolean trangThai);

    // Tìm sản phẩm mới (theo ngày tạo)
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai ORDER BY sp.createAt DESC")
    List<SanPham> findNewProducts(@Param("trangThai") Boolean trangThai);

    // Cập nhật lượt xem
    @Query("UPDATE SanPham sp SET sp.luotXem = sp.luotXem + 1, sp.updateAt = CURRENT_TIMESTAMP WHERE sp.id = :id")
    void incrementLuotXem(@Param("id") Integer id);

    // Cập nhật lượt bán
    @Query("UPDATE SanPham sp SET sp.luotBan = sp.luotBan + :quantity, sp.updateAt = CURRENT_TIMESTAMP WHERE sp.id = :id")
    void incrementLuotBan(@Param("id") Integer id, @Param("quantity") Integer quantity);

    // Cập nhật điểm đánh giá
    @Query("UPDATE SanPham sp SET sp.diemDanhGia = :newRating, sp.soLuongDanhGia = :totalReviews, sp.updateAt = CURRENT_TIMESTAMP WHERE sp.id = :id")
    void updateRating(@Param("id") Integer id, @Param("newRating") BigDecimal newRating, @Param("totalReviews") Integer totalReviews);

    // Tìm kiếm nâng cao với nhiều bộ lọc
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.idHinhAnhDaiDien " +
           "WHERE sp.trangThai = :trangThai " +
           "AND (:thuongHieuId IS NULL OR sp.thuongHieu.id = :thuongHieuId) " +
           "AND (:danhMucId IS NULL OR sp.danhMuc.id = :danhMucId) " +
           "AND (:minPrice IS NULL OR " +
           "    ((sp.phanTramGiam = 0 AND sp.giaBan >= :minPrice) OR " +
           "     (sp.phanTramGiam > 0 AND (sp.giaBan * (100 - sp.phanTramGiam) / 100) >= :minPrice))) " +
           "AND (:maxPrice IS NULL OR " +
           "    ((sp.phanTramGiam = 0 AND sp.giaBan <= :maxPrice) OR " +
           "     (sp.phanTramGiam > 0 AND (sp.giaBan * (100 - sp.phanTramGiam) / 100) <= :maxPrice))) " +
           "AND (:isHot IS NULL OR sp.laHot = :isHot) " +
           "AND (:isSale IS NULL OR sp.laSale = :isSale)")
    List<SanPham> findWithFilters(@Param("trangThai") Boolean trangThai,
                                  @Param("thuongHieuId") Integer thuongHieuId,
                                  @Param("danhMucId") Integer danhMucId,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("isHot") Boolean isHot,
                                  @Param("isSale") Boolean isSale);
}