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
                        // Footer info pages should be public
                        .requestMatchers(
                                "/gioi-thieu", "/cau-hoi-thuong-gap", "/tuyen-dung",
                                "/dang-ky-ban-hang", "/lien-he", "/ho-tro", "/van-don",
                                "/huong-dan-tra-gop", "/huong-dan-doi-tra", "/chinh-sach-doi-tra",
                                "/chinh-sach-bao-hanh").permitAll()
                        .requestMatchers("/tim-kiem/**", "/search/**").permitAll()
                        .requestMatchers("/dang-ky", "/register", "/tai-khoan/dang-ky").permitAll()
                        .requestMatchers("/dang-nhap", "/login", "/tai-khoan/dang-nhap").permitAll()

                        // Static resources - CSS, JS, Images
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()
                        .requestMatchers("/favicon.ico", "/robots.txt").permitAll()

                        // API endpoints - Cho phép guest thao tác giỏ hàng
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()

                        // Giỏ hàng và thanh toán cho guest cũng được phép truy cập
                        .requestMatchers("/gio-hang", "/gio-hang/", "/cart", "/cart/").permitAll()
                        .requestMatchers("/thanh-toan/**", "/checkout/**").permitAll()

                        // POS endpoints - Cho phép public
                        .requestMatchers("/pos/products", "/pos/order").permitAll()

                        // User area - Cần đăng nhập
                        .requestMatchers("/don-hang/**", "/order/**").authenticated()
                        .requestMatchers("/tai-khoan/**", "/account/**").authenticated()
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
                            // Redirect based on role  
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                                System.out.println("Debug: Redirecting admin to /admin/products");
                                response.sendRedirect("/admin/products");
                            } else {
                                System.out.println("Debug: Redirecting user to /");
                                response.sendRedirect("/");
                            }
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
                )
                .rememberMe(remember -> remember
                        .key("urbanStepsRememberMeKey")
                        .tokenValiditySeconds(86400) // 1 day
                );

        return http.build();
    }
}

