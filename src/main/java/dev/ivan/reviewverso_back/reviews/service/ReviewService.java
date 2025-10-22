package dev.ivan.reviewverso_back.reviews.service;

import dev.ivan.reviewverso_back.implementations.IReviewService;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewRequestDTO;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewResponseDTO;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;

import java.util.List;

public interface ReviewService extends IReviewService<ReviewResponseDTO, ReviewRequestDTO> {

    List<ReviewResponseDTO> getReviewsByUserId(Long userId);
    
    List<ReviewResponseDTO> getReviewsByContent(ContentType contentType, String contentId);
    
    Double getAverageRatingByContent(ContentType contentType, String contentId);
    
    Long getTotalReviewsByContent(ContentType contentType, String contentId);

    void unlikeReview(Long reviewId, UserEntity user);

    void likeReview(Long reviewId, UserEntity user);
}
