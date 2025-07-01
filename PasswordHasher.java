import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Hash các mật khẩu đơn giản
        String admin123 = encoder.encode("admin123");
        String user123 = encoder.encode("user123");
        
        System.out.println("admin123 hash: " + admin123);
        System.out.println("user123 hash: " + user123);
    }
}
