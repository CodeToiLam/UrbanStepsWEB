package vn.urbansteps.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import vn.urbansteps.service.GioHangService;
import vn.urbansteps.service.TaiKhoanService;
import vn.urbansteps.model.TaiKhoan;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private GioHangService gioHangService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Sử dụng NoOpPasswordEncoder cho demo với mật khẩu plain text
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("Debug: Login success handler called");
            System.out.println("Debug: User authorities = " + authentication.getAuthorities());
            
            // Merge cart từ session sang user account
            try {
                String sessionId = request.getSession().getId();
                String username = authentication.getName();
                TaiKhoan taiKhoan = taiKhoanService.findByTaiKhoan(username);
                
                if (taiKhoan != null) {
                    boolean mergeResult = gioHangService.mergeSessionCartToUserCart(sessionId, taiKhoan.getId());
                    System.out.println("Debug: Cart merge result = " + mergeResult);
                }
            } catch (Exception e) {
                System.err.println("Error merging cart: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Redirect based on role
            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                System.out.println("Debug: Redirecting admin to /admin/products");
                response.sendRedirect("/admin/products");
            } else {
                System.out.println("Debug: Redirecting user to /");
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - Không cần đăng nhập
                        .requestMatchers("/", "/home", "/trang-chu").permitAll()
                        .requestMatchers("/san-pham/**", "/product/**").permitAll()
                        .requestMatchers("/tim-kiem/**", "/search/**").permitAll()
                        .requestMatchers("/dang-ky", "/register", "/tai-khoan/dang-ky").permitAll()
                        .requestMatchers("/dang-nhap", "/login", "/tai-khoan/dang-nhap").permitAll()

                        // Static resources - CSS, JS, Images
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                        .requestMatchers("/favicon.ico", "/robots.txt").permitAll()

                        // API endpoints - Cho phép guest thao tác giỏ hàng
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()

                        // Giỏ hàng cho guest cũng được phép truy cập
                        .requestMatchers("/gio-hang", "/gio-hang/", "/cart", "/cart/").permitAll()

                        // POS endpoints - Cho phép public
                        .requestMatchers("/pos/products", "/pos/order").permitAll()

                        // User area - Cần đăng nhập
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
                        .successHandler(customSuccessHandler())
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

