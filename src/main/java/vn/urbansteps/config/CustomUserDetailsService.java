package vn.urbansteps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.urbansteps.model.TaiKhoan;
import vn.urbansteps.repository.TaiKhoanRepository;

import java.util.Arrays;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTaiKhoan(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại: " + username));

        // Với NoOpPasswordEncoder, ta không cần prefix {noop}
        String password = taiKhoan.getMatKhau();
        
        return User.builder()
                .username(taiKhoan.getTaiKhoan())
                .password(password)
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_" + taiKhoan.getRole())))
                .build();
    }
}
