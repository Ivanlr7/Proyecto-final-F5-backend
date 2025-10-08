package dev.ivan.reviewverso_back.register.service;

import dev.ivan.reviewverso_back.implementations.IRegisterService;
import dev.ivan.reviewverso_back.register.dto.RegisterRequestDTO;
import dev.ivan.reviewverso_back.register.dto.RegisterResponseDTO;

import org.springframework.web.multipart.MultipartFile;

public interface RegisterService extends IRegisterService<RegisterRequestDTO, RegisterResponseDTO> {
	@Override
	RegisterResponseDTO register(RegisterRequestDTO request, MultipartFile profileImage);
}
