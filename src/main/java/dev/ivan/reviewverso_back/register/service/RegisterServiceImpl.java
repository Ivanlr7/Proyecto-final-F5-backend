
package dev.ivan.reviewverso_back.register.service;

import dev.ivan.reviewverso_back.register.dto.*;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	@Transactional
	public RegisterResponseDTO register(RegisterRequestDTO request) {
	
		if (userRepository.findByEmail(request.email()).isPresent()) {
			throw new IllegalArgumentException("El email ya está registrado");
		}

		if (userRepository.findByUserName(request.userName()).isPresent()) {
			throw new IllegalArgumentException("El nombre de usuario ya está registrado");
		}

		// Hasheo de roles
		String encodedPassword = passwordEncoder.encode(request.password());

		// Asignación de roles
		Set<RoleEntity> roles = new HashSet<>();
		if (request.roles() != null) {
			for (String roleName : request.roles()) {
				RoleEntity role = roleRepository.findByName(roleName)
						.orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName));
				roles.add(role);
			}
		}

	
		ProfileEntity profile = ProfileEntity.builder()
				.profileImage(request.profileImage())
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

    
