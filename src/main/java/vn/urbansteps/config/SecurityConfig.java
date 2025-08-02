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

                        // Order tracking - Cho phép public (cả guest và user)
                        .requestMatchers("/don-hang", "/don-hang/", "/order", "/order/").permitAll()

                        // User area - Cần đăng nhập
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
                            // Custom success handler để chuyển hướng admin đến /admin/products
                            System.out.println("Debug: User authorities = " + authentication.getAuthorities()); // Thêm log
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                                System.out.println("Debug: Redirecting to /admin/products");
                                response.sendRedirect("/admin/products");
                            }
                            else if (authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                                System.out.println("Debug: Redirecting to /admin/san-pham-chi-tiet");
                                response.sendRedirect("/admin/san-pham-chi-tiet");
                            }
                            else {
                                System.out.println("Debug: Redirecting to /");
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
                );

        return http.build();
    }
}

