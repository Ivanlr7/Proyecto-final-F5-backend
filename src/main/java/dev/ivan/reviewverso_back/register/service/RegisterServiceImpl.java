
package dev.ivan.reviewverso_back.register.service;

import dev.ivan.reviewverso_back.register.dto.*;
import dev.ivan.reviewverso_back.register.exceptions.RegisterIllegalArgument;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import dev.ivan.reviewverso_back.file.FileStorageService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final FileStorageService fileStorageService;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	@Transactional
	public RegisterResponseDTO register(RegisterRequestDTO request, MultipartFile profileImage) {
	
		if (userRepository.findByEmail(request.email()).isPresent()) {
			throw new RegisterIllegalArgument("El email ya está registrado");
		}

		if (userRepository.findByUserName(request.userName()).isPresent()) {
			throw new RegisterIllegalArgument("El nombre de usuario ya está registrado");
		}

		// Hasheo de roles
		String encodedPassword = passwordEncoder.encode(request.password());

		// Asignación de roles
		Set<RoleEntity> roles = new HashSet<>();
		if (request.roles() != null) {
			for (String roleName : request.roles()) {
				RoleEntity role = roleRepository.findByName(roleName)
						.orElseThrow(() -> new RegisterIllegalArgument("Rol no encontrado: " + roleName));
				roles.add(role);
			}
		}

	

		String imageFileName = null;
		if (profileImage != null && !profileImage.isEmpty()) {
			try {
				imageFileName = fileStorageService.storeFile(profileImage);
			} catch (IOException e) {
				throw new RegisterIllegalArgument("Error al guardar la imagen de perfil", e);
			}
		}
		ProfileEntity profile = ProfileEntity.builder()
				.profileImage(imageFileName)
				.build();

		UserEntity user = UserEntity.builder()
				.userName(request.userName())
				.email(request.email())
				.password(encodedPassword)
				.roles(roles)
				.profile(profile)
				.build();
		profile.setUser(user);


		userRepository.save(user);


		return RegisterMapper.toRegisterResponseDTO(user);
	}
}

    
