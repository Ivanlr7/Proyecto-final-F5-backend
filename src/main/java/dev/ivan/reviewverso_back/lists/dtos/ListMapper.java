package dev.ivan.reviewverso_back.lists.dtos;

import dev.ivan.reviewverso_back.lists.ListEntity;
import dev.ivan.reviewverso_back.lists.ListItemEntity;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListMapper {
    
    @Value("${base-url}")
    private String baseUrl;

    public ListEntity listRequestDtoToListEntity(ListRequestDTO dto, UserEntity user) {
        ListEntity list = ListEntity.builder()
                .user(user)
                .title(dto.title())
                .description(dto.description())
                .items(new ArrayList<>())
                .build();

        if (dto.items() != null && !dto.items().isEmpty()) {
            for (int i = 0; i < dto.items().size(); i++) {
                ListItemDTO itemDto = dto.items().get(i);
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

        return list;
    }

    public ListResponseDTO listEntityToListResponseDto(ListEntity list) {
        List<ListItemResponseDTO> itemDtos = list.getItems().stream()
                .map(this::listItemEntityToDto)
                .collect(Collectors.toList());

        String profileImageUrl;
        String imagePath = "/api/v1/files/images/";
        if (list.getUser() != null && list.getUser().getProfile() != null) {
            String profileImage = list.getUser().getProfile().getProfileImage();
            if (profileImage != null && !profileImage.isBlank()) {
                if (profileImage.startsWith("http")) {
                    profileImageUrl = profileImage;
                } else {
                    profileImageUrl = baseUrl + imagePath + profileImage;
                }
            } else {
                profileImageUrl = baseUrl + imagePath + "default.png";
            }
        } else {
            profileImageUrl = baseUrl + imagePath + "default.png";
        }

        return new ListResponseDTO(
                list.getIdList(),
                list.getUser().getIdUser(),
                list.getUser().getUserName(),
                profileImageUrl,
                list.getTitle(),
                list.getDescription(),
                itemDtos,
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }

    private ListItemResponseDTO listItemEntityToDto(ListItemEntity item) {
        return new ListItemResponseDTO(
                item.getIdListItem(),
                item.getContentType(),
                item.getContentId(),
                item.getApiSource(),
                item.getPosition()
        );
    }
}
