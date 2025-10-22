package dev.ivan.reviewverso_back.reviews.dtos;

import dev.ivan.reviewverso_back.reviews.ReviewEntity;
import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {
    private ReviewMapper reviewMapper;
    private final String BASE_URL = "http://test-url";

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapper();
        ReflectionTestUtils.setField(reviewMapper, "baseUrl", BASE_URL);
    }

    @Test
    void testReviewEntityToReviewResponseDTO_withProfileImage() {
        ProfileEntity profile = new ProfileEntity();
        profile.setProfileImage("avatar123.png");
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .userName("testuser")
                .profile(profile)
                .build();
        ReviewEntity review = ReviewEntity.builder()
                .idReview(10L)
                .user(user)
                .contentType(ContentType.MOVIE)
                .contentId("550")
                .apiSource(ApiSource.TMDB)
                .reviewTitle("Título")
                .reviewText("Texto de la reseña")
                .rating(4.5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ReviewResponseDTO dto = reviewMapper.reviewEntityToReviewResponseDTO(review);
        assertEquals(BASE_URL + "/api/v1/files/images/avatar123.png", dto.userProfileImageUrl());
        assertEquals("testuser", dto.userName());
        assertEquals(ContentType.MOVIE, dto.contentType());
    }

        @Test
        void testReviewEntityToReviewResponseDTO_likeCount_and_likedByCurrentUser() {
                UserEntity user1 = UserEntity.builder().idUser(1L).userName("user1").build();
                UserEntity user2 = UserEntity.builder().idUser(2L).userName("user2").build();
                UserEntity user3 = UserEntity.builder().idUser(3L).userName("user3").build();
                ReviewEntity review = ReviewEntity.builder().idReview(100L).user(user1).build();
                review.getLikedByUsers().add(user2);
                review.getLikedByUsers().add(user3);

                // When currentUser is user2, likedByCurrentUser should be true
                ReviewResponseDTO dtoLiked = reviewMapper.reviewEntityToReviewResponseDTO(review, user2);
                assertEquals(2, dtoLiked.likeCount());
                assertTrue(dtoLiked.likedByCurrentUser());

                // When currentUser is user1 (author, not in likedByUsers), likedByCurrentUser should be false
                ReviewResponseDTO dtoNotLiked = reviewMapper.reviewEntityToReviewResponseDTO(review, user1);
                assertEquals(2, dtoNotLiked.likeCount());
                assertFalse(dtoNotLiked.likedByCurrentUser());

                // When currentUser is null, likedByCurrentUser should be false
                ReviewResponseDTO dtoNull = reviewMapper.reviewEntityToReviewResponseDTO(review, null);
                assertEquals(2, dtoNull.likeCount());
                assertFalse(dtoNull.likedByCurrentUser());
        }

    @Test
    void testReviewEntityToReviewResponseDTO_noProfileImage() {
        ProfileEntity profile = new ProfileEntity();
        profile.setProfileImage(null);
        UserEntity user = UserEntity.builder()
                .idUser(2L)
                .userName("nouserimg")
                .profile(profile)
                .build();
        ReviewEntity review = ReviewEntity.builder()
                .idReview(11L)
                .user(user)
                .contentType(ContentType.BOOK)
                .contentId("123")
                .apiSource(ApiSource.OPENLIBRARY)
                .reviewTitle("Libro")
                .reviewText("Texto libro")
                .rating(3.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ReviewResponseDTO dto = reviewMapper.reviewEntityToReviewResponseDTO(review);
        assertEquals(BASE_URL + "/api/v1/files/images/default.png", dto.userProfileImageUrl());
    }

    @Test
    void testReviewEntityToReviewResponseDTO_noProfile() {
        UserEntity user = UserEntity.builder()
                .idUser(3L)
                .userName("nouserprofile")
                .profile(null)
                .build();
        ReviewEntity review = ReviewEntity.builder()
                .idReview(12L)
                .user(user)
                .contentType(ContentType.GAME)
                .contentId("999")
                .apiSource(ApiSource.IGDB)
                .reviewTitle("Juego")
                .reviewText("Texto juego")
                .rating(5.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ReviewResponseDTO dto = reviewMapper.reviewEntityToReviewResponseDTO(review);
        assertEquals(BASE_URL + "/api/v1/files/images/default.png", dto.userProfileImageUrl());
    }

    @Test
    void testReviewRequestDTOToReviewEntity() {
        UserEntity user = UserEntity.builder()
                .idUser(42L)
                .userName("reviewer")
                .build();
        ReviewRequestDTO dto = new ReviewRequestDTO(
                ContentType.SERIES,
                "9999",
                ApiSource.TMDB,
                "Gran serie",
                "Me encantó la trama y los personajes.",
                4.0
        );
        ReviewEntity entity = reviewMapper.reviewRequestDTOToReviewEntity(dto, user);
        assertEquals(user, entity.getUser());
        assertEquals(ContentType.SERIES, entity.getContentType());
        assertEquals("9999", entity.getContentId());
        assertEquals(ApiSource.TMDB, entity.getApiSource());
        assertEquals("Gran serie", entity.getReviewTitle());
        assertEquals("Me encantó la trama y los personajes.", entity.getReviewText());
        assertEquals(4.0, entity.getRating());
    }
}
