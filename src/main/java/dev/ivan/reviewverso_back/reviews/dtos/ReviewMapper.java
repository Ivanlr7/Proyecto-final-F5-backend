package dev.ivan.reviewverso_back.reviews.dtos;

import dev.ivan.reviewverso_back.reviews.ReviewEntity;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponseDTO reviewEntityToReviewResponseDTO(ReviewEntity review) {
        String profileImageUrl = null;
        if (review.getUser() != null && review.getUser().getProfile() != null) {
            profileImageUrl = review.getUser().getProfile().getProfileImage();
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
