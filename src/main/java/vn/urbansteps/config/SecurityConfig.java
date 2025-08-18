package vn.urbansteps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.urbansteps.security.LenientPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Chấp nhận mật khẩu cũ (plain) và dùng bcrypt cho mật khẩu mới
        return new LenientPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - Không cần đăng nhập
                        .requestMatchers("/", "/home", "/trang-chu").permitAll()
                        .requestMatchers("/san-pham/**", "/product/**").permitAll()
                        // New page-based return request endpoints
                        .requestMatchers("/don-hang/*/return-request").permitAll()
                        .requestMatchers("/don-hang/guest/*/cancel").permitAll()
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
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/data/**").permitAll()
                        // Uploaded files must be publicly readable for product images
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/favicon.ico", "/robots.txt").permitAll()

                        // API endpoints - Cho phép guest thao tác giỏ hàng
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()
                        .requestMatchers("/api/search/**").permitAll()

                        // Giỏ hàng và thanh toán cho guest cũng được phép truy cập
                        .requestMatchers("/gio-hang", "/gio-hang/", "/cart", "/cart/").permitAll()
                        .requestMatchers("/thanh-toan/**", "/checkout/**").permitAll()

                        // POS endpoints - Cho phép public
                        .requestMatchers("/pos/products", "/pos/order").permitAll()

                        // User area - Cần đăng nhập
                        // Order tracking: allow basic tracking & lookup for guests, protect detail/huy
                        .requestMatchers("/don-hang/tra-cuu", "/don-hang", "/order/track", "/order/lookup").permitAll()
                        // Guest-friendly detail by code+phone (handled in controller)
                        .requestMatchers("/don-hang/chi-tiet-ma/**").permitAll()
                        .requestMatchers("/don-hang/chi-tiet/**", "/don-hang/huy/**", "/order/**").authenticated()
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
                                return;
                            } else {
                                System.out.println("Debug: Redirecting user to /");
                                response.sendRedirect("/");
                                return;
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
                        .ignoringRequestMatchers("/api/**", "/orders/*/return-request") // Tắt CSRF cho API và endpoint trả hàng public
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

