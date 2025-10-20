package dev.ivan.reviewverso_back.reviews.dtos;

import dev.ivan.reviewverso_back.reviews.ReviewEntity;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    @Value("${base-url}")
    private String baseUrl;

    public ReviewResponseDTO reviewEntityToReviewResponseDTO(ReviewEntity review) {
        String profileImageUrl;
        String imagePath = "/api/v1/files/images/";
        if (review.getUser() != null && review.getUser().getProfile() != null) {
            String fileName = review.getUser().getProfile().getProfileImage();
            if (fileName != null && !fileName.isBlank()) {
                profileImageUrl = baseUrl + imagePath + fileName;
            } else {
                profileImageUrl = baseUrl + imagePath + "default.png";
            }
        } else {
            profileImageUrl = baseUrl + imagePath + "default.png";
        }
        return new ReviewResponseDTO(
                review.getIdReview(),
                review.getUser().getIdUser(),
                review.getUser().getUserName(),
                profileImageUrl,
                review.getContentType(),
                review.getContentId(),
                review.getApiSource(),
                review.getReviewTitle(),
                review.getReviewText(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    public ReviewEntity reviewRequestDTOToReviewEntity(ReviewRequestDTO dto, UserEntity user) {
        return ReviewEntity.builder()
                .user(user)
                .contentType(dto.contentType())
                .contentId(dto.contentId())
                .apiSource(dto.apiSource())
                .reviewTitle(dto.reviewTitle())
                .reviewText(dto.reviewText())
                .rating(dto.rating())
                .build();
    }
}
