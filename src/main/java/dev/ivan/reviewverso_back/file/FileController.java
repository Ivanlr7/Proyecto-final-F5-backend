package dev.ivan.reviewverso_back.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;

@RestController
@RequestMapping(path = "${api-endpoint}/files")
@RequiredArgsConstructor
public class FileController {


    @PutMapping("/images/{fileName}")
    public ResponseEntity<?> overwriteImage(
            @PathVariable String fileName,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            boolean overwritten = fileStorageService.overwriteFile(fileName, file);
            if (overwritten) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al sobrescribir la imagen");
        }
    }

    private final FileStorageService fileStorageService;

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Resource resource = fileStorageService.loadFileAsResource(fileName);
 
            String contentType = determineContentType(fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") 
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

   
    @GetMapping("/exists/{fileName}")
    public ResponseEntity<Boolean> fileExists(@PathVariable String fileName) {
        boolean exists = fileStorageService.fileExists(fileName);
        return ResponseEntity.ok(exists);
    }

    private String determineContentType(String fileName) {
        String extension = "";
        if (fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }
        
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "json" -> "application/json";
            default -> "application/octet-stream";
        };
    }
}