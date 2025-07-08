package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.SanPhamYeuThich;

import java.util.List;

@Repository
public interface SanPhamYeuThichRepository extends JpaRepository<SanPhamYeuThich, Integer> {
    
    // Tìm sản phẩm yêu thích của user
    List<SanPhamYeuThich> findByTaiKhoan_IdOrderByCreateAtDesc(Integer taiKhoanId);
    
    // Kiểm tra đã thích chưa
    boolean existsByTaiKhoan_IdAndSanPham_Id(Integer taiKhoanId, Integer sanPhamId);
    
    // Xóa sản phẩm yêu thích
    void deleteByTaiKhoan_IdAndSanPham_Id(Integer taiKhoanId, Integer sanPhamId);
    
    // Đếm số lượt thích của sản phẩm
    long countBySanPham_Id(Integer sanPhamId);
    
    // Top sản phẩm được yêu thích nhiều nhất
    @Query("SELECT spyt.sanPham, COUNT(spyt) as likeCount " +
           "FROM SanPhamYeuThich spyt " +
           "GROUP BY spyt.sanPham ORDER BY COUNT(spyt) DESC")
    List<Object[]> getMostLikedProducts();
    
    // Đếm số sản phẩm yêu thích của user
    long countByTaiKhoan_Id(Integer taiKhoanId);
}
