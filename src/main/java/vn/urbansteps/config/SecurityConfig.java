package vn.urbansteps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng NoOpPasswordEncoder cho demo với mật khẩu plain text
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - Không cần đăng nhập
                .requestMatchers("/", "/home", "/trang-chu").permitAll()
                .requestMatchers("/san-pham/**", "/product/**").permitAll()
                .requestMatchers("/tim-kiem/**", "/search/**").permitAll()
                .requestMatchers("/dang-ky", "/register").permitAll()
                .requestMatchers("/dang-nhap", "/login").permitAll()
                
                // Static resources - CSS, JS, Images
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                .requestMatchers("/favicon.ico", "/robots.txt").permitAll()
                
                // API endpoints - Tùy theo yêu cầu
                .requestMatchers("/api/public/**").permitAll()
                
                // User area - Cần đăng nhập
                .requestMatchers("/gio-hang/**", "/cart/**").authenticated()
                .requestMatchers("/don-hang/**", "/order/**").authenticated()
                .requestMatchers("/tai-khoan/**", "/account/**").authenticated()
                .requestMatchers("/thanh-toan/**", "/checkout/**").authenticated()
                .requestMatchers("/api/user/**").authenticated()
                
                // Admin area - Cần quyền ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Default - Cần đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/dang-nhap")
                .loginProcessingUrl("/dang-nhap")
                .usernameParameter("taiKhoan")
                .passwordParameter("matKhau")
                .defaultSuccessUrl("/", true)
                .successHandler((request, response, authentication) -> {
                    // Custom success handler để đảm bảo redirect đúng
                    response.sendRedirect("/");
                })
                .failureUrl("/dang-nhap?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/dang-xuat")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**") // Tắt CSRF cho API
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
            
        return http.build();
    }
}