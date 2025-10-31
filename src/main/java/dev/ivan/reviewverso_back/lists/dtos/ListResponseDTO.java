package dev.ivan.reviewverso_back.lists.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ListResponseDTO(
    Long idList,
    Long userId,
    String userName,
    String title,
    String description,
    List<ListItemResponseDTO> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
