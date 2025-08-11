package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.urbansteps.model.DiaChiGiaoHang;
import vn.urbansteps.model.TaiKhoan;

import java.util.List;
import java.util.Optional;

public interface DiaChiGiaoHangRepository extends JpaRepository<DiaChiGiaoHang, Integer> {
    List<DiaChiGiaoHang> findByTaiKhoanOrderByIdDesc(TaiKhoan taiKhoan);
    Optional<DiaChiGiaoHang> findFirstByTaiKhoanOrderByIdAsc(TaiKhoan taiKhoan);
}
