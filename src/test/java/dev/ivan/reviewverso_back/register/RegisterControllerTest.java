package dev.ivan.reviewverso_back.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivan.reviewverso_back.register.dto.RegisterRequestDTO;
import dev.ivan.reviewverso_back.register.dto.RegisterResponseDTO;
import dev.ivan.reviewverso_back.implementations.IRegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IRegisterService<RegisterRequestDTO, RegisterResponseDTO> registerService;
    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequestDTO requestDTO;
    private RegisterResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", null, Set.of("ROLE_USER"));
        responseDTO = new RegisterResponseDTO(1L, "usuario", "correo@ejemplo.com", "test.png", Set.of("ROLE_USER"));
    }

    @Test
    void register_withImage_returnsCreated() throws Exception {
        MockMultipartFile image = new MockMultipartFile("profileImage", "test.png", MediaType.IMAGE_PNG_VALUE, "fakeimg".getBytes());
        MockMultipartFile data = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(requestDTO));
        Mockito.when(registerService.register(any(RegisterRequestDTO.class), any())).thenReturn(responseDTO);

        mockMvc.perform(multipart("/api/v1/register")
                .file(data)
                .file(image)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("usuario"))
                .andExpect(jsonPath("$.profileImage").value("test.png"));
    }

    @Test
    void register_withoutImage_returnsCreated() throws Exception {
        RegisterResponseDTO responseNoImg = new RegisterResponseDTO(1L, "usuario", "correo@ejemplo.com", null, Set.of("ROLE_USER"));
        MockMultipartFile data = new MockMultipartFile("data", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(requestDTO));
        Mockito.when(registerService.register(any(RegisterRequestDTO.class), any())).thenReturn(responseNoImg);

        mockMvc.perform(multipart("/api/v1/register")
                .file(data)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("usuario"))
                .andExpect(jsonPath("$.profileImage").doesNotExist());
    }
}
