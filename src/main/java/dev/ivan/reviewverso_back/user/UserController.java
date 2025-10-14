package dev.ivan.reviewverso_back.user;

import dev.ivan.reviewverso_back.user.dtos.UserRequestDTO;
import dev.ivan.reviewverso_back.user.dtos.UserResponseDTO;
import dev.ivan.reviewverso_back.user.exceptions.UserAccessDeniedException;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import dev.ivan.reviewverso_back.user.service.UserService;
import dev.ivan.reviewverso_back.implementations.IUserService;
import dev.ivan.reviewverso_back.role.RoleEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "${api-endpoint}/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService<UserResponseDTO, UserRequestDTO> userService;
    private final UserRepository userRepository;

    /**
     * Obtiene el ID del usuario desde el JWT
     */
    private Long getUserIdFromJwt(Principal principal) {
        if (principal instanceof Authentication authentication) {
            if (authentication.getPrincipal() instanceof Jwt jwt) {
                return jwt.getClaim("userId");
            }
        }
        return null;
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    private boolean hasRole(Principal principal, String roleName) {
        if (principal instanceof Authentication authentication) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> 
                        authority.getAuthority().equals("ROLE_" + roleName) || 
                        authority.getAuthority().equals("SCOPE_ROLE_" + roleName)
                    );
        }
        return false;
    }

    /**
     * Obtiene el usuario actual desde el JWT o por userName como fallback
     */
    private UserEntity getCurrentUserEntity(Principal principal) {
        Long userId = getUserIdFromJwt(principal);
        if (userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        }
        // Fallback: buscar por userName
        return userRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }


    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getEntities());
    }


   @GetMapping("/{id}")
public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id, Principal principal) {
    UserEntity currentUser = getCurrentUserEntity(principal);
    boolean isAdmin = hasRole(principal, "ADMIN");

    if (!isAdmin && !currentUser.getIdUser().equals(id)) {
        throw new UserAccessDeniedException("No puedes acceder a otro usuario");
    }

    return ResponseEntity.ok(userService.getByID(id));
}

   @PutMapping("/{id}")
public ResponseEntity<UserResponseDTO> updateUser(
        @PathVariable Long id,
        @RequestBody UserRequestDTO dto,
        Principal principal) {

    UserEntity currentUser = getCurrentUserEntity(principal);
    boolean isAdmin = hasRole(principal, "ADMIN");

    if (!isAdmin && !currentUser.getIdUser().equals(id)) {
        throw new UserAccessDeniedException("No puedes editar otro usuario");
    }

    return ResponseEntity.ok(userService.updateEntity(id, dto));
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id, Principal principal) {
    UserEntity currentUser = getCurrentUserEntity(principal);
    boolean isAdmin = hasRole(principal, "ADMIN");

    if (!isAdmin && !currentUser.getIdUser().equals(id)) {
        throw new UserAccessDeniedException("No puedes borrar otro usuario");
    }

    userService.deleteEntity(id);
    return ResponseEntity.noContent().build();
}
      
    @GetMapping("/me")
public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
    UserEntity user = getCurrentUserEntity(principal);
    return ResponseEntity.ok(userService.getByID(user.getIdUser()));
}
}