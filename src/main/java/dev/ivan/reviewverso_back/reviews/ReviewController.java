package dev.ivan.reviewverso_back.reviews;

import dev.ivan.reviewverso_back.implementations.IReviewService;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewRequestDTO;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewResponseDTO;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.reviews.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "${api-endpoint}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService<ReviewResponseDTO, ReviewRequestDTO> reviewService;
    private final ReviewService reviewLikeService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@RequestBody ReviewRequestDTO reviewRequest) {
        ReviewResponseDTO createdReview = reviewService.createEntity(reviewRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.getEntities();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        ReviewResponseDTO review = reviewService.getByID(id);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewRequestDTO reviewRequest) {
        ReviewResponseDTO updatedReview = reviewService.updateEntity(id, reviewRequest);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteEntity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewResponseDTO> reviews = ((ReviewService)reviewService).getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/content")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByContent(
            @RequestParam ContentType contentType,
            @RequestParam String contentId) {
        List<ReviewResponseDTO> reviews = ((ReviewService)reviewService).getReviewsByContent(contentType, contentId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/content/stats")
    public ResponseEntity<Map<String, Object>> getContentStats(
            @RequestParam ContentType contentType,
            @RequestParam String contentId) {
        Double averageRating = ((ReviewService)reviewService).getAverageRatingByContent(contentType, contentId);
        Long totalReviews = ((ReviewService)reviewService).getTotalReviewsByContent(contentType, contentId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("contentType", contentType);
        stats.put("contentId", contentId);
        stats.put("averageRating", averageRating);
        stats.put("totalReviews", totalReviews);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeReview(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        reviewLikeService.likeReview(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeReview(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        reviewLikeService.unlikeReview(id, user);
        return ResponseEntity.ok().build();
    }
}
