package vn.urbansteps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.PhieuGiamGia;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.repository.PhieuGiamGiaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

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

    /**
     * Allocation result for item-level discount distribution.
     */
    public static class AllocationResult {
        public final BigDecimal totalBefore;
        public final BigDecimal discountTotal;
        public final BigDecimal totalAfter;
        public final Map<Integer, BigDecimal> perItemDiscount; // key: GioHangItem.id, value: discount for that line

        public AllocationResult(BigDecimal totalBefore, BigDecimal discountTotal, BigDecimal totalAfter, Map<Integer, BigDecimal> perItemDiscount) {
            this.totalBefore = totalBefore;
            this.discountTotal = discountTotal;
            this.totalAfter = totalAfter;
            this.perItemDiscount = perItemDiscount;
        }
    }

    /**
     * Distribute a valid voucher's discount proportionally across cart items.
     * If voucher is invalid or discount is zero, returns null.
     */
    public AllocationResult allocateDiscountAcrossItems(String code, List<GioHangItem> items) {
        if (items == null || items.isEmpty()) return null;
        Optional<PhieuGiamGia> voucher = phieuGiamGiaRepository.findValidVoucherByCode(code, LocalDateTime.now());
        if (voucher.isEmpty()) return null;

        // Compute cart total
        BigDecimal total = BigDecimal.ZERO;
        for (GioHangItem it : items) {
            if (it.getGiaTaiThoidiem() == null || it.getSoLuong() == null) continue;
            total = total.add(it.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(it.getSoLuong())));
        }
        if (total.compareTo(BigDecimal.ZERO) <= 0) return null;

        BigDecimal discountTotal = voucher.get().calculateDiscount(total);
        if (discountTotal.compareTo(BigDecimal.ZERO) <= 0) return null;

        // Proportional allocation with rounding fix on the last item
        Map<Integer, BigDecimal> map = new HashMap<>();
        BigDecimal allocated = BigDecimal.ZERO;
        int count = 0;
        int lastIndex = items.size() - 1;
        for (GioHangItem it : items) {
            BigDecimal line = (it.getGiaTaiThoidiem() == null || it.getSoLuong() == null)
                    ? BigDecimal.ZERO
                    : it.getGiaTaiThoidiem().multiply(BigDecimal.valueOf(it.getSoLuong()));
            BigDecimal alloc;
            if (count < lastIndex) {
                BigDecimal ratio = line.compareTo(BigDecimal.ZERO) > 0 ? line.divide(total, 10, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                alloc = discountTotal.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                allocated = allocated.add(alloc);
            } else {
                // Last item gets the remaining to make sum exact
                alloc = discountTotal.subtract(allocated).setScale(2, RoundingMode.HALF_UP);
            }
            map.put(it.getId(), alloc);
            count++;
        }

        return new AllocationResult(total, discountTotal, total.subtract(discountTotal), map);
    }
}