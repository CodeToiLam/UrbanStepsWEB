package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.repository.SanPhamChiTietRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public List<SanPhamChiTiet> getBySanPhamId(Integer sanPhamId) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("SanPhamId cannot be null");
        }
    // Trả về tất cả biến thể còn hoạt động, kể cả soLuong = 0 để POS có thể thấy sản phẩm mới
    return sanPhamChiTietRepository.findBySanPhamIdAndTrangThaiTrue(sanPhamId);
    }

    public List<String> getKichCosBySanPhamId(Integer sanPhamId) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("SanPhamId cannot be null");
        }
        return sanPhamChiTietRepository.findDistinctKichCoBySanPhamId(sanPhamId);
    }

    public List<String> getMauSacsBySanPhamId(Integer sanPhamId) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("SanPhamId cannot be null");
        }
        return sanPhamChiTietRepository.findDistinctMauSacBySanPhamId(sanPhamId);
    }

    public SanPhamChiTiet getChiTietByOptions(Integer sanPhamId, String kichCo, String mauSac) {
        if (sanPhamId == null || kichCo == null || mauSac == null) {
            throw new IllegalArgumentException("SanPhamId, kichCo, and mauSac cannot be null");
        }
        return sanPhamChiTietRepository.findBySanPhamIdAndKichCoAndMauSac(sanPhamId, kichCo, mauSac);
    }

    public Map<String, Map<String, Integer>> getTonKhoBySanPhamId(Integer sanPhamId) {
        if (sanPhamId == null) {
            throw new IllegalArgumentException("SanPhamId cannot be null");
        }
        List<SanPhamChiTiet> chiTiets = getBySanPhamId(sanPhamId);
        return chiTiets.stream()
                .filter(ct -> ct.getKichCo() != null && ct.getMauSac() != null)
                .collect(Collectors.groupingBy(
                        ct -> ct.getKichCo().getTenKichCo(),
                        Collectors.toMap(
                                ct -> ct.getMauSac().getTenMauSac(),
                                SanPhamChiTiet::getSoLuong,
                                (v1, v2) -> v1
                        )
                ));
    }

    public List<SanPhamChiTiet> getAllSanPhamChiTiets() {
        return sanPhamChiTietRepository.findAll();
    }

    public Optional<SanPhamChiTiet> findById(Integer id) {
        return sanPhamChiTietRepository.findById(id);
    }

    // Kiểm tra tồn tại biến thể theo productId + kichCoId + mauSacId
    public Optional<SanPhamChiTiet> findBySanPhamIdAndKichCoIdAndMauSacId(Integer sanPhamId, Integer kichCoId, Integer mauSacId) {
        if (sanPhamId == null || kichCoId == null || mauSacId == null) return Optional.empty();
        return sanPhamChiTietRepository.findFirstBySanPham_IdAndKichCo_IdAndMauSac_Id(sanPhamId, kichCoId, mauSacId);
    }

    public SanPhamChiTiet save(SanPhamChiTiet chiTiet) {
        if (chiTiet.getId() == null) {
            chiTiet.setTrangThai(true); // Mặc định trạng thái là true khi tạo mới
            chiTiet.setCreateAt(LocalDateTime.now()); // Thêm thời gian tạo
        } else {
            chiTiet.setUpdateAt(LocalDateTime.now()); // Cập nhật thời gian
        }
        return sanPhamChiTietRepository.save(chiTiet);
    }

    public void softDelete(Integer id) {
        Optional<SanPhamChiTiet> chiTietOpt = findById(id);
        chiTietOpt.ifPresent(chiTiet -> {
            chiTiet.setTrangThai(false); // Xóa mềm
            chiTiet.setDeleteAt(LocalDateTime.now()); // Thêm thời gian xóa
            sanPhamChiTietRepository.save(chiTiet);
        });
    }
}