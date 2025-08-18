package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.repository.SanPhamRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.text.Normalizer;

@Service
public class SanPhamService {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Cache để tránh duplicate queries
    private List<SanPham> cachedActiveProducts;
    private long lastCacheTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 phút

    private void invalidateCache() {
        cachedActiveProducts = null;
        lastCacheTime = 0;
        System.out.println("Invalidated active products cache");
    }

    private List<SanPham> getActiveProductsWithCache() {
        long currentTime = System.currentTimeMillis();
        if (cachedActiveProducts == null || (currentTime - lastCacheTime) > CACHE_DURATION) {
            System.out.println("Loading active products from database with JOIN FETCH...");
            cachedActiveProducts = sanPhamRepository.findByTrangThaiOrderByGiaBanAsc(Boolean.TRUE);
            lastCacheTime = currentTime;
            System.out.println("Loaded " + cachedActiveProducts.size() + " active products to cache");
            for (SanPham product : cachedActiveProducts) {
                System.out.println("Product: " + product.getTenSanPham() + ", Price: " + product.getGiaBan());
                if (product.getIdHinhAnhDaiDien() != null) {
                    System.out.println("  Image: " + product.getIdHinhAnhDaiDien().getDuongDan());
                } else {
                    System.out.println("  No image!");
                }
            }
        } else {
            System.out.println("Using cached active products (" + cachedActiveProducts.size() + " items)");
        }
        return cachedActiveProducts;
    }

    public List<SanPham> getFlashDealProducts() {
        // Flash Deal = các sản phẩm đang sale (giảm giá cao trước)
        List<SanPham> saleProducts = sanPhamRepository.findSaleProducts(Boolean.TRUE);
        System.out.println("Flash Deal (sale) Products count: " + saleProducts.size());
        return saleProducts;
    }

    public List<SanPham> getTopPopularProducts() {
        // Popular/Hot = sản phẩm bán chạy (ưu tiên theo lượt bán)
        List<SanPham> bestSelling = sanPhamRepository.findBestSellingProducts(Boolean.TRUE);
        System.out.println("Top Popular (best-selling) Products count: " + bestSelling.size());
        return bestSelling;
    }

    public List<SanPham> getAllProducts() {
        return getActiveProductsWithCache();
    }

    public List<SanPham> findByTrangThai(Boolean trangThai) {
        return sanPhamRepository.findByTrangThai(trangThai);
    }

    public List<SanPham> findByThuongHieu(String tenThuongHieu) {
        return sanPhamRepository.findByThuongHieuTenThuongHieuAndTrangThai(tenThuongHieu, Boolean.TRUE);
    }

    public List<SanPham> findByDanhMuc(String tenDanhMuc) {
        return sanPhamRepository.findByDanhMucTenDanhMucAndTrangThai(tenDanhMuc, Boolean.TRUE);
    }

    public List<SanPham> findByLoaiSanPham(String tenLoaiSanPham) {
        return sanPhamRepository.findByLoaiSanPhamTenLoaiSanPhamAndTrangThai(tenLoaiSanPham, Boolean.TRUE);
    }

    public List<SanPham> findByXuatXu(String tenXuatXu) {
        return sanPhamRepository.findByXuatXuTenXuatXuAndTrangThai(tenXuatXu, Boolean.TRUE);
    }

    public List<SanPham> findByKieuDang(String tenKieuDang) {
        return sanPhamRepository.findByKieuDangTenKieuDangAndTrangThai(tenKieuDang, Boolean.TRUE);
    }

    public List<SanPham> findByChatLieu(String tenChatLieu) {
        return sanPhamRepository.findByChatLieuTenChatLieuAndTrangThai(tenChatLieu, Boolean.TRUE);
    }

    public List<SanPham> getSaleProducts() {
        // Dùng đúng định nghĩa SALE: có cờ laSale và phanTramGiam > 0
        return sanPhamRepository.findSaleProducts(Boolean.TRUE);
    }

    public List<SanPham> getHotProducts() {
    // HOT: hiển thị TẤT CẢ sản phẩm active, sắp xếp theo lượt bán giảm dần (lượt xem phụ trợ)
    return sanPhamRepository.findBestSellingProducts(Boolean.TRUE);
    }

    public Optional<SanPham> findById(Integer id) {
        return sanPhamRepository.findById(id);
    }

    public List<SanPham> getRelatedProducts(Integer productId) {
        return sanPhamRepository.findTop4ByTrangThaiAndIdNotOrderByGiaBanAsc(Boolean.TRUE, productId);
    }

