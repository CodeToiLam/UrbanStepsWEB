package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.repository.SanPhamRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SanPhamService {
    @Autowired
    private SanPhamRepository sanPhamRepository;

    // Cache để tránh duplicate queries
    private List<SanPham> cachedActiveProducts;
    private long lastCacheTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes

    private List<SanPham> getActiveProductsWithCache() {
        long currentTime = System.currentTimeMillis();
        if (cachedActiveProducts == null || (currentTime - lastCacheTime) > CACHE_DURATION) {
            System.out.println("Loading active products from database with JOIN FETCH...");
            cachedActiveProducts = sanPhamRepository.findByTrangThaiOrderByGiaBanAsc(true);
            lastCacheTime = currentTime;
            System.out.println("Loaded " + cachedActiveProducts.size() + " active products to cache");
            
            // Debug info
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
        // Lấy từ cache, sắp xếp theo giá thấp nhất (Flash Deal)
        List<SanPham> allProducts = getActiveProductsWithCache();
        System.out.println("Flash Deal Products count: " + allProducts.size());
        return allProducts;
    }

    public List<SanPham> getTopPopularProducts() {
        // Lấy từ cache, nhưng sắp xếp theo giá cao nhất (Top Popular)
        List<SanPham> allProducts = getActiveProductsWithCache();
        
        // Tạo danh sách mới với thứ tự ngược lại (giá cao nhất trước)
        List<SanPham> reversedProducts = new java.util.ArrayList<>(allProducts);
        java.util.Collections.reverse(reversedProducts);
        
        System.out.println("Top Popular Products count: " + reversedProducts.size());
        return reversedProducts;
    }

    public List<SanPham> getAllProducts() {
        return getActiveProductsWithCache();
    }

    public List<SanPham> findByThuongHieu(String thuongHieu) {
        return sanPhamRepository.findByThuongHieuTenThuongHieuAndTrangThai(thuongHieu, true);
    }

    public List<SanPham> getSaleProducts() {
        // Giả sử sản phẩm sale là những sản phẩm có giá bán < 2000000
        return sanPhamRepository.findByTrangThaiAndGiaBanLessThanOrderByGiaBanAsc(true, 2000000);
    }

    public List<SanPham> getHotProducts() {
        // Giả sử sản phẩm hot là những sản phẩm đắt nhất
        return sanPhamRepository.findTop10ByTrangThaiOrderByGiaBanDesc(true);
    }

    public Optional<SanPham> findById(Integer id) {
        return sanPhamRepository.findById(id);
    }

    public List<SanPham> getRelatedProducts(Integer productId) {
        // Lấy 4 sản phẩm ngẫu nhiên khác (trừ sản phẩm hiện tại)
        return sanPhamRepository.findTop4ByTrangThaiAndIdNotOrderByGiaBanAsc(true, productId);
    }
    
    // Methods for search functionality
    public List<SanPham> timKiemTheoTen(String keyword) {
        return sanPhamRepository.findByTenSanPhamContainingIgnoreCaseAndTrangThai(keyword, true);
    }
    
    public List<SanPham> layTatCaSanPham() {
        return getActiveProductsWithCache();
    }
    
    public List<String> layDanhSachThuongHieu() {
        List<SanPham> allProducts = getActiveProductsWithCache();
        return allProducts.stream()
            .filter(sp -> sp.getThuongHieu() != null)
            .map(sp -> sp.getThuongHieu().getTenThuongHieu())
            .distinct()
            .sorted()
            .collect(java.util.stream.Collectors.toList());
    }
}
