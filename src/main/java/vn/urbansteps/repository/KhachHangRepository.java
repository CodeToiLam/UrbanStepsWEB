package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.KhachHang;
import vn.urbansteps.model.TaiKhoan;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {


    Optional<KhachHang> findByTaiKhoan(TaiKhoan taiKhoan);

    // Tìm khách hàng theo tài khoản
    Optional<KhachHang> findByTaiKhoan_Id(Integer taiKhoanId);
    
    // Tìm khách hàng theo email
    List<KhachHang> findByEmailContainingIgnoreCase(String email);
    
    // Tìm khách hàng theo số điện thoại
    List<KhachHang> findBySdtContaining(String sdt);
    
    // Tìm khách hàng theo tên
    List<KhachHang> findByHoTenKhachHangContainingIgnoreCase(String hoTen);
    
    // Tìm khách vãng lai
    List<KhachHang> findByLaKhachVangLai(Boolean laKhachVangLai);
    
    // Tìm khách hàng đã đăng ký
    @Query("SELECT kh FROM KhachHang kh WHERE kh.taiKhoan IS NOT NULL")
    List<KhachHang> findRegisteredCustomers();
    
    // Tìm khách hàng theo nhiều tiêu chí
    @Query("SELECT kh FROM KhachHang kh WHERE " +
           "(:hoTen IS NULL OR kh.hoTenKhachHang LIKE %:hoTen%) " +
           "AND (:email IS NULL OR kh.email LIKE %:email%) " +
           "AND (:sdt IS NULL OR kh.sdt LIKE %:sdt%) " +
           "AND (:laKhachVangLai IS NULL OR kh.laKhachVangLai = :laKhachVangLai)")
    List<KhachHang> findByMultipleCriteria(@Param("hoTen") String hoTen,
                                          @Param("email") String email,
                                          @Param("sdt") String sdt,
                                          @Param("laKhachVangLai") Boolean laKhachVangLai);
    
    // Đếm số khách hàng đăng ký
    long countByTaiKhoanIsNotNull();
    
    // Đếm số khách vãng lai
    long countByLaKhachVangLai(Boolean laKhachVangLai);
}
