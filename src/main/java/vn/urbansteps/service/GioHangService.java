package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
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
            System.out.println("=== ADD TO CART SERVICE DEBUG ===");
            System.out.println("Gio hang ID: " + gioHang.getId());
            System.out.println("San pham chi tiet ID: " + sanPhamChiTietId);
            System.out.println("So luong: " + soLuong);
            
            // Kiểm tra sản phẩm có tồn tại không
            Optional<SanPhamChiTiet> sanPhamChiTiet = sanPhamChiTietRepository.findById(sanPhamChiTietId);
            if (!sanPhamChiTiet.isPresent()) {
                System.out.println("ERROR: San pham chi tiet not found!");
                return false;
            }

            SanPhamChiTiet spct = sanPhamChiTiet.get();
            System.out.println("Found san pham chi tiet: " + spct.getId() + " - " + spct.getSanPham().getTenSanPham());
            System.out.println("Stock available: " + spct.getSoLuong());
            System.out.println("Size: " + spct.getKichCo().getTenKichCo() + ", Color: " + spct.getMauSac().getTenMauSac());

            // Kiểm tra số lượng tồn kho
            if (spct.getSoLuong() < soLuong) {
                System.out.println("ERROR: Not enough stock! Available: " + spct.getSoLuong() + ", Requested: " + soLuong);
                return false;
            }

            // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
            Optional<GioHangItem> existingItem = gioHangItemRepository
                .findByGioHangAndSanPhamChiTiet(gioHang, spct);

            if (existingItem.isPresent()) {
                // Nếu đã có, cập nhật số lượng
                GioHangItem item = existingItem.get();
                int newQuantity = item.getSoLuong() + soLuong;
                
                System.out.println("Item already exists with quantity: " + item.getSoLuong());
                System.out.println("New quantity will be: " + newQuantity);
                
                // Kiểm tra số lượng tồn kho
                if (spct.getSoLuong() < newQuantity) {
                    System.out.println("ERROR: Not enough stock for update! Available: " + spct.getSoLuong() + ", New quantity: " + newQuantity);
                    return false;
                }
                
                item.setSoLuong(newQuantity);
                item.setUpdateAt(LocalDateTime.now());
                gioHangItemRepository.save(item);
                System.out.println("Updated existing item quantity to: " + newQuantity);
            } else {
                // Nếu chưa có, tạo mới
                System.out.println("Creating new cart item...");
                GioHangItem newItem = new GioHangItem();
                newItem.setGioHang(gioHang);
                newItem.setSanPhamChiTiet(spct);
                newItem.setSoLuong(soLuong);
                newItem.setGiaTaiThoidiem(spct.getSanPham().getGiaBan());
                newItem.setCreateAt(LocalDateTime.now());
                newItem.setUpdateAt(LocalDateTime.now());
                GioHangItem savedItem = gioHangItemRepository.save(newItem);
                System.out.println("Created new cart item with ID: " + savedItem.getId());
            }

            // Cập nhật thời gian giỏ hàng
            updateGioHangTime(gioHang);
            System.out.println("ADD TO CART SUCCESS!");
            return true;
        } catch (Exception e) {
            System.err.println("ERROR in addToCart service:");
            e.printStackTrace();
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
                return false;
            }

            GioHangItem gioHangItem = item.get();
            
            // Kiểm tra số lượng tồn kho
            if (gioHangItem.getSanPhamChiTiet().getSoLuong() < soLuong) {
                return false;
            }

            gioHangItem.setSoLuong(soLuong);
            gioHangItem.setUpdateAt(LocalDateTime.now());
            gioHangItemRepository.save(gioHangItem);

            // Cập nhật thời gian giỏ hàng
            updateGioHangTime(gioHangItem.getGioHang());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    public boolean removeFromCart(Integer gioHangItemId) {
        try {
            Optional<GioHangItem> item = gioHangItemRepository.findById(gioHangItemId);
            if (!item.isPresent()) {
                return false;
            }

            GioHangItem gioHangItem = item.get();
            GioHang gioHang = gioHangItem.getGioHang();
            
            // Xóa item
            gioHangItemRepository.delete(gioHangItem);

            // Cập nhật thời gian giỏ hàng
            updateGioHangTime(gioHang);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
    }

    /**
     * Xóa toàn bộ giỏ hàng (sau khi checkout)
     */
    public boolean clearCart(GioHang gioHang) {
        try {
            gioHangItemRepository.deleteByGioHang(gioHang);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Merge giỏ hàng từ session sang tài khoản khi đăng nhập
     */
    public boolean mergeSessionCartToUserCart(String sessionId, Integer userId) {
        try {
            // Lấy giỏ hàng session
            Optional<GioHang> sessionCart = gioHangRepository.findBySessionId(sessionId);
            if (!sessionCart.isPresent()) {
                return true; // Không có giỏ hàng session thì không cần merge
            }

            // Lấy giỏ hàng user
            GioHang userCart = getGioHangByUserId(userId);
            if (userCart == null) {
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
                }
            }

            // Xóa session cart
            gioHangRepository.delete(sessionCart.get());
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void clearGioHangByUserId(Integer userId) {
        Optional<TaiKhoan> taiKhoan = taiKhoanRepository.findById(userId);
        taiKhoan.ifPresent(tk -> {
            Optional<GioHang> gioHang = gioHangRepository.findByTaiKhoan(tk);
            gioHang.ifPresent(gioHangItemRepository::deleteByGioHang);
        });
    }
}
