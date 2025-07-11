package vn.urbansteps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.PhieuGiamGia;
import vn.urbansteps.repository.PhieuGiamGiaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PhieuGiamGiaService {

    private static final Logger logger = LoggerFactory.getLogger(PhieuGiamGiaService.class);

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    public List<PhieuGiamGia> getAllPhieuGiamGia() {
        return phieuGiamGiaRepository.findAll();
    }

    public Optional<PhieuGiamGia> findById(Integer id) {
        return phieuGiamGiaRepository.findById(id);
    }

    public void save(PhieuGiamGia phieuGiamGia) {
        phieuGiamGiaRepository.save(phieuGiamGia);
    }

    public void softDelete(Integer id) {
        phieuGiamGiaRepository.findById(id).ifPresent(pg -> {
            pg.setDeleteAt(LocalDateTime.now());
            phieuGiamGiaRepository.save(pg);
        });
    }

    public List<PhieuGiamGia> findActiveVouchers() {
        return phieuGiamGiaRepository.findActiveVouchers(LocalDateTime.now());
    }

    public List<PhieuGiamGia> findExpiringSoon() {
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        return phieuGiamGiaRepository.findExpiringSoon(LocalDateTime.now(), endDate);
    }

    public List<Object[]> getMonthlyStatistics(Integer year) {
        logger.info("Fetching total monthly statistics for year: {}", year);
        List<Object[]> stats = phieuGiamGiaRepository.getMonthlyStatistics(year);
        logger.info("Monthly stats result: {}", stats);
        return stats;
    }

    public void incrementUsageCount(Integer id) {
        phieuGiamGiaRepository.incrementUsageCount(id);
    }

    public void deactivate(Integer id) {
        phieuGiamGiaRepository.findById(id).ifPresent(pg -> {
            pg.setTrangThai(false);
            pg.setUpdateAt(LocalDateTime.now());
            phieuGiamGiaRepository.save(pg);
            logger.info("Deactivated discount with ID: {}", id);
        });
    }

    public List<PhieuGiamGia> findInactiveVouchers() {
        return phieuGiamGiaRepository.findInactiveVouchers();
    }

    public void saveWithCreateAt(PhieuGiamGia phieuGiamGia) {
        if (phieuGiamGia.getCreateAt() == null) {
            phieuGiamGia.setCreateAt(LocalDateTime.now());
            logger.info("Setting createAt for new discount: {}, createAt: {}", phieuGiamGia.getMaPhieuGiamGia(), phieuGiamGia.getCreateAt());
        }
        phieuGiamGiaRepository.save(phieuGiamGia);
        logger.info("Saved discount with ID: {}, createAt: {}", phieuGiamGia.getId(), phieuGiamGia.getCreateAt());
    }

    public List<Object[]> getActiveMonthlyStatistics(Integer year) {
        return phieuGiamGiaRepository.getActiveMonthlyStatistics(year);
    }

    // Thêm phương thức để kiểm tra danh sách phiếu theo tháng
    public List<PhieuGiamGia> findVouchersByMonth(Integer year, Integer month) {
        logger.info("Fetching vouchers for year: {}, month: {}", year, month);
        List<PhieuGiamGia> vouchers = phieuGiamGiaRepository.findVouchersByMonth(year, month);
        logger.info("Vouchers found for year {} month {}: {}", year, month, vouchers);
        return vouchers;
    }
}