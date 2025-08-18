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

    @Value("${app.upload.dir:uploads}")
    private String root;

    public String save(MultipartFile file, String subdir) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String ext = getExtension(file.getOriginalFilename());
        String name = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);
        Path dir = Paths.get(System.getProperty("user.dir"), root, subdir);
        Files.createDirectories(dir);
        Path target = dir.resolve(name);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return "/uploads/" + subdir + "/" + name;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }
}
