package vn.urbansteps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.urbansteps.model.TaiKhoan;

import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByTaiKhoan(String taiKhoan);
    Optional<TaiKhoan> findByEmail(String email);
}
