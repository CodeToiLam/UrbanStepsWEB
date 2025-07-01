package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.SanPham;

import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham,Integer> {
    // Lấy top 5 sản phẩm (cho compatibility cũ)
    List<SanPham> findTop5ByTrangThaiOrderByGiaBanAsc(Boolean trangThai);
    List<SanPham> findTop5ByTrangThaiOrderByGiaBanDesc(Boolean trangThai);
    
    // Lấy TẤT CẢ sản phẩm có sắp xếp - TỐI ƯU với JOIN FETCH để tránh N+1 query
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai ORDER BY sp.giaBan ASC")
    List<SanPham> findByTrangThaiOrderByGiaBanAsc(@Param("trangThai") Boolean trangThai);
    
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai ORDER BY sp.giaBan DESC")
    List<SanPham> findByTrangThaiOrderByGiaBanDesc(@Param("trangThai") Boolean trangThai);
    
    // Tất cả sản phẩm - TỐI ƯU với JOIN FETCH
    @Query("SELECT DISTINCT sp FROM SanPham sp LEFT JOIN FETCH sp.idHinhAnhDaiDien WHERE sp.trangThai = :trangThai")
    List<SanPham> findByTrangThai(@Param("trangThai") Boolean trangThai);
    
    // Tìm theo thương hiệu
    @Query("SELECT s FROM SanPham s WHERE s.thuongHieu.tenThuongHieu = :thuongHieu AND s.trangThai = :trangThai")
    List<SanPham> findByThuongHieuTenThuongHieuAndTrangThai(@Param("thuongHieu") String thuongHieu, @Param("trangThai") Boolean trangThai);
    
    // Sản phẩm sale (giá thấp)
    List<SanPham> findByTrangThaiAndGiaBanLessThanOrderByGiaBanAsc(Boolean trangThai, Integer giaBan);
    
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
