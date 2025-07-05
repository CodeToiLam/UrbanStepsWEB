package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.SanPham;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
}