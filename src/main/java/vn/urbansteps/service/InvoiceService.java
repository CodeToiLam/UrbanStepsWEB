package vn.urbansteps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.HoaDon;
import vn.urbansteps.model.HoaDonChiTiet;
import vn.urbansteps.repository.HoaDonRepository;
import vn.urbansteps.repository.HoaDonChiTietRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    public List<HoaDon> getAllInvoices() {
        return hoaDonRepository.findAll();
    }

    public Optional<HoaDon> findById(Integer id) {
        return hoaDonRepository.findById(id);
    }

    public List<HoaDonChiTiet> getInvoiceDetails(Integer hoaDonId) {
        return hoaDonChiTietRepository.findByHoaDon_Id(hoaDonId);
    }

    public void save(HoaDon hoaDon) {
        if (hoaDon.getCreateAt() == null) {
            hoaDon.setCreateAt(LocalDateTime.now());
            logger.info("Setting createAt for new invoice: {}, createAt: {}", hoaDon.getMaHoaDon(), hoaDon.getCreateAt());
        }
        hoaDonRepository.save(hoaDon);
        logger.info("Saved invoice with ID: {}", hoaDon.getId());
    }

    public void delete(Integer id) {
        hoaDonRepository.findById(id).ifPresent(hoaDon -> {
            hoaDon.setTrangThai((byte) 4); // Đặt trạng thái "Đã hủy"
            hoaDon.setUpdateAt(LocalDateTime.now());
            hoaDonRepository.save(hoaDon);
            logger.info("Deleted invoice with ID: {}", id);
        });
    }
}