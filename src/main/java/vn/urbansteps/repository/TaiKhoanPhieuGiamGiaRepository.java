package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.TaiKhoanPhieuGiamGia;

import java.util.List;

@Repository
public interface TaiKhoanPhieuGiamGiaRepository extends JpaRepository<TaiKhoanPhieuGiamGia, Integer> {
    List<TaiKhoanPhieuGiamGia> findByTaiKhoanId(Integer taiKhoanId);
    boolean existsByTaiKhoanIdAndPhieuGiamGiaId(Integer taiKhoanId, Integer phieuGiamGiaId);
}
