package dev.ivan.reviewverso_back.lists.dtos;

import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;

public record ListItemResponseDTO(
    Long idListItem,
    ContentType contentType,
    String contentId,
    ApiSource apiSource,
    Integer position
) {}
