package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.urbansteps.model.HinhAnh;
import vn.urbansteps.model.HinhAnhSanPham;
import vn.urbansteps.model.SanPham;
import vn.urbansteps.repository.HinhAnhRepository;
import vn.urbansteps.repository.HinhAnhSanPhamRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private HinhAnhSanPhamRepository hinhAnhSanPhamRepository;

    @Autowired
    private vn.urbansteps.repository.HinhAnhSanPhamChiTietRepository hinhAnhSanPhamChiTietRepository;

    @Autowired
    private SanPhamService sanPhamService;

    /**
     * Attach a main image to product: store file, create HinhAnh and HinhAnhSanPham link,
     * set product's representative image. Safe: does not set explicit ids.
     */
    public void addMainImage(SanPham sanPham, MultipartFile mainImage) {
        if (sanPham == null || mainImage == null || mainImage.isEmpty()) return;
        try {
            String url = imageStorageService.save(mainImage, "products");
            if (url == null) return;
            System.out.println("[ProductImageService] addMainImage returned url=" + url);

            HinhAnh hinh = new HinhAnh();
            hinh.setDuongDan(url);
            hinh.setLaAnhChinh(true);
            hinh = hinhAnhRepository.save(hinh);
            System.out.println("[ProductImageService] HinhAnh saved id=" + hinh.getId() + " duongDan=" + hinh.getDuongDan());

            // Update product representative image
            sanPham.setIdHinhAnhDaiDien(hinh);
            sanPhamService.save(sanPham);

            // Create gallery link and mark as primary
            HinhAnhSanPham link = new HinhAnhSanPham();
            link.setSanPham(sanPham);
            link.setHinhAnh(hinh);
            link.setLaAnhChinh(true);
            link.setThuTu(0);
            hinhAnhSanPhamRepository.save(link);

            // Unset other links' primary flag
            List<HinhAnhSanPham> links = hinhAnhSanPhamRepository.findBySanPham_Id(sanPham.getId());
            for (HinhAnhSanPham l : links) {
                if (!l.getId().equals(link.getId())) {
                    l.setLaAnhChinh(false);
                    hinhAnhSanPhamRepository.save(l);
                }
            }
        } catch (IOException e) {
            System.err.println("[ProductImageService] Error saving main image: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ProductImageService] Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Attach images to a specific variant (SanPhamChiTiet). Creates HinhAnh and HinhAnh_SanPhamChiTiet links.
     */
    public void addVariantImages(vn.urbansteps.model.SanPhamChiTiet sanPhamChiTiet, List<MultipartFile> files) {
        if (sanPhamChiTiet == null || files == null || files.isEmpty()) return;
        try {
            int order = 0;
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;
                try {
                    String url = imageStorageService.save(f, "variants");
                    if (url == null) continue;

                    HinhAnh img = new HinhAnh();
                    img.setDuongDan(url);
                    img.setLaAnhChinh(order == 0);
                    img = hinhAnhRepository.save(img);

                    vn.urbansteps.model.HinhAnh_SanPhamChiTiet link = new vn.urbansteps.model.HinhAnh_SanPhamChiTiet();
                    link.setSanPhamChiTiet(sanPhamChiTiet);
                    link.setHinhAnh(img);
                    hinhAnhSanPhamChiTietRepository.save(link);
                    order++;
                } catch (IOException ioe) {
                    System.err.println("[ProductImageService] Error saving variant image: " + ioe.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ProductImageService] Unexpected error in addVariantImages: " + e.getMessage());
        }
    }

    /**
     * Append detail images to product gallery. Does not remove existing links.
     */
    public void addDetailImages(SanPham sanPham, MultipartFile[] detailImages) {
        if (sanPham == null || detailImages == null || detailImages.length == 0) return;
        try {
            int order = 1;
            List<HinhAnhSanPham> current = hinhAnhSanPhamRepository.findBySanPham_IdOrderByThuTuAsc(sanPham.getId());
            if (current != null && !current.isEmpty()) {
                Integer max = current.stream().map(HinhAnhSanPham::getThuTu)
                        .filter(java.util.Objects::nonNull)
                        .max(Integer::compareTo).orElse(0);
                order = max + 1;
            }

            for (MultipartFile f : detailImages) {
                if (f == null || f.isEmpty()) continue;
                try {
                    String url = imageStorageService.save(f, "products");
                    if (url == null) continue;

                    System.out.println("[ProductImageService] addDetailImages saved url=" + url);

                    HinhAnh img = new HinhAnh();
                    img.setDuongDan(url);
                    img.setLaAnhChinh(false);
                    img = hinhAnhRepository.save(img);

                    HinhAnhSanPham link = new HinhAnhSanPham();
                    link.setSanPham(sanPham);
                    link.setHinhAnh(img);
                    link.setLaAnhChinh(false);
                    link.setThuTu(order++);
                    hinhAnhSanPhamRepository.save(link);
                } catch (IOException ioe) {
                    System.err.println("[ProductImageService] Error saving detail image: " + ioe.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ProductImageService] Unexpected error in addDetailImages: " + e.getMessage());
        }
    }
}
