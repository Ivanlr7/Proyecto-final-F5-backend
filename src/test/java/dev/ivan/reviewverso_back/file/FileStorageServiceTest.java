package dev.ivan.reviewverso_back.file;

import org.junit.jupiter.api.*;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FileStorageServiceTest {
    private FileStorageService service;
    private final String uploadDir = "uploads";

    @BeforeEach
    void setUp() throws IOException {
        service = new FileStorageService();
        Files.createDirectories(Path.of(uploadDir));
    }

    @AfterEach
    void cleanUp() throws IOException {
        // Borra todos los archivos creados en uploads
        Files.walk(Path.of(uploadDir))
                .filter(Files::isRegularFile)
                .forEach(path -> path.toFile().delete());
    }

    @Test
    @DisplayName("storeFile guarda el archivo y retorna el nombre")
    void storeFile_savesFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "contenido".getBytes());
        String fileName = service.storeFile(file);
        assertThat(fileName, endsWith(".txt"));
        assertThat(Files.exists(Path.of(uploadDir, fileName)), is(true));
    }

    @Test
    @DisplayName("loadFileAsResource retorna el recurso si existe")
    void loadFileAsResource_success() throws IOException {
        String content = "abc";
        String fileName = UUID.randomUUID() + ".txt";
        Path filePath = Path.of(uploadDir, fileName);
        Files.writeString(filePath, content);
        Resource resource = service.loadFileAsResource(fileName);
        assertThat(resource.exists(), is(true));
        assertThat(resource.isReadable(), is(true));
        assertThat(resource.getFilename(), is(fileName));
    }

    @Test
    @DisplayName("loadFileAsResource lanza excepción si no existe")
    void loadFileAsResource_notFound() {
        String fileName = UUID.randomUUID() + ".txt";
        Exception ex = Assertions.assertThrows(RuntimeException.class, () -> service.loadFileAsResource(fileName));
        assertThat(ex.getMessage(), containsString("Archivo no encontrado"));
    }

    @Test
    @DisplayName("fileExists retorna true/false según existencia")
    void fileExists_works() throws IOException {
        String fileName = UUID.randomUUID() + ".txt";
        Path filePath = Path.of(uploadDir, fileName);
        Files.writeString(filePath, "contenido");
        assertThat(service.fileExists(fileName), is(true));
        Files.delete(filePath);
        assertThat(service.fileExists(fileName), is(false));
    }

    @Test
    @DisplayName("deleteFile elimina el archivo y retorna true")
    void deleteFile_success() throws IOException {
        String fileName = UUID.randomUUID() + ".txt";
        Path filePath = Path.of(uploadDir, fileName);
        Files.writeString(filePath, "contenido");
        assertThat(service.deleteFile(fileName), is(true));
        assertThat(Files.exists(filePath), is(false));
    }

    @Test
    @DisplayName("deleteFile retorna false si el archivo no existe")
    void deleteFile_notFound() {
        String fileName = UUID.randomUUID() + ".txt";
        assertThat(service.deleteFile(fileName), is(false));
    }
}
