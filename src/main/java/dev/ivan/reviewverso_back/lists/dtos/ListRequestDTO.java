package dev.ivan.reviewverso_back.lists.dtos;

import java.util.List;

public record ListRequestDTO(
    String title,
    String description,
    List<ListItemDTO> items
) {}
