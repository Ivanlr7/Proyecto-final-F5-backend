package dev.ivan.reviewverso_back.register;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ivan.reviewverso_back.implementations.IRegisterService;
import dev.ivan.reviewverso_back.register.dto.RegisterRequestDTO;
import dev.ivan.reviewverso_back.register.dto.RegisterResponseDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("${api-endpoint}/register")
@RequiredArgsConstructor
public class RegisterController {

    private final IRegisterService<RegisterRequestDTO, RegisterResponseDTO> registerService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegisterResponseDTO> register(
        @RequestPart("data") RegisterRequestDTO dto,
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerService.register(dto, profileImage));
    }
}
