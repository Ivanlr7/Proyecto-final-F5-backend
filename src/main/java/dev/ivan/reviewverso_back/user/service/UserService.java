package dev.ivan.reviewverso_back.user.service;

import java.util.Optional;

import dev.ivan.reviewverso_back.implementations.IUserService;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.dtos.UserRequestDTO;
import dev.ivan.reviewverso_back.user.dtos.UserResponseDTO;

public interface UserService extends IUserService<UserResponseDTO, UserRequestDTO> {
  Optional<UserResponseDTO> findByEmail(String email);
  Optional<UserResponseDTO> findByUserName(String userName);
}
