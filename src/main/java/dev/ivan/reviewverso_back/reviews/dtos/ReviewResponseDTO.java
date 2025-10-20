package dev.ivan.reviewverso_back.reviews.dtos;

import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long idReview,
        Long userId,
        String userName,
        String userProfileImageUrl,
        ContentType contentType,
        String contentId,
        ApiSource apiSource,
        String reviewTitle,
        String reviewText,
        Double rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
