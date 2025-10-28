package dev.ivan.reviewverso_back.file;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    // Sobrescribe un archivo existente
    public boolean overwriteFile(String fileName, MultipartFile file) throws IOException {
        Path filePath = Paths.get(uploadDir, fileName);
        if (Files.exists(filePath)) {
            file.transferTo(filePath); // Sobrescribe el archivo
            return true;
        }
        return false;
    }
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


     //Recupera un archivo almacenado por su nombre
   
    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Archivo no encontrado: " + fileName);
        }
    }

    //Verifica si un archivo existe
     
    public boolean fileExists(String fileName) {
        Path filePath = Paths.get(uploadDir, fileName);
        return Files.exists(filePath);
    }

   
    //Elimina un archivo
 
    public boolean deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
}
