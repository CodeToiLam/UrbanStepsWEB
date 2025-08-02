package vn.urbansteps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.PhieuGiamGia;
import vn.urbansteps.repository.PhieuGiamGiaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PhieuGiamGiaService {

    private static final Logger logger = LoggerFactory.getLogger(PhieuGiamGiaService.class);

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    public List<PhieuGiamGia> getAllPhieuGiamGia() {
        logger.info("Fetching all discounts");
        return phieuGiamGiaRepository.findAll();
    }

    public Optional<PhieuGiamGia> findById(Integer id) {
        logger.info("Finding discount with ID: {}", id);
        return phieuGiamGiaRepository.findById(id);
    }

    public void save(PhieuGiamGia phieuGiamGia) {
        logger.info("Saving discount: {}", phieuGiamGia.getMaPhieuGiamGia());
        phieuGiamGiaRepository.save(phieuGiamGia);
    }

    public void softDelete(Integer id) {
        phieuGiamGiaRepository.findById(id).ifPresent(pg -> {
            pg.setDeleteAt(LocalDateTime.now());
            phieuGiamGiaRepository.save(pg);
            logger.info("Soft deleted discount with ID: {}", id);
        });
    }

    public List<PhieuGiamGia> findActiveVouchers() {
        logger.info("Fetching active vouchers");
        return phieuGiamGiaRepository.findActiveVouchers(LocalDateTime.now());
    }

    public List<PhieuGiamGia> findExpiringSoon() {
        LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        logger.info("Fetching vouchers expiring soon until: {}", endDate);
        return phieuGiamGiaRepository.findExpiringSoon(LocalDateTime.now(), endDate);
    }

    public List<Object[]> getMonthlyStatistics(Integer year) {
        logger.info("Fetching total monthly statistics for year: {}", year);
        List<Object[]> stats = phieuGiamGiaRepository.getMonthlyStatistics(year);
        logger.info("Monthly stats result: {}", stats);
        return stats;
    }

    public void incrementUsageCount(Integer id) {
        logger.info("Incrementing usage count for discount ID: {}", id);
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
        logger.info("Fetching inactive vouchers");
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
        logger.info("Fetching active monthly statistics for year: {}", year);
        return phieuGiamGiaRepository.getActiveMonthlyStatistics(year);
    }

    public List<PhieuGiamGia> findVouchersByMonth(Integer year, Integer month) {
        logger.info("Fetching vouchers for year: {}, month: {}", year, month);
        List<PhieuGiamGia> vouchers = phieuGiamGiaRepository.findVouchersByMonth(year, month);
        logger.info("Vouchers found for year {} month {}: {}", year, month, vouchers);
        return vouchers;
    }

    public List<PhieuGiamGia> findPublicVouchers() {
        logger.info("Fetching public vouchers");
        return phieuGiamGiaRepository.findByApDungChoTatCaAndTrangThaiTrueAndNgayKetThucAfter(true, LocalDateTime.now());
    }

    public BigDecimal applyVoucher(String code, BigDecimal totalAmount) {
        logger.info("Applying voucher with code: {}", code);
        Optional<PhieuGiamGia> voucher = phieuGiamGiaRepository.findValidVoucherByCode(code, LocalDateTime.now());
        if (voucher.isPresent()) {
            PhieuGiamGia pg = voucher.get();
            BigDecimal discount = pg.calculateDiscount(totalAmount);
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                incrementUsageCount(pg.getId());
                return totalAmount.subtract(discount);
            }
        }
        return totalAmount;
    }
}