package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.urbansteps.model.HinhAnh_SanPhamChiTiet;

@Repository
public interface HinhAnhSanPhamChiTietRepository extends JpaRepository<HinhAnh_SanPhamChiTiet, Integer> {
}
