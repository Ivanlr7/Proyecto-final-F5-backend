package dev.ivan.reviewverso_back.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class FileControllerTest {
    @Mock
    private FileStorageService fileStorageService;
    @InjectMocks
    private FileController fileController;

    public FileControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getImage retorna 200 y el recurso si existe")
    void getImage_success() throws Exception {
        String fileName = "test.png";
        Resource resource = new ByteArrayResource("imgdata".getBytes());
        when(fileStorageService.loadFileAsResource(fileName)).thenReturn(resource);
        ResponseEntity<Resource> response = fileController.getImage(fileName);
        assertThat(response.getStatusCode().value(), is(200));
        assertThat(response.getBody(), is(resource));
        assertThat(response.getHeaders().getContentType(), is(MediaType.IMAGE_PNG));
    }

    @Test
    @DisplayName("getImage retorna 404 si no existe el archivo")
    void getImage_notFound() throws Exception {
        String fileName = "nope.png";
        when(fileStorageService.loadFileAsResource(fileName)).thenThrow(new RuntimeException("not found"));
        ResponseEntity<Resource> response = fileController.getImage(fileName);
        assertThat(response.getStatusCode().value(), is(404));
        assertThat(response.getBody(), is(nullValue()));
    }

    @Test
    @DisplayName("fileExists retorna true/false según existencia")
    void fileExists_works() {
        when(fileStorageService.fileExists("a.png")).thenReturn(true);
        when(fileStorageService.fileExists("b.png")).thenReturn(false);
        assertThat(fileController.fileExists("a.png").getBody(), is(true));
        assertThat(fileController.fileExists("b.png").getBody(), is(false));
    }
}