    public List<SanPham> timKiemTheoTen(String keyword) {
        System.out.println("=== DEBUG SEARCH ===");
        System.out.println("Search keyword: '" + keyword + "'");
        
    String lower = normalizeText(keyword);
        List<SanPham> allProducts = getActiveProductsWithCache();
        
        System.out.println("Total active products: " + allProducts.size());
        
        List<SanPham> result = allProducts.stream()
            .filter(sp -> {
                if (lower.isEmpty()) return true;
                try {
                    String ten = normalizeText(sp.getTenSanPham());
                    String thuongHieu = "";
                    if (sp.getThuongHieu() != null) {
                        try { thuongHieu = normalizeText(sp.getThuongHieu().getTenThuongHieu()); } catch (Exception ignore) {}
                    }
                    String loai = "";
                    if (sp.getLoaiSanPham() != null) {
                        try { loai = normalizeText(sp.getLoaiSanPham().getTenLoaiSanPham()); } catch (Exception ignore) {}
                    }
                    String danhMuc = "";
                    if (sp.getDanhMuc() != null) {
                        try { danhMuc = normalizeText(sp.getDanhMuc().getTenDanhMuc()); } catch (Exception ignore) {}
                    }
                    String ma = normalizeText(sp.getMaSanPham());
                    String xuatXu = "";
                    if (sp.getXuatXu() != null) {
                        try { xuatXu = normalizeText(sp.getXuatXu().getTenXuatXu()); } catch (Exception ignore) {}
                    }
                    String kieuDang = "";
                    if (sp.getKieuDang() != null) {
                        try { kieuDang = normalizeText(sp.getKieuDang().getTenKieuDang()); } catch (Exception ignore) {}
                    }
                    String chatLieu = "";
                    if (sp.getChatLieu() != null) {
                        try { chatLieu = normalizeText(sp.getChatLieu().getTenChatLieu()); } catch (Exception ignore) {}
                    }

                    boolean matches = (ten.contains(lower))
                        || (thuongHieu.contains(lower))
                        || (loai.contains(lower))
                        || (danhMuc.contains(lower))
                        || (ma.contains(lower))
                        || (xuatXu.contains(lower))
                        || (kieuDang.contains(lower))
                        || (chatLieu.contains(lower));

                    if (matches && sp.getTenSanPham() != null) {
                        System.out.println("Match found: " + sp.getTenSanPham());
                    }
                    return matches;
                } catch (Exception e) {
                    // Nếu gặp lazy proxy chưa khởi tạo, bỏ qua field đó
                    String ten = normalizeText(sp.getTenSanPham());
                    String ma = normalizeText(sp.getMaSanPham());
                    boolean matches = ten.contains(lower) || ma.contains(lower);
                    if (matches && sp.getTenSanPham() != null) {
                        System.out.println("Match(found-safe): " + sp.getTenSanPham());
                    }
                    return matches;
                }
            })
            .collect(Collectors.toList());
            
        System.out.println("Search results: " + result.size() + " products");
        System.out.println("=== END DEBUG SEARCH ===");
        
        return result;
    }

    private static String normalizeText(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase().trim();
        String norm = Normalizer.normalize(lower, Normalizer.Form.NFD);
        // remove diacritical marks
        norm = norm.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // collapse spaces
        return norm.replaceAll("\\s+", " ").trim();
    }

    public List<String> layDanhSachThuongHieu() {
        return getActiveProductsWithCache().stream()
                .filter(sp -> sp.getThuongHieu() != null && sp.getThuongHieu().getTenThuongHieu() != null)
                .map(sp -> sp.getThuongHieu().getTenThuongHieu())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> layDanhSachDanhMuc() {
        return getActiveProductsWithCache().stream()
                .filter(sp -> sp.getDanhMuc() != null && sp.getDanhMuc().getTenDanhMuc() != null)
                .map(sp -> sp.getDanhMuc().getTenDanhMuc())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> layDanhSachLoaiSanPham() {
        return getActiveProductsWithCache().stream()
                .filter(sp -> sp.getLoaiSanPham() != null && sp.getLoaiSanPham().getTenLoaiSanPham() != null)
                .map(sp -> sp.getLoaiSanPham().getTenLoaiSanPham())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> layDanhSachXuatXu() {
        return getActiveProductsWithCache().stream()
                .filter(sp -> sp.getXuatXu() != null && sp.getXuatXu().getTenXuatXu() != null)
                .map(sp -> sp.getXuatXu().getTenXuatXu())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> layDanhSachKieuDang() {
        return getActiveProductsWithCache().stream()
                .filter(sp -> sp.getKieuDang() != null && sp.getKieuDang().getTenKieuDang() != null)
                .map(sp -> sp.getKieuDang().getTenKieuDang())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> layDanhSachChatLieu() {
        return getActiveProductsWithCache().stream()
                .filter(sp -> sp.getChatLieu() != null && sp.getChatLieu().getTenChatLieu() != null)
                .map(sp -> sp.getChatLieu().getTenChatLieu())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public void save(SanPham sanPham) {
        if (sanPham.getId() == null) {
            sanPham.setCreateAt(LocalDateTime.now());
            sanPham.setTrangThai(Boolean.TRUE); // Mặc định active
        } else {
            sanPham.setUpdateAt(LocalDateTime.now());
        }
    sanPhamRepository.save(sanPham);
    // Bất kỳ thay đổi nào cũng cần làm mới cache để tìm kiếm/lists thấy ngay
    invalidateCache();
    }

    public void softDelete(Integer id) {
        Optional<SanPham> sanPhamOpt = sanPhamRepository.findById(id);
        sanPhamOpt.ifPresent(sanPham -> {
            sanPham.setDeleteAt(LocalDateTime.now());
            sanPham.setTrangThai(Boolean.FALSE); // Đánh dấu inactive
            sanPhamRepository.save(sanPham);
            invalidateCache();
        });
    }
}

// ...existing code...