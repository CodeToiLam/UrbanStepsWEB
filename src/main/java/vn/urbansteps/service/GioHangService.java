package vn.urbansteps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.GioHangRepository;
import vn.urbansteps.repository.GioHangItemRepository;
import vn.urbansteps.repository.SanPhamChiTietRepository;
import vn.urbansteps.repository.TaiKhoanRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GioHangService {

    private static final Logger logger = LoggerFactory.getLogger(GioHangService.class);

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangItemRepository gioHangItemRepository;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Lấy giỏ hàng của user (đã đăng nhập)
     */
    public GioHang getGioHangByUserId(Integer userId) {
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findById(userId);
        if (!taiKhoan.isPresent()) {
            logger.warn("Không tìm thấy tài khoản với ID: {}", userId);
            return null;
        }

        Optional<GioHang> gioHang = gioHangRepository.findByTaiKhoan(taiKhoan.get());
        if (gioHang.isPresent()) {
            return gioHang.get();
        } else {
            // Tạo giỏ hàng mới nếu chưa có
            return createGioHangForUser(taiKhoan.get());
        }
    }

    /**
     * Lấy giỏ hàng với đầy đủ thông tin items
     */
    public GioHang getGioHangWithItemsByUserId(Integer userId) {
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findById(userId);
        if (!taiKhoan.isPresent()) {
            logger.warn("Không tìm thấy tài khoản với ID: {}", userId);
            return null;
        }

        Optional<GioHang> gioHang = gioHangRepository.findByTaiKhoanWithItems(taiKhoan.get());
        if (gioHang.isPresent()) {
            return gioHang.get();
        } else {
            // Tạo giỏ hàng mới nếu chưa có
            return createGioHangForUser(taiKhoan.get());
        }
    }

    /**
     * Lấy giỏ hàng của guest (chưa đăng nhập) theo session
     */
    public GioHang getGioHangBySessionId(String sessionId) {
        Optional<GioHang> gioHang = gioHangRepository.findBySessionId(sessionId);
        if (gioHang.isPresent()) {
            return gioHang.get();
        } else {
            // Tạo giỏ hàng mới cho guest
            return createGioHangForGuest(sessionId);
        }
    }

    /**
     * Lấy giỏ hàng với đầy đủ thông tin items cho guest
     */
    public GioHang getGioHangWithItemsBySessionId(String sessionId) {
        Optional<GioHang> gioHang = gioHangRepository.findBySessionIdWithItems(sessionId);
        if (gioHang.isPresent()) {
            return gioHang.get();
        } else {
            // Tạo giỏ hàng mới cho guest
            return createGioHangForGuest(sessionId);
        }
    }

    /**
     * Tạo giỏ hàng mới cho user
     */
    private GioHang createGioHangForUser(TaiKhoan taiKhoan) {
        GioHang gioHang = new GioHang();
        gioHang.setTaiKhoan(taiKhoan);
        gioHang.setCreateAt(LocalDateTime.now());
        gioHang.setUpdateAt(LocalDateTime.now());
        logger.info("Tạo giỏ hàng mới cho tài khoản ID: {}", taiKhoan.getId());
        return gioHangRepository.save(gioHang);
    }

    /**
     * Tạo giỏ hàng mới cho guest
     */
    private GioHang createGioHangForGuest(String sessionId) {
        GioHang gioHang = new GioHang();
        gioHang.setSessionId(sessionId);
        gioHang.setCreateAt(LocalDateTime.now());
        gioHang.setUpdateAt(LocalDateTime.now());
        logger.info("Tạo giỏ hàng mới cho session ID: {}", sessionId);
        return gioHangRepository.save(gioHang);
    }

    /**
     * Lấy danh sách item trong giỏ hàng
     */
    public List<GioHangItem> getGioHangItems(GioHang gioHang) {
        return gioHangItemRepository.findByGioHangWithDetails(gioHang);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public boolean addToCart(GioHang gioHang, Integer sanPhamChiTietId, int soLuong) {
        try {
            logger.info("Thêm sản phẩm vào giỏ hàng: gioHangId={}, sanPhamChiTietId={}, soLuong={}",
                    gioHang.getId(), sanPhamChiTietId, soLuong);

            // Kiểm tra sản phẩm có tồn tại không
            Optional<SanPhamChiTiet> sanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId);
            if (!sanPhamChiTiet.isPresent()) {
                logger.warn("Không tìm thấy sản phẩm chi tiết ID: {}", sanPhamChiTietId);
                return false;
            }

            SanPhamChiTiet spct = sanPhamChiTiet.get();
            logger.info("Tìm thấy sản phẩm: {} - {}, stock={}",
                    spct.getId(), spct.getSanPham().getTenSanPham(), spct.getSoLuong());

            // Kiểm tra số lượng tồn kho
            if (spct.getSoLuong() < soLuong) {
                logger.warn("Không đủ hàng trong kho: available={}, requested={}", spct.getSoLuong(), soLuong);
                return false;
            }

            // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
            Optional<GioHangItem> existingItem = gioHangItemRepository
                    .findByGioHangAndSanPhamChiTiet(gioHang, spct);

            if (existingItem.isPresent()) {
                // Nếu đã có, cập nhật số lượng
                GioHangItem item = existingItem.get();
                int newQuantity = item.getSoLuong() + soLuong;

                logger.info("Sản phẩm đã tồn tại trong giỏ hàng, cập nhật số lượng từ {} lên {}",
                        item.getSoLuong(), newQuantity);

                // Kiểm tra số lượng tồn kho
                if (spct.getSoLuong() < newQuantity) {
                    logger.warn("Không đủ hàng trong kho để cập nhật: available={}, requested={}",
                            spct.getSoLuong(), newQuantity);
                    return false;
                }

                item.setSoLuong(newQuantity);
                item.setUpdateAt(LocalDateTime.now());
                gioHangItemRepository.save(item);
                logger.info("Cập nhật số lượng sản phẩm thành công: itemId={}", item.getId());
            } else {
                // Nếu chưa có, tạo mới
                logger.info("Tạo mới sản phẩm trong giỏ hàng...");
                GioHangItem newItem = new GioHangItem();
                newItem.setGioHang(gioHang);
                newItem.setSanPhamChiTiet(spct);
                newItem.setSoLuong(soLuong);
                newItem.setGiaTaiThoidiem(spct.getSanPham().getGiaBan());
                newItem.setCreateAt(LocalDateTime.now());
                newItem.setUpdateAt(LocalDateTime.now());
                GioHangItem savedItem = gioHangItemRepository.save(newItem);
                logger.info("Tạo mới sản phẩm trong giỏ hàng thành công: itemId={}", savedItem.getId());
            }

            // Cập nhật thời gian giỏ hàng
            updateGioHangTime(gioHang);
            logger.info("Thêm sản phẩm vào giỏ hàng thành công!");
            return true;
        } catch (Exception e) {
            logger.error("Lỗi khi thêm sản phẩm vào giỏ hàng: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     */
    public boolean updateQuantity(Integer gioHangItemId, int soLuong) {
        try {
            Optional<GioHangItem> item = gioHangItemRepository.findById(gioHangItemId);
            if (!item.isPresent()) {
                logger.warn("Không tìm thấy sản phẩm trong giỏ hàng: itemId={}", gioHangItemId);
                return false;
            }

            GioHangItem gioHangItem = item.get();

            // Kiểm tra số lượng tồn kho
            if (gioHangItem.getSanPhamChiTiet().getSoLuong() < soLuong) {
                logger.warn("Không đủ hàng trong kho: itemId={}, available={}, requested={}",
                        gioHangItemId, gioHangItem.getSanPhamChiTiet().getSoLuong(), soLuong);
                return false;
            }

            gioHangItem.setSoLuong(soLuong);
            gioHangItem.setUpdateAt(LocalDateTime.now());
            gioHangItemRepository.save(gioHangItem);

            // Cập nhật thời gian giỏ hàng
            updateGioHangTime(gioHangItem.getGioHang());
            logger.info("Cập nhật số lượng sản phẩm thành công: itemId={}", gioHangItemId);
            return true;
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật số lượng: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Transactional
    public boolean removeFromCart(Integer gioHangItemId) {
        try {
            Optional<GioHangItem> item = gioHangItemRepository.findById(gioHangItemId);
            if (!item.isPresent()) {
                logger.warn("Không tìm thấy sản phẩm trong giỏ hàng: itemId={}", gioHangItemId);
                return false;
            }

            GioHangItem gioHangItem = item.get();
            GioHang gioHang = gioHangItem.getGioHang();

            logger.info("Xóa sản phẩm khỏi giỏ hàng: itemId={}", gioHangItemId);
            gioHangItemRepository.delete(gioHangItem);

            // Cập nhật thời gian giỏ hàng
            updateGioHangTime(gioHang);
            logger.info("Xóa sản phẩm khỏi giỏ hàng thành công: itemId={}", gioHangItemId);
            return true;
        } catch (Exception e) {
            logger.error("Lỗi khi xóa sản phẩm khỏi giỏ hàng: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Tính tổng tiền giỏ hàng
     */
    public BigDecimal calculateTotal(GioHang gioHang) {
        return gioHang.getTongTien();
    }

    /**
     * Đếm số lượng item trong giỏ hàng
     */
    public int countItems(GioHang gioHang) {
        return gioHang.getTongSoLuong();
    }

    /**
     * Cập nhật thời gian giỏ hàng
     */
    private void updateGioHangTime(GioHang gioHang) {
        gioHang.setUpdateAt(LocalDateTime.now());
        gioHangRepository.save(gioHang);
        logger.info("Cập nhật thời gian giỏ hàng: gioHangId={}", gioHang.getId());
    }

    /**
     * Xóa toàn bộ giỏ hàng (sau khi checkout)
     */
    @Transactional
    public boolean clearCart(GioHang gioHang) {
        try {
            logger.info("Xóa toàn bộ giỏ hàng: gioHangId={}", gioHang.getId());
            gioHangItemRepository.deleteByGioHang(gioHang);
            logger.info("Xóa toàn bộ giỏ hàng thành công: gioHangId={}", gioHang.getId());
            return true;
        } catch (Exception e) {
            logger.error("Lỗi khi xóa toàn bộ giỏ hàng: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Merge giỏ hàng từ session sang tài khoản khi đăng nhập
     */
    @Transactional
    public boolean mergeSessionCartToUserCart(String sessionId, Integer userId) {
        try {
            // Lấy giỏ hàng session
            Optional<GioHang> sessionCart = gioHangRepository.findBySessionId(sessionId);
            if (!sessionCart.isPresent()) {
                logger.info("Không có giỏ hàng session để merge: sessionId={}", sessionId);
                return true; // Không có giỏ hàng session thì không cần merge
            }

            // Lấy giỏ hàng user
            GioHang userCart = getGioHangByUserId(userId);
            if (userCart == null) {
                logger.warn("Không thể lấy giỏ hàng user: userId={}", userId);
                return false;
            }

            // Lấy items từ session cart
            List<GioHangItem> sessionItems = gioHangItemRepository.findByGioHangWithDetails(sessionCart.get());

            // Merge từng item
            for (GioHangItem sessionItem : sessionItems) {
                Optional<GioHangItem> existingItem = gioHangItemRepository
                        .findByGioHangAndSanPhamChiTiet(userCart, sessionItem.getSanPhamChiTiet());

                if (existingItem.isPresent()) {
                    // Cộng dồn số lượng
                    GioHangItem item = existingItem.get();
                    int newQuantity = item.getSoLuong() + sessionItem.getSoLuong();

                    // Kiểm tra tồn kho
                    if (sessionItem.getSanPhamChiTiet().getSoLuong() >= newQuantity) {
                        item.setSoLuong(newQuantity);
                        item.setUpdateAt(LocalDateTime.now());
                        gioHangItemRepository.save(item);
                        logger.info("Cập nhật số lượng sản phẩm khi merge: itemId={}", item.getId());
                    }
                } else {
                    // Tạo mới item trong user cart
                    GioHangItem newItem = new GioHangItem();
                    newItem.setGioHang(userCart);
                    newItem.setSanPhamChiTiet(sessionItem.getSanPhamChiTiet());
                    newItem.setSoLuong(sessionItem.getSoLuong());
                    newItem.setGiaTaiThoidiem(sessionItem.getGiaTaiThoidiem());
                    newItem.setCreateAt(LocalDateTime.now());
                    newItem.setUpdateAt(LocalDateTime.now());
                    gioHangItemRepository.save(newItem);
                    logger.info("Tạo mới sản phẩm khi merge: sanPhamChiTietId={}", newItem.getSanPhamChiTiet().getId());
                }
            }

            // Xóa session cart
            logger.info("Xóa giỏ hàng session: sessionId={}", sessionId);
            gioHangRepository.delete(sessionCart.get());

            logger.info("Merge giỏ hàng thành công: userId={}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Lỗi khi merge giỏ hàng: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng theo userId
     */
    @Transactional
    public void clearGioHangByUserId(Integer userId) {
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findById(userId);
        taiKhoan.ifPresent(tk -> {
            Optional<GioHang> gioHang = gioHangRepository.findByTaiKhoan(tk);
            gioHang.ifPresent(g -> {
                logger.info("Xóa toàn bộ giỏ hàng của tài khoản: userId={}, gioHangId={}", userId, g.getId());
                gioHangItemRepository.deleteByGioHang(g);
                logger.info("Xóa giỏ hàng thành công: userId={}", userId);
            });
        });
    }
}