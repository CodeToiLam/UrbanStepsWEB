package vn.urbansteps.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.TaiKhoanRepository;

import java.time.LocalDateTime;

@Transactional
@Service
public class TaiKhoanService {
    private static final Logger logger = LoggerFactory.getLogger(TaiKhoanService.class);
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public TaiKhoan registerTaiKhoan(TaiKhoan tk) {
        logger.info("Đăng ký tài khoản: {}", tk.getTaiKhoan());
        // Mã hóa mật khẩu
        tk.setMatKhau(passwordEncoder.encode(tk.getMatKhau()));
        tk.setCreateAt(tk.getCreateAt() != null ? tk.getCreateAt() : LocalDateTime.now());
        tk.setUpdateAt(tk.getUpdateAt() != null ? tk.getUpdateAt() : LocalDateTime.now());
        TaiKhoan savedTk = taiKhoanRepository.save(tk);
        logger.info("Đăng ký thành công: {}", savedTk.getTaiKhoan());
        return savedTk;
    }

    public TaiKhoan findByTaiKhoan(String taiKhoan) {
        return taiKhoanRepository.findByTaiKhoan(taiKhoan).orElse(null);
    }

    public TaiKhoan findByEmail(String email) {
        return taiKhoanRepository.findByEmail(email).orElse(null);
    }
}
