package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.SanPhamChiTiet;
import vn.urbansteps.repository.SanPhamChiTietRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    public List<SanPhamChiTiet> getBySanPhamId(Integer sanPhamId) {
        return sanPhamChiTietRepository.findBySanPhamIdAndTrangThaiTrue(sanPhamId);
    }

    public List<String> getKichCosBySanPhamId(Integer sanPhamId) {
        return sanPhamChiTietRepository.findDistinctKichCoBySanPhamId(sanPhamId);
    }

    public List<String> getMauSacsBySanPhamId(Integer sanPhamId) {
        return sanPhamChiTietRepository.findDistinctMauSacBySanPhamId(sanPhamId);
    }

    public SanPhamChiTiet getChiTietByOptions(Integer sanPhamId, String kichCo, String mauSac) {
        return sanPhamChiTietRepository.findBySanPhamIdAndKichCoAndMauSac(sanPhamId, kichCo, mauSac);
    }

    // Tạo một cấu trúc dữ liệu dễ sử dụng cho frontend
    public Map<String, Map<String, Integer>> getTonKhoBySanPhamId(Integer sanPhamId) {
        List<SanPhamChiTiet> chiTiets = getBySanPhamId(sanPhamId);

        return chiTiets.stream().collect(
                Collectors.groupingBy(
                        ct -> ct.getKichCo().getTenKichCo(),
                        Collectors.toMap(
                                ct -> ct.getMauSac().getTenMauSac(),
                                SanPhamChiTiet::getSoLuong
                        )
                )
        );
    }
}