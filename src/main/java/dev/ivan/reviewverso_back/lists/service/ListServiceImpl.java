package dev.ivan.reviewverso_back.lists.service;

import dev.ivan.reviewverso_back.lists.ListEntity;
import dev.ivan.reviewverso_back.lists.ListItemEntity;
import dev.ivan.reviewverso_back.lists.ListRepository;
import dev.ivan.reviewverso_back.lists.dtos.ListMapper;
import dev.ivan.reviewverso_back.lists.dtos.ListRequestDTO;
import dev.ivan.reviewverso_back.lists.dtos.ListResponseDTO;
import dev.ivan.reviewverso_back.lists.exceptions.ListNotFoundException;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService {

    private final ListRepository listRepository;
    private final UserRepository userRepository;
    private final ListMapper listMapper;

    @Override
    @Transactional
    public ListResponseDTO createEntity(ListRequestDTO dto) {
        UserEntity currentUser = getCurrentUser();
        ListEntity list = listMapper.listRequestDtoToListEntity(dto, currentUser);
        ListEntity savedList = listRepository.save(list);
        return listMapper.listEntityToListResponseDto(savedList);
    }

    @Override
    public List<ListResponseDTO> getEntities() {
        return listRepository.findAll().stream()
                .map(listMapper::listEntityToListResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ListResponseDTO getByID(Long id) {
        ListEntity list = listRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException("Lista no encontrada con id: " + id));
        return listMapper.listEntityToListResponseDto(list);
    }

    @Override
    @Transactional
    public ListResponseDTO updateEntity(Long id, ListRequestDTO dto) {
        ListEntity list = listRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException("Lista no encontrada con id: " + id));

        UserEntity currentUser = getCurrentUser();
        if (!list.getUser().getIdUser().equals(currentUser.getIdUser())) {
            throw new RuntimeException("No tienes permiso para editar esta lista");
        }

        if (dto.title() != null) {
            list.setTitle(dto.title());
        }
        if (dto.description() != null) {
            list.setDescription(dto.description());
        }

        if (dto.items() != null) {
            list.getItems().clear();
            
            for (int i = 0; i < dto.items().size(); i++) {
                var itemDto = dto.items().get(i);
                ListItemEntity item = ListItemEntity.builder()
                        .list(list)
                        .contentType(itemDto.contentType())
                        .contentId(itemDto.contentId())
                        .apiSource(itemDto.apiSource())
                        .position(i)
                        .build();
                list.getItems().add(item);
            }
        }

        ListEntity updatedList = listRepository.save(list);
        return listMapper.listEntityToListResponseDto(updatedList);
    }

    @Override
    @Transactional
    public void deleteEntity(Long id) {
        ListEntity list = listRepository.findById(id)
                .orElseThrow(() -> new ListNotFoundException("Lista no encontrada con id: " + id));

        UserEntity currentUser = getCurrentUser();
        if (!list.getUser().getIdUser().equals(currentUser.getIdUser())) {
            throw new RuntimeException("No tienes permiso para eliminar esta lista");
        }

        listRepository.deleteById(id);
    }

    @Override
    public List<ListResponseDTO> getListsByUserId(Long userId) {
        return listRepository.findByUserId(userId).stream()
                .map(listMapper::listEntityToListResponseDto)
                .collect(Collectors.toList());
    }

    private UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
    }
}
