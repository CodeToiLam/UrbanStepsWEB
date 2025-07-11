package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.PhieuGiamGia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    Optional<PhieuGiamGia> findByMaPhieuGiamGia(String maPhieuGiamGia);

    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.trangThai = true " +
            "AND pg.ngayBatDau <= :now AND pg.ngayKetThuc >= :now " +
            "AND (pg.soLuong IS NULL OR pg.soLuongDaSuDung < pg.soLuong)")
    List<PhieuGiamGia> findActiveVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.maPhieuGiamGia = :ma " +
            "AND pg.trangThai = true " +
            "AND pg.ngayBatDau <= :now AND pg.ngayKetThuc >= :now " +
            "AND (pg.soLuong IS NULL OR pg.soLuongDaSuDung < pg.soLuong)")
    Optional<PhieuGiamGia> findValidVoucherByCode(@Param("ma") String ma, @Param("now") LocalDateTime now);

    List<PhieuGiamGia> findByTrangThai(Boolean trangThai);

    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.trangThai = true " +
            "AND pg.ngayKetThuc BETWEEN :now AND :endDate")
    List<PhieuGiamGia> findExpiringSoon(@Param("now") LocalDateTime now, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.trangThai = true " +
            "AND pg.soLuong IS NOT NULL " +
            "AND (pg.soLuong - pg.soLuongDaSuDung) <= :threshold")
    List<PhieuGiamGia> findLowQuantity(@Param("threshold") Integer threshold);

    List<PhieuGiamGia> findByGiamTheoPhanTramAndTrangThai(Boolean giamTheoPhanTram, Boolean trangThai);

    @Query("SELECT MONTH(pg.createAt) as month, COUNT(pg) as count " +
            "FROM PhieuGiamGia pg WHERE YEAR(pg.createAt) = :year " +
            "GROUP BY MONTH(pg.createAt) ORDER BY MONTH(pg.createAt)")
    List<Object[]> getMonthlyStatistics(@Param("year") Integer year);

    @Modifying
    @Transactional
    @Query("UPDATE PhieuGiamGia pg SET pg.soLuongDaSuDung = pg.soLuongDaSuDung + 1 WHERE pg.id = :id")
    void incrementUsageCount(@Param("id") Integer id);

    @Query("SELECT pg FROM PhieuGiamGia pg WHERE pg.trangThai = false")
    List<PhieuGiamGia> findInactiveVouchers();

    @Query("SELECT MONTH(pg.createAt) as month, COUNT(pg) as count " +
            "FROM PhieuGiamGia pg WHERE YEAR(pg.createAt) = :year AND pg.trangThai = true " +
            "GROUP BY MONTH(pg.createAt) ORDER BY MONTH(pg.createAt)")
    List<Object[]> getActiveMonthlyStatistics(@Param("year") Integer year);

    // Thêm phương thức để kiểm tra danh sách phiếu theo tháng
    @Query("SELECT pg FROM PhieuGiamGia pg WHERE YEAR(pg.createAt) = :year AND MONTH(pg.createAt) = :month")
    List<PhieuGiamGia> findVouchersByMonth(@Param("year") Integer year, @Param("month") Integer month);
}