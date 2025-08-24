package vn.urbansteps.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${app.upload.dir:src/main/resources/static/images}")
    private String root;

    public String save(MultipartFile file, String subdir) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String ext = getExtension(file.getOriginalFilename());
        String name = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);
    // ánh xạ tới cấu trúc
        String sub = (subdir == null) ? "" : subdir.trim();
    // chuẩn hóa subdir 
        sub = sub.replaceAll("^/+", "").replaceAll("/+$", "");
        // map some known names to the images layout used by the app
        if (sub.isEmpty()) {
            sub = "product"; // vị trí mặc định dưới images
        } else if ("products".equalsIgnoreCase(sub)) {
            sub = "product";
        } else if ("variants".equalsIgnoreCase(sub)) {
            sub = "product/variants";
        }

    // tạo đường dẫn thư mục đích. Nếu root cấu hình là đường dẫn tuyệt đối thì dùng trực tiếp; nếu không thì resolve từ project root
        Path base = Paths.get(root);
        Path dir;
        if (base.isAbsolute()) {
            dir = base.resolve(sub);
        } else {
            dir = Paths.get(System.getProperty("user.dir")).resolve(base).resolve(sub);
        }
        Files.createDirectories(dir);
        Path target = dir.resolve(name);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    System.out.println("[ImageStorageService] saved file system path: " + target.toAbsolutePath().toString());
        // Also copy into target/classes static folder so the running app (dev) can serve it
        Path targetInClasses = null;
        try {
            Path classesStatic = Paths.get(System.getProperty("user.dir")).resolve("target").resolve("classes").resolve("static").resolve("images");
            Path classesDir = classesStatic.resolve(sub);
            Files.createDirectories(classesDir);
            targetInClasses = classesDir.resolve(name);
            // copy from the file we just saved
            try {
                Files.copy(target, targetInClasses, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {}
        } catch (Exception ignored) {}
    // trả về đường dẫn web có thể truy cập (phục vụ từ resources/static/images)
        String webBase = "/images";
    // đảm bảo dấu gạch chéo đầu và ghép chuỗi đúng
        String webPath = webBase + "/" + (sub.isEmpty() ? "" : sub + "/") + name;
    // chuẩn hóa các dấu gạch chéo kép
    webPath = webPath.replaceAll("//+", "/");
        // Log web path and classes copy for debugging (after webPath is assembled)
        try {
            System.out.println("[ImageStorageService] webPath=" + webPath + " savedTo=" + target.toAbsolutePath() + (targetInClasses != null ? (" copiedTo=" + targetInClasses.toAbsolutePath()) : ""));
        } catch (Exception ignored) {}
        return webPath;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }
}
