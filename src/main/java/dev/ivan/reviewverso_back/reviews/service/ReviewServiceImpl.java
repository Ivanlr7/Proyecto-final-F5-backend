package dev.ivan.reviewverso_back.reviews.service;


import dev.ivan.reviewverso_back.reviews.ReviewEntity;
import dev.ivan.reviewverso_back.reviews.ReviewRepository;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewMapper;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewRequestDTO;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewResponseDTO;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.reviews.exceptions.DuplicateReviewException;
import dev.ivan.reviewverso_back.reviews.exceptions.ReviewNotFoundException;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    @Override
    @Transactional
    public void likeReview(Long reviewId, UserEntity user) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id: " + reviewId));
        if (review.getLikedByUsers().contains(user)) {
            return; // Ya ha dado like
        }
        review.getLikedByUsers().add(user);
        reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void unlikeReview(Long reviewId, UserEntity user) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id: " + reviewId));
        if (!review.getLikedByUsers().contains(user)) {
            return; // No había like
        }
        review.getLikedByUsers().remove(user);
        reviewRepository.save(review);
    }

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponseDTO createEntity(ReviewRequestDTO dto) {

        validateReviewRequest(dto);
        

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));

        boolean reviewExists = reviewRepository.existsByUser_IdUserAndContentTypeAndContentId(
                user.getIdUser(), dto.contentType(), dto.contentId());
        
        if (reviewExists) {
            throw new DuplicateReviewException(
                    "Ya has escrito una reseña sobre este contenido. Puedes editarla en lugar de crear una nueva.");
        }

        ReviewEntity review = reviewMapper.reviewRequestDTOToReviewEntity(dto, user);
        ReviewEntity savedReview = reviewRepository.save(review);
        
        return reviewMapper.reviewEntityToReviewResponseDTO(savedReview);
    }
    
    private void validateReviewRequest(ReviewRequestDTO dto) {
        if (dto.contentType() == null) {
            throw new IllegalArgumentException("El tipo de contenido es obligatorio");
        }
        if (dto.contentId() == null || dto.contentId().isBlank()) {
            throw new IllegalArgumentException("El ID del contenido es obligatorio");
        }
        if (dto.apiSource() == null) {
            throw new IllegalArgumentException("La fuente de la API es obligatoria");
        }
        if (dto.reviewTitle() == null || dto.reviewTitle().isBlank()) {
            throw new IllegalArgumentException("El título de la reseña es obligatorio");
        }
        if (dto.reviewTitle().length() > 200) {
            throw new IllegalArgumentException("El título no puede superar los 200 caracteres");
        }
        if (dto.reviewText() == null || dto.reviewText().isBlank()) {
            throw new IllegalArgumentException("El texto de la reseña es obligatorio");
        }
        if (dto.reviewText().length() < 10) {
            throw new IllegalArgumentException("La reseña debe tener al menos 10 caracteres");
        }
        if (dto.rating() == null) {
            throw new IllegalArgumentException("La valoración es obligatoria");
        }
        if (dto.rating() < 0.0 || dto.rating() > 5.0) {
            throw new IllegalArgumentException("La valoración debe estar entre 0.0 y 5.0");
        }
    }


    @Override
    public List<ReviewResponseDTO> getEntities() {
        UserEntity currentUser = getCurrentUserOrNull();
        return reviewRepository.findAll().stream()
                .map(r -> reviewMapper.reviewEntityToReviewResponseDTO(r, currentUser))
                .collect(Collectors.toList());
    }


    @Override
    public ReviewResponseDTO getByID(Long id) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id: " + id));
        UserEntity currentUser = getCurrentUserOrNull();
        return reviewMapper.reviewEntityToReviewResponseDTO(review, currentUser);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateEntity(Long id, ReviewRequestDTO dto) {
        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id: " + id));

       
        if (dto.reviewTitle() != null) {
            if (dto.reviewTitle().length() > 200) {
                throw new IllegalArgumentException("El título no puede superar los 200 caracteres");
            }
            review.setReviewTitle(dto.reviewTitle());
        }
        if (dto.reviewText() != null) {
            if (dto.reviewText().length() < 10) {
                throw new IllegalArgumentException("La reseña debe tener al menos 10 caracteres");
            }
            review.setReviewText(dto.reviewText());
        }
        if (dto.rating() != null) {
            if (dto.rating() < 0.0 || dto.rating() > 5.0) {
                throw new IllegalArgumentException("La valoración debe estar entre 0.0 y 5.0");
            }
            review.setRating(dto.rating());
        }

        ReviewEntity updatedReview = reviewRepository.save(review);
        return reviewMapper.reviewEntityToReviewResponseDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteEntity(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Reseña no encontrada con id: " + id);
        }
        reviewRepository.deleteById(id);
    }


    @Override
    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
        UserEntity currentUser = getCurrentUserOrNull();
        return reviewRepository.findByUser_IdUser(userId).stream()
                .map(r -> reviewMapper.reviewEntityToReviewResponseDTO(r, currentUser))
                .collect(Collectors.toList());
    }


    @Override
    public List<ReviewResponseDTO> getReviewsByContent(ContentType contentType, String contentId) {
        UserEntity currentUser = getCurrentUserOrNull();
        return reviewRepository.findByContentTypeAndContentId(contentType, contentId).stream()
                .map(r -> reviewMapper.reviewEntityToReviewResponseDTO(r, currentUser))
                .collect(Collectors.toList());
    }

    private UserEntity getCurrentUserOrNull() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
                return null;
            }
            return userRepository.findByUserName(authentication.getName()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Double getAverageRatingByContent(ContentType contentType, String contentId) {
        Double average = reviewRepository.calculateAverageRating(contentType, contentId);
        return average != null ? average : 0.0;
    }

    @Override
    public Long getTotalReviewsByContent(ContentType contentType, String contentId) {
        return reviewRepository.countByContentTypeAndContentId(contentType, contentId);
    }
}
