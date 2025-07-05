package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.repository.SanPhamRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SanPhamService {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Cache để tránh duplicate queries
    private List<SanPham> cachedActiveProducts;
    private long lastCacheTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 phút

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
        List<SanPham> allProducts = getActiveProductsWithCache();
        System.out.println("Flash Deal Products count: " + allProducts.size());
        return allProducts;
    }

    public List<SanPham> getTopPopularProducts() {
        List<SanPham> allProducts = getActiveProductsWithCache();
        List<SanPham> reversedProducts = new java.util.ArrayList<>(allProducts);
        java.util.Collections.reverse(reversedProducts);
        System.out.println("Top Popular Products count: " + reversedProducts.size());
        return reversedProducts;
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
        return sanPhamRepository.findByTrangThaiAndGiaBanLessThanOrderByGiaBanAsc(Boolean.TRUE, new BigDecimal("2000000"));
    }

    public List<SanPham> getHotProducts() {
        return sanPhamRepository.findTop10ByTrangThaiOrderByGiaBanDesc(Boolean.TRUE);
    }

    public Optional<SanPham> findById(Integer id) {
        return sanPhamRepository.findById(id);
    }

    public List<SanPham> getRelatedProducts(Integer productId) {
        return sanPhamRepository.findTop4ByTrangThaiAndIdNotOrderByGiaBanAsc(Boolean.TRUE, productId);
    }

    public List<SanPham> timKiemTheoTen(String keyword) {
        return sanPhamRepository.findByTenSanPhamContainingIgnoreCaseAndTrangThai(keyword, Boolean.TRUE);
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
    }

    public void softDelete(Integer id) {
        Optional<SanPham> sanPhamOpt = sanPhamRepository.findById(id);
        sanPhamOpt.ifPresent(sanPham -> {
            sanPham.setDeleteAt(LocalDateTime.now());
            sanPham.setTrangThai(Boolean.FALSE); // Đánh dấu inactive
            sanPhamRepository.save(sanPham);
        });
    }
}