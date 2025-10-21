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

class ReviewServiceImplTest {

    @Test
    @DisplayName("getReviewsByUserId retorna lista de reseñas del usuario")
    void getReviewsByUserId_returnsList() {
        ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
        ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
        when(reviewRepository.findByUser_IdUser(2L)).thenReturn(List.of(r));
        when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
        List<ReviewResponseDTO> result = reviewService.getReviewsByUserId(2L);
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(dto));
    }

    @Test
    @DisplayName("getReviewsByContent retorna lista de reseñas del contenido")
    void getReviewsByContent_returnsList() {
        ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
        ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
        when(reviewRepository.findByContentTypeAndContentId(ContentType.MOVIE, "MOV123")).thenReturn(List.of(r));
        when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
        List<ReviewResponseDTO> result = reviewService.getReviewsByContent(ContentType.MOVIE, "MOV123");
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(dto));
    }

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
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(10L, 1L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto de prueba", 4.0, savedReview.getCreatedAt(), savedReview.getUpdatedAt());

        when(userRepository.findByUserName("usuario1")).thenReturn(Optional.of(user));
        when(reviewRepository.existsByUser_IdUserAndContentTypeAndContentId(1L, ContentType.MOVIE, "MOV123")).thenReturn(false);
        when(reviewMapper.reviewRequestDTOToReviewEntity(dto, user)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(savedReview);
        when(reviewMapper.reviewEntityToReviewResponseDTO(savedReview)).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.createEntity(dto);
        assertThat(result, is(responseDTO));
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("getEntities retorna lista de reseñas")
    void getEntities_returnsList() {
        ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
        ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
        when(reviewRepository.findAll()).thenReturn(List.of(r));
        when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
        List<ReviewResponseDTO> result = reviewService.getEntities();
        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(dto));
    }

    @Test
    @DisplayName("getByID retorna la reseña esperada")
    void getByID_returnsReview() {
        ReviewEntity r = ReviewEntity.builder().idReview(1L).build();
        ReviewResponseDTO dto = mock(ReviewResponseDTO.class);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reviewMapper.reviewEntityToReviewResponseDTO(r)).thenReturn(dto);
        ReviewResponseDTO result = reviewService.getByID(1L);
        assertThat(result, is(dto));
    }
}
