package dev.ivan.reviewverso_back.lists.dtos;

import dev.ivan.reviewverso_back.lists.ListEntity;
import dev.ivan.reviewverso_back.lists.ListItemEntity;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListMapper {

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

        return new ListResponseDTO(
                list.getIdList(),
                list.getUser().getIdUser(),
                list.getUser().getUserName(),
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
