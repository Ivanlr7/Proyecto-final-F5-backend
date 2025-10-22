package dev.ivan.reviewverso_back.reviews.service;

import dev.ivan.reviewverso_back.reviews.ReviewEntity;
import dev.ivan.reviewverso_back.reviews.ReviewRepository;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewMapper;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewRequestDTO;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewResponseDTO;
import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReviewServiceImplTest {
    @Test
    @DisplayName("likeReview adds user to likedByUsers if not already present")
    void likeReview_addsUser() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("user1").build();
        ReviewEntity review = ReviewEntity.builder().idReview(100L).likedByUsers(new java.util.HashSet<>()).build();
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.likeReview(100L, user);
        assertThat(review.getLikedByUsers(), contains(user));
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("likeReview does nothing if user already liked")
    void likeReview_noDuplicate() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("user1").build();
        java.util.Set<UserEntity> likes = new java.util.HashSet<>();
        likes.add(user);
        ReviewEntity review = ReviewEntity.builder().idReview(101L).likedByUsers(likes).build();
        when(reviewRepository.findById(101L)).thenReturn(Optional.of(review));

        reviewService.likeReview(101L, user);
        // Should not add again
        assertThat(review.getLikedByUsers().size(), is(1));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("unlikeReview removes user from likedByUsers if present")
    void unlikeReview_removesUser() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("user1").build();
        java.util.Set<UserEntity> likes = new java.util.HashSet<>();
        likes.add(user);
        ReviewEntity review = ReviewEntity.builder().idReview(102L).likedByUsers(likes).build();
        when(reviewRepository.findById(102L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.unlikeReview(102L, user);
        assertThat(review.getLikedByUsers(), is(empty()));
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("unlikeReview does nothing if user had not liked")
    void unlikeReview_noOpIfNotLiked() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("user1").build();
        ReviewEntity review = ReviewEntity.builder().idReview(103L).likedByUsers(new java.util.HashSet<>()).build();
        when(reviewRepository.findById(103L)).thenReturn(Optional.of(review));

        reviewService.unlikeReview(103L, user);
        assertThat(review.getLikedByUsers(), is(empty()));
        verify(reviewRepository, never()).save(any());
    }

    // @Test
    // @DisplayName("getEntities returns likeCount and likedByCurrentUser correctly")
    // void getEntities_likeInfo() {
    //     UserEntity currentUser = UserEntity.builder().idUser(1L).userName("user1").build();
    //     UserEntity otherUser = UserEntity.builder().idUser(2L).userName("user2").build();
    //     ReviewEntity review = ReviewEntity.builder().idReview(200L).likedByUsers(new java.util.HashSet<>()).build();
    //     review.getLikedByUsers().add(currentUser);
    //     review.getLikedByUsers().add(otherUser);
    //     when(reviewRepository.findAll()).thenReturn(List.of(review));
    //     ReviewResponseDTO dto = new ReviewResponseDTO(200L, 1L, "user1", null, ContentType.MOVIE, "MOV1", ApiSource.TMDB, "Title", "Text", 4.0, java.time.LocalDateTime.now(), java.time.LocalDateTime.now(), 2, true);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class),
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.user.UserEntity.class)
    //     )).thenReturn(dto);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class)
    //     )).thenReturn(dto);
    //     // Mock getCurrentUserOrNull
    //     when(userRepository.findByUserName("usuario1")).thenReturn(Optional.of(currentUser));

    //     List<ReviewResponseDTO> result = reviewService.getEntities();
    //     assertThat(result, hasSize(1));
    //     assertThat(result.get(0).likeCount(), is(2));
    //     assertThat(result.get(0).likedByCurrentUser(), is(true));
    // }

    @Test
    @DisplayName("validateReviewRequest lanza excepción si contentType es null")
    void validateReviewRequest_contentTypeNull() throws Exception {
        ReviewRequestDTO dto = new ReviewRequestDTO(null, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "Texto suficiente", 4.0);
        var method = reviewService.getClass().getDeclaredMethod("validateReviewRequest", ReviewRequestDTO.class);
        method.setAccessible(true);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(reviewService, dto));
        assertThat(ex.getCause().getMessage(), containsString("tipo de contenido"));
    }

    @Test
    @DisplayName("validateReviewRequest lanza excepción si contentId es null o blank")
    void validateReviewRequest_contentIdNullOrBlank() throws Exception {
        var method = reviewService.getClass().getDeclaredMethod("validateReviewRequest", ReviewRequestDTO.class);
        method.setAccessible(true);
        ReviewRequestDTO dtoNull = new ReviewRequestDTO(ContentType.MOVIE, null, dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "Texto suficiente", 4.0);
        ReviewRequestDTO dtoBlank = new ReviewRequestDTO(ContentType.MOVIE, "", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "Texto suficiente", 4.0);
        Exception ex1 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoNull));
        Exception ex2 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoBlank));
        assertThat(ex1.getCause().getMessage(), containsString("ID del contenido"));
        assertThat(ex2.getCause().getMessage(), containsString("ID del contenido"));
    }

    @Test
    @DisplayName("validateReviewRequest lanza excepción si apiSource es null")
    void validateReviewRequest_apiSourceNull() throws Exception {
        var method = reviewService.getClass().getDeclaredMethod("validateReviewRequest", ReviewRequestDTO.class);
        method.setAccessible(true);
        ReviewRequestDTO dto = new ReviewRequestDTO(ContentType.MOVIE, "id", null, "Titulo", "Texto suficiente", 4.0);
        Exception ex = assertThrows(Exception.class, () -> method.invoke(reviewService, dto));
        assertThat(ex.getCause().getMessage(), containsString("fuente de la API"));
    }

    @Test
    @DisplayName("validateReviewRequest lanza excepción si reviewTitle es null, blank o demasiado largo")
    void validateReviewRequest_reviewTitleNullBlankOrTooLong() throws Exception {
        var method = reviewService.getClass().getDeclaredMethod("validateReviewRequest", ReviewRequestDTO.class);
        method.setAccessible(true);
        ReviewRequestDTO dtoNull = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, null, "Texto suficiente", 4.0);
        ReviewRequestDTO dtoBlank = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "", "Texto suficiente", 4.0);
        ReviewRequestDTO dtoLong = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "a".repeat(201), "Texto suficiente", 4.0);
        Exception ex1 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoNull));
        Exception ex2 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoBlank));
        Exception ex3 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoLong));
        assertThat(ex1.getCause().getMessage(), containsString("título de la reseña"));
        assertThat(ex2.getCause().getMessage(), containsString("título de la reseña"));
        assertThat(ex3.getCause().getMessage(), containsString("superar los 200 caracteres"));
    }

    @Test
    @DisplayName("validateReviewRequest lanza excepción si reviewText es null, blank o demasiado corto")
    void validateReviewRequest_reviewTextNullBlankOrTooShort() throws Exception {
        var method = reviewService.getClass().getDeclaredMethod("validateReviewRequest", ReviewRequestDTO.class);
        method.setAccessible(true);
        ReviewRequestDTO dtoNull = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", null, 4.0);
        ReviewRequestDTO dtoBlank = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "", 4.0);
        ReviewRequestDTO dtoShort = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "corto", 4.0);
        Exception ex1 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoNull));
        Exception ex2 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoBlank));
        Exception ex3 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoShort));
        assertThat(ex1.getCause().getMessage(), containsString("texto de la reseña"));
        assertThat(ex2.getCause().getMessage(), containsString("texto de la reseña"));
        assertThat(ex3.getCause().getMessage(), containsString("al menos 10 caracteres"));
    }

    @Test
    @DisplayName("validateReviewRequest lanza excepción si rating es null o fuera de rango")
    void validateReviewRequest_ratingNullOrOutOfBounds() throws Exception {
        var method = reviewService.getClass().getDeclaredMethod("validateReviewRequest", ReviewRequestDTO.class);
        method.setAccessible(true);
        ReviewRequestDTO dtoNull = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "Texto suficiente", null);
        ReviewRequestDTO dtoLow = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "Texto suficiente", -1.0);
        ReviewRequestDTO dtoHigh = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "Texto suficiente", 5.1);
        Exception ex1 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoNull));
        Exception ex2 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoLow));
        Exception ex3 = assertThrows(Exception.class, () -> method.invoke(reviewService, dtoHigh));
        assertThat(ex1.getCause().getMessage(), containsString("valoración es obligatoria"));
        assertThat(ex2.getCause().getMessage(), containsString("debe estar entre 0.0 y 5.0"));
        assertThat(ex3.getCause().getMessage(), containsString("debe estar entre 0.0 y 5.0"));
    }

    @Test
    @DisplayName("validateReviewRequest no lanza excepción si el DTO es válido")
    void validateReviewRequest_validDtoDoesNotThrow() throws Exception {
        var method = reviewService.getClass().getDeclaredMethod("validateReviewRequest", ReviewRequestDTO.class);
        method.setAccessible(true);
        ReviewRequestDTO dto = new ReviewRequestDTO(ContentType.MOVIE, "id", dev.ivan.reviewverso_back.reviews.enums.ApiSource.TMDB, "Titulo", "Texto suficiente para la reseña", 4.0);
        assertDoesNotThrow(() -> method.invoke(reviewService, dto));
    }

    // @Test
    // @DisplayName("getReviewsByUserId retorna lista de reseñas del usuario")
    // void getReviewsByUserId_returnsList() {
    //     ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
    //     ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
    //     when(reviewRepository.findByUser_IdUser(2L)).thenReturn(List.of(r));
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class),
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.user.UserEntity.class)
    //     )).thenReturn(dto);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class)
    //     )).thenReturn(dto);
    //     List<ReviewResponseDTO> result = reviewService.getReviewsByUserId(2L);
    //     assertThat(result, hasSize(1));
    //     assertThat(result.get(0), is(dto));
    // }

    // @Test
    // @DisplayName("getReviewsByContent retorna lista de reseñas del contenido")
    // void getReviewsByContent_returnsList() {
    //     ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
    //     ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
    //     when(reviewRepository.findByContentTypeAndContentId(ContentType.MOVIE, "MOV123")).thenReturn(List.of(r));
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
    //     List<ReviewResponseDTO> result = reviewService.getReviewsByContent(ContentType.MOVIE, "MOV123");
    //     assertThat(result, hasSize(1));
    //     assertThat(result.get(0), is(dto));
    // }

    @Test
    @DisplayName("getAverageRatingByContent retorna el promedio correcto")
    void getAverageRatingByContent_returnsAverage() {
        when(reviewRepository.calculateAverageRating(ContentType.MOVIE, "MOV123")).thenReturn(4.5);
        Double avg = reviewService.getAverageRatingByContent(ContentType.MOVIE, "MOV123");
        assertThat(avg, is(4.5));
    }

    @Test
    @DisplayName("getTotalReviewsByContent retorna el total correcto")
    void getTotalReviewsByContent_returnsTotal() {
        when(reviewRepository.countByContentTypeAndContentId(ContentType.MOVIE, "MOV123")).thenReturn(3L);
        Long total = reviewService.getTotalReviewsByContent(ContentType.MOVIE, "MOV123");
        assertThat(total, is(3L));
    }

    @Test
    @DisplayName("createEntity lanza DuplicateReviewException si ya existe reseña")
    void createEntity_throwsDuplicateReviewException() {
        ReviewRequestDTO dto = new ReviewRequestDTO(ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto suficiente", 4.0);
        UserEntity user = UserEntity.builder().idUser(1L).userName("usuario1").build();
        when(userRepository.findByUserName("usuario1")).thenReturn(Optional.of(user));
        when(reviewRepository.existsByUser_IdUserAndContentTypeAndContentId(1L, ContentType.MOVIE, "MOV123")).thenReturn(true);

        Exception ex = assertThrows(dev.ivan.reviewverso_back.reviews.exceptions.DuplicateReviewException.class,
            () -> reviewService.createEntity(dto));
        assertThat(ex.getMessage(), containsString("Ya has escrito una reseña"));
    }

    @Test
    @DisplayName("getByID lanza ReviewNotFoundException si no existe la reseña")
    void getByID_throwsReviewNotFoundException() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(dev.ivan.reviewverso_back.reviews.exceptions.ReviewNotFoundException.class,
            () -> reviewService.getByID(99L));
        assertThat(ex.getMessage(), containsString("Reseña no encontrada"));
    }

    @Test
    @DisplayName("updateEntity lanza ReviewNotFoundException si no existe la reseña")
    void updateEntity_throwsReviewNotFoundException() {
        ReviewRequestDTO dto = new ReviewRequestDTO(ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto suficiente", 4.0);
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(dev.ivan.reviewverso_back.reviews.exceptions.ReviewNotFoundException.class,
            () -> reviewService.updateEntity(99L, dto));
        assertThat(ex.getMessage(), containsString("Reseña no encontrada"));
    }

    @Test
    @DisplayName("deleteEntity lanza ReviewNotFoundException si no existe la reseña")
    void deleteEntity_throwsReviewNotFoundException() {
        when(reviewRepository.existsById(99L)).thenReturn(false);
        Exception ex = assertThrows(dev.ivan.reviewverso_back.reviews.exceptions.ReviewNotFoundException.class,
            () -> reviewService.deleteEntity(99L));
        assertThat(ex.getMessage(), containsString("Reseña no encontrada"));
    }
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("usuario1");
    }

    @Test
    @DisplayName("createEntity crea y retorna la reseña correctamente")
    void createEntity_createsReview() {
        ReviewRequestDTO dto = new ReviewRequestDTO(ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto bastante largo para la reseña", 4.0);
        UserEntity user = UserEntity.builder().idUser(1L).userName("usuario1").build();
        ReviewEntity review = ReviewEntity.builder().user(user).contentType(ContentType.MOVIE).contentId("MOV123").apiSource(ApiSource.TMDB).reviewTitle("Titulo").reviewText("Texto de prueba").rating(4.0).build();
        ReviewEntity savedReview = ReviewEntity.builder().user(user).contentType(ContentType.MOVIE).contentId("MOV123").apiSource(ApiSource.TMDB).reviewTitle("Titulo").reviewText("Texto de prueba").rating(4.0).idReview(10L).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(10L, 1L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto de prueba", 4.0, savedReview.getCreatedAt(), savedReview.getUpdatedAt(), 0, false);

        when(userRepository.findByUserName("usuario1")).thenReturn(Optional.of(user));
        when(reviewRepository.existsByUser_IdUserAndContentTypeAndContentId(1L, ContentType.MOVIE, "MOV123")).thenReturn(false);
        when(reviewMapper.reviewRequestDTOToReviewEntity(dto, user)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(savedReview);
        when(reviewMapper.reviewEntityToReviewResponseDTO(savedReview)).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.createEntity(dto);
        assertThat(result, is(responseDTO));
        verify(reviewRepository).save(review);
    }

    // @Test
    // @DisplayName("getEntities retorna lista de reseñas")
    // void getEntities_returnsList() {
    //     ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
    //     ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
        
    //     when(reviewRepository.findAll()).thenReturn(List.of(r));
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class),
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.user.UserEntity.class)
    //     )).thenReturn(dto);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class)
    //     )).thenReturn(dto);
    //     List<ReviewResponseDTO> result = reviewService.getEntities();
    //     assertThat(result, hasSize(1));
    //     assertThat(result.get(0), is(dto));
    // }

    // @Test
    // @DisplayName("getByID retorna la reseña esperada")
    // void getByID_returnsReview() {
    //     ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
    //     ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
    //     when(reviewRepository.findById(1L)).thenReturn(Optional.of(r));
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class),
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.user.UserEntity.class)
    //     )).thenReturn(dto);
    //     when(reviewMapper.reviewEntityToReviewResponseDTO(
    //         org.mockito.ArgumentMatchers.any(dev.ivan.reviewverso_back.reviews.ReviewEntity.class)
    //     )).thenReturn(dto);
    //     ReviewResponseDTO result = reviewService.getByID(1L);
    //     assertThat(result, is(dto));
    // }
}
