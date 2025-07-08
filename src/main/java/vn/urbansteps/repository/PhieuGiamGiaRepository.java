package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.PhieuGiamGia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {
    
    // Tìm theo mã phiếu giảm giá
    Optional<PhieuGiamGia> findByMaPhieuGiamGia(String maPhieuGiamGia);
    
    // Tìm phiếu giảm giá đang hoạt động
    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.trangThai = true " +
           "AND pg.ngayBatDau <= :now AND pg.ngayKetThuc >= :now " +
           "AND (pg.soLuong IS NULL OR pg.soLuongDaSuDung < pg.soLuong)")
    List<PhieuGiamGia> findActiveVouchers(@Param("now") LocalDateTime now);
    
    // Tìm phiếu giảm giá khả dụng cho mã cụ thể
    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.maPhieuGiamGia = :ma " +
           "AND pg.trangThai = true " +
           "AND pg.ngayBatDau <= :now AND pg.ngayKetThuc >= :now " +
           "AND (pg.soLuong IS NULL OR pg.soLuongDaSuDung < pg.soLuong)")
    Optional<PhieuGiamGia> findValidVoucherByCode(@Param("ma") String ma, @Param("now") LocalDateTime now);
    
    // Tìm theo trạng thái
    List<PhieuGiamGia> findByTrangThai(Boolean trangThai);
    
    // Tìm phiếu sắp hết hạn
    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.trangThai = true " +
           "AND pg.ngayKetThuc BETWEEN :now AND :endDate")
    List<PhieuGiamGia> findExpiringSoon(@Param("now") LocalDateTime now, @Param("endDate") LocalDateTime endDate);
    
    // Tìm phiếu sắp hết số lượng
    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.trangThai = true " +
           "AND pg.soLuong IS NOT NULL " +
           "AND (pg.soLuong - pg.soLuongDaSuDung) <= :threshold")
    List<PhieuGiamGia> findLowQuantity(@Param("threshold") Integer threshold);
    
    // Tìm theo loại giảm giá
    List<PhieuGiamGia> findByGiamTheoPhanTramAndTrangThai(Boolean giamTheoPhanTram, Boolean trangThai);
    
    // Thống kê phiếu giảm giá theo tháng
    @Query("SELECT MONTH(pg.createAt) as month, COUNT(pg) as count " +
           "FROM PhieuGiamGia pg WHERE YEAR(pg.createAt) = :year " +
           "GROUP BY MONTH(pg.createAt) ORDER BY MONTH(pg.createAt)")
    List<Object[]> getMonthlyStatistics(@Param("year") Integer year);
    
    // Cập nhật số lượng đã sử dụng
    @Query("UPDATE PhieuGiamGia pg SET pg.soLuongDaSuDung = pg.soLuongDaSuDung + 1 WHERE pg.id = :id")
    void incrementUsageCount(@Param("id") Integer id);
}
