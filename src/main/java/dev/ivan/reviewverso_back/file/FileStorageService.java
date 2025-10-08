package dev.ivan.reviewverso_back.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private final String uploadDir = "uploads";

    public String storeFile(MultipartFile file) throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir, fileName);
        file.transferTo(filePath);
        return fileName;
    }
}
