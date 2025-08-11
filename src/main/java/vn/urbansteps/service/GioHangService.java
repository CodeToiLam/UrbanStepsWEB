package vn.urbansteps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.urbansteps.dto.CartErrorType;
import vn.urbansteps.dto.CartOperationResult;
import vn.urbansteps.model.GioHang;
import vn.urbansteps.model.GioHangItem;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.GioHangRepository;
import vn.urbansteps.repository.GioHangItemRepository;
import vn.urbansteps.repository.SanPhamChiTietRepository;
import vn.urbansteps.repository.TaiKhoanRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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

    @PersistenceContext
    private EntityManager entityManager;

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
     * Thêm sản phẩm vào giỏ hàng với validation đầy đủ
     * @return CartOperationResult object with operation result and details
     */
    public CartOperationResult addToCart(GioHang gioHang, Integer sanPhamChiTietId, int soLuong) {
        try {
            logger.info("Thêm sản phẩm vào giỏ hàng: gioHangId={}, sanPhamChiTietId={}, soLuong={}",
                    gioHang.getId(), sanPhamChiTietId, soLuong);

            // Validation đầu vào
            if (soLuong <= 0) {
                logger.warn("Số lượng không hợp lệ: {}", soLuong);
                return CartOperationResult.error(CartErrorType.INVALID_QUANTITY);
            }

            if (soLuong > 999) {
                logger.warn("Số lượng vượt quá giới hạn cho phép (999): {}", soLuong);
                return CartOperationResult.error(CartErrorType.MAXIMUM_QUANTITY);
            }

            // Kiểm tra sản phẩm có tồn tại không
            Optional<SanPhamChiTiet> sanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId);
            if (!sanPhamChiTiet.isPresent()) {
                logger.warn("Không tìm thấy sản phẩm chi tiết ID: {}", sanPhamChiTietId);
                return CartOperationResult.error(CartErrorType.PRODUCT_NOT_FOUND);
            }

            SanPhamChiTiet spct = sanPhamChiTiet.get();
            logger.info("Tìm thấy sản phẩm: {} - {}, stock={}",
                    spct.getId(), spct.getSanPham().getTenSanPham(), spct.getSoLuong());

            // Validation sản phẩm
            if (!spct.isAvailable()) {
                logger.warn("Sản phẩm không khả dụng: {}", sanPhamChiTietId);
                return CartOperationResult.error(CartErrorType.PRODUCT_UNAVAILABLE);
            }

            if (spct.getSanPham() == null) {
                logger.warn("Sản phẩm không có thông tin sản phẩm cha: {}", sanPhamChiTietId);
                return CartOperationResult.error(CartErrorType.PRODUCT_NOT_FOUND);
            }

            // Validation giá
            BigDecimal giaBan = spct.getGiaBanThucTe();
            if (giaBan == null || giaBan.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Sản phẩm không có giá hợp lệ: {}, giá: {}", sanPhamChiTietId, giaBan);
                return CartOperationResult.error(CartErrorType.INVALID_PRICE);
            }

            // Kiểm tra số lượng tồn kho
            if (spct.getSoLuong() < soLuong) {
                logger.warn("Không đủ hàng trong kho: available={}, requested={}", spct.getSoLuong(), soLuong);
                return CartOperationResult.error(CartErrorType.INSUFFICIENT_STOCK, 
                        String.format("Sản phẩm chỉ còn %d trong kho, bạn yêu cầu %d", spct.getSoLuong(), soLuong));
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

                // Validation số lượng mới
                if (newQuantity > 999) {
                    logger.warn("Tổng số lượng vượt quá giới hạn cho phép (999): {}", newQuantity);
                    return CartOperationResult.error(CartErrorType.MAXIMUM_QUANTITY);
                }

                // Kiểm tra số lượng tồn kho cho số lượng mới
                if (spct.getSoLuong() < newQuantity) {
                    logger.warn("Không đủ hàng trong kho để cập nhật: available={}, requested={}",
                            spct.getSoLuong(), newQuantity);
                    return CartOperationResult.error(CartErrorType.INSUFFICIENT_STOCK,
                            String.format("Sản phẩm chỉ còn %d trong kho, bạn yêu cầu %d", spct.getSoLuong(), newQuantity));
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
                newItem.setGiaTaiThoidiem(giaBan);
                newItem.setCreateAt(LocalDateTime.now());
                newItem.setUpdateAt(LocalDateTime.now());
                GioHangItem savedItem = gioHangItemRepository.save(newItem);
                logger.info("Tạo mới sản phẩm trong giỏ hàng thành công: itemId={}", savedItem.getId());
            }

            // Cập nhật thời gian giỏ hàng
            updateGioHangTime(gioHang);
            
            // Tính toán tổng tiền và số lượng sản phẩm trong giỏ hàng
            BigDecimal cartTotal = calculateTotal(gioHang);
            int cartCount = countItems(gioHang);
            
            logger.info("Thêm sản phẩm vào giỏ hàng thành công! Total={}, Count={}", cartTotal, cartCount);
            return CartOperationResult.success(cartTotal, cartCount);
        } catch (Exception e) {
            logger.error("Lỗi khi thêm sản phẩm vào giỏ hàng: {}", e.getMessage(), e);
            return CartOperationResult.error(CartErrorType.SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng với validation đầy đủ
     */
    public CartOperationResult updateQuantity(Integer gioHangItemId, int soLuong) {
        try {
            // Validation đầu vào
            if (soLuong <= 0) {
                logger.warn("Số lượng không hợp lệ: {}", soLuong);
                return CartOperationResult.error(CartErrorType.INVALID_QUANTITY, "Số lượng phải lớn hơn 0");
            }

            if (soLuong > 999) {
                logger.warn("Số lượng vượt quá giới hạn cho phép (999): {}", soLuong);
                return CartOperationResult.error(CartErrorType.QUANTITY_EXCEEDS_LIMIT, "Số lượng không được vượt quá 999");
            }

            Optional<GioHangItem> item = gioHangItemRepository.findById(gioHangItemId);
            if (!item.isPresent()) {
                logger.warn("Không tìm thấy sản phẩm trong giỏ hàng: itemId={}", gioHangItemId);
                return CartOperationResult.error(CartErrorType.ITEM_NOT_FOUND, "Sản phẩm không còn trong giỏ hàng");
            }

            GioHangItem gioHangItem = item.get();
            SanPhamChiTiet sanPhamChiTiet = gioHangItem.getSanPhamChiTiet();

            // Validation sản phẩm
            if (sanPhamChiTiet == null) {
                logger.warn("Sản phẩm chi tiết không tồn tại cho item: {}", gioHangItemId);
                return CartOperationResult.error(CartErrorType.PRODUCT_NOT_FOUND, "Sản phẩm không còn tồn tại");
            }

            if (!sanPhamChiTiet.isAvailable()) {
                logger.warn("Sản phẩm không khả dụng: itemId={}, sanPhamChiTietId={}", 
                        gioHangItemId, sanPhamChiTiet.getId());
                return CartOperationResult.error(CartErrorType.PRODUCT_UNAVAILABLE, "Sản phẩm này hiện không còn kinh doanh");
            }

            // Validation giá
            BigDecimal giaBan = sanPhamChiTiet.getGiaBanThucTe();
            if (giaBan == null || giaBan.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("Sản phẩm không có giá hợp lệ: itemId={}, giá={}", gioHangItemId, giaBan);
                return CartOperationResult.error(CartErrorType.INVALID_PRICE, "Sản phẩm không có giá hợp lệ");
            }

            // Kiểm tra số lượng tồn kho
            if (sanPhamChiTiet.getSoLuong() == 0) {
                logger.warn("Sản phẩm hết hàng: itemId={}, requested={}", gioHangItemId, soLuong);
                return CartOperationResult.error(CartErrorType.OUT_OF_STOCK, 
                    "Sản phẩm đã hết hàng", sanPhamChiTiet.getId());
            } else if (sanPhamChiTiet.getSoLuong() < soLuong) {
                logger.warn("Không đủ hàng trong kho: itemId={}, available={}, requested={}",
                        gioHangItemId, sanPhamChiTiet.getSoLuong(), soLuong);
                return CartOperationResult.error(CartErrorType.INSUFFICIENT_STOCK, 
                    "Chỉ còn " + sanPhamChiTiet.getSoLuong() + " sản phẩm trong kho", sanPhamChiTiet.getId());
            }

            gioHangItem.setSoLuong(soLuong);
            gioHangItem.setUpdateAt(LocalDateTime.now());
            gioHangItemRepository.save(gioHangItem);

            // Cập nhật thời gian giỏ hàng
            GioHang gioHang = gioHangItem.getGioHang();
            updateGioHangTime(gioHang);
            
            // Tính toán thông tin giỏ hàng mới
            BigDecimal cartTotal = calculateTotal(gioHang);
            int cartCount = countItems(gioHang);
            
            logger.info("Cập nhật số lượng sản phẩm thành công: itemId={}, newQuantity={}", gioHangItemId, soLuong);
            return CartOperationResult.success(cartTotal, cartCount);
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật số lượng: {}", e.getMessage(), e);
            return CartOperationResult.error(CartErrorType.SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Lấy tổng tiền của một item trong giỏ hàng
     */
    public BigDecimal getItemTotal(Integer gioHangItemId) {
        try {
            Optional<GioHangItem> item = gioHangItemRepository.findById(gioHangItemId);
            if (item.isPresent()) {
                GioHangItem gioHangItem = item.get();
                BigDecimal giaBan = gioHangItem.getSanPhamChiTiet().getGiaBanThucTe();
                int soLuong = gioHangItem.getSoLuong();
                return giaBan.multiply(BigDecimal.valueOf(soLuong));
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("Lỗi khi tính tổng tiền item: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @Transactional
    public CartOperationResult removeFromCart(Integer gioHangItemId) {
        try {
            // Lấy thông tin giỏ hàng trước khi xóa để tính toán giá trị mới
            Optional<GioHangItem> itemOpt = gioHangItemRepository.findById(gioHangItemId);
            if (!itemOpt.isPresent()) {
                logger.warn("Không tìm thấy sản phẩm trong giỏ hàng để xóa: itemId={}", gioHangItemId);
                return CartOperationResult.error(CartErrorType.ITEM_NOT_FOUND, "Sản phẩm không tồn tại trong giỏ hàng");
            }
            
            GioHang gioHang = itemOpt.get().getGioHang();
            
            // Sử dụng native query để tránh vấn đề với Hibernate cascade
            Query updateCartTimeQuery = entityManager.createNativeQuery(
                "UPDATE GioHang SET update_at = CURRENT_TIMESTAMP " +
                "WHERE id = (SELECT id_gio_hang FROM GioHangItem WHERE id = :itemId)");
            updateCartTimeQuery.setParameter("itemId", gioHangItemId);
            updateCartTimeQuery.executeUpdate();
            
            // Xóa item bằng native query
            Query deleteQuery = entityManager.createNativeQuery(
                "DELETE FROM GioHangItem WHERE id = :itemId");
            deleteQuery.setParameter("itemId", gioHangItemId);
            int deletedCount = deleteQuery.executeUpdate();
            
            if (deletedCount > 0) {
                logger.info("Xóa sản phẩm khỏi giỏ hàng thành công: itemId={}", gioHangItemId);
                
                // Tính lại thông tin giỏ hàng sau khi xóa
                BigDecimal cartTotal = calculateTotal(gioHang);
                int cartCount = countItems(gioHang);
                
                return CartOperationResult.success(cartTotal, cartCount);
            } else {
                logger.warn("Không thể xóa sản phẩm trong giỏ hàng: itemId={}", gioHangItemId);
                return CartOperationResult.error(CartErrorType.DELETE_FAILED, "Không thể xóa sản phẩm khỏi giỏ hàng");
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xóa sản phẩm khỏi giỏ hàng: {}", e.getMessage(), e);
            return CartOperationResult.error(CartErrorType.SERVER_ERROR, "Lỗi hệ thống: " + e.getMessage());
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
        if (gioHang == null || gioHang.getId() == null) {
            logger.warn("Không thể cập nhật thời gian cho giỏ hàng null hoặc không có ID");
            return;
        }
        
        try {
            // Sử dụng native query để cập nhật thời gian giỏ hàng trực tiếp trong DB
            Query updateQuery = entityManager.createNativeQuery(
                "UPDATE GioHang SET update_at = CURRENT_TIMESTAMP WHERE id = :id");
            updateQuery.setParameter("id", gioHang.getId());
            int updatedRows = updateQuery.executeUpdate();
            
            if (updatedRows > 0) {
                logger.info("Cập nhật thời gian giỏ hàng thành công: gioHangId={}", gioHang.getId());
            } else {
                logger.warn("Không tìm thấy giỏ hàng với ID={} để cập nhật thời gian", gioHang.getId());
            }
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật thời gian giỏ hàng: {}", e.getMessage(), e);
        }
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
     * Xóa giỏ hàng theo session ID (cho guest)
     */
    @Transactional
    public boolean clearGioHangBySessionId(String sessionId) {
        try {
            Optional<GioHang> gioHang = gioHangRepository.findBySessionId(sessionId);
            if (gioHang.isPresent()) {
                // Xóa tất cả items trước
                gioHangItemRepository.deleteByGioHang(gioHang.get());
                // Xóa giỏ hàng
                gioHangRepository.delete(gioHang.get());
                logger.info("Đã xóa giỏ hàng session: sessionId={}", sessionId);
                return true;
            }
            logger.warn("Không tìm thấy giỏ hàng cho session: {}", sessionId);
            return true; // Trả về true vì mục tiêu đã đạt được (giỏ hàng trống)
        } catch (Exception e) {
            logger.error("Lỗi khi xóa giỏ hàng session: {}", e.getMessage(), e);
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
                try {
                    gioHangRepository.delete(g); // Xóa luôn record giỏ hàng để tạo mới sạch lần sau
                } catch (Exception ex) {
                    logger.warn("Không thể xóa entity giỏ hàng (sẽ bỏ qua): {}", ex.getMessage());
                }
                logger.info("Xóa giỏ hàng thành công: userId={}", userId);
            });
        });
    }
}