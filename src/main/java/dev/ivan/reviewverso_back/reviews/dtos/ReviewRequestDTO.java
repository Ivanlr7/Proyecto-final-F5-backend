package dev.ivan.reviewverso_back.reviews.dtos;

import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;

public record ReviewRequestDTO(
        ContentType contentType,
        String contentId,
        ApiSource apiSource,
        String reviewTitle,
        String reviewText,
        Double rating
) {
}
