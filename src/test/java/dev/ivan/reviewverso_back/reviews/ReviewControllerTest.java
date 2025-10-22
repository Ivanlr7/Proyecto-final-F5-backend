package dev.ivan.reviewverso_back.reviews;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewRequestDTO;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewResponseDTO;
import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.reviews.service.ReviewService;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "usuario1", roles = {"USER"})
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /reviews crea una reseña y responde con 201")
    void createReview_returnsCreated() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(
                ContentType.MOVIE, "MOV123", ApiSource.TMDB, 
                "Gran película", "Me encantó esta película", 4.5);
        
        ReviewResponseDTO response = new ReviewResponseDTO(
                1L, 2L, "usuario1", "http://test.com/image.png",
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Gran película", "Me encantó esta película", 4.5,
                LocalDateTime.now(), LocalDateTime.now(), 0, false);
        
        when(reviewService.createEntity(any(ReviewRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReview", is(1)))
                .andExpect(jsonPath("$.userName", is("usuario1")))
                .andExpect(jsonPath("$.contentType", is("MOVIE")))
                .andExpect(jsonPath("$.contentId", is("MOV123")))
                .andExpect(jsonPath("$.reviewTitle", is("Gran película")))
                .andExpect(jsonPath("$.rating", is(4.5)))
                .andExpect(jsonPath("$.likeCount", is(0)))
                .andExpect(jsonPath("$.likedByCurrentUser", is(false)));
        
        verify(reviewService, times(1)).createEntity(any(ReviewRequestDTO.class));
    }

    @Test
    @DisplayName("GET /reviews retorna lista de todas las reseñas")
    void getAllReviews_returnsList() throws Exception {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                1L, 2L, "usuario1", null,
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Título 1", "Texto 1", 4.0,
                LocalDateTime.now(), LocalDateTime.now(), 3, true);
        
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                2L, 3L, "usuario2", null,
                ContentType.BOOK, "BOOK1", ApiSource.OPENLIBRARY,
                "Libro genial", "Muy buena lectura", 5.0,
                LocalDateTime.now(), LocalDateTime.now(), 0, false);
        
        when(reviewService.getEntities()).thenReturn(List.of(review1, review2));

        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idReview", is(1)))
                .andExpect(jsonPath("$[0].userName", is("usuario1")))
                .andExpect(jsonPath("$[0].likeCount", is(3)))
                .andExpect(jsonPath("$[0].likedByCurrentUser", is(true)))
                .andExpect(jsonPath("$[1].contentType", is("BOOK")));
        
        verify(reviewService, times(1)).getEntities();
    }

    @Test
    @DisplayName("GET /reviews/{id} retorna una reseña específica")
    void getReviewById_returnsReview() throws Exception {
        ReviewResponseDTO review = new ReviewResponseDTO(
                1L, 2L, "usuario1", null,
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Título", "Texto", 4.5,
                LocalDateTime.now(), LocalDateTime.now(), 5, true);
        
        when(reviewService.getByID(1L)).thenReturn(review);

        mockMvc.perform(get("/api/v1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReview", is(1)))
                .andExpect(jsonPath("$.userName", is("usuario1")))
                .andExpect(jsonPath("$.likeCount", is(5)))
                .andExpect(jsonPath("$.likedByCurrentUser", is(true)));
        
        verify(reviewService, times(1)).getByID(1L);
    }

    @Test
    @DisplayName("PUT /reviews/{id} actualiza una reseña")
    void updateReview_returnsUpdated() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Título actualizado", "Texto actualizado más largo", 4.8);
        
        ReviewResponseDTO response = new ReviewResponseDTO(
                1L, 2L, "usuario1", null,
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Título actualizado", "Texto actualizado más largo", 4.8,
                LocalDateTime.now(), LocalDateTime.now(), 2, false);
        
        when(reviewService.updateEntity(eq(1L), any(ReviewRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReview", is(1)))
                .andExpect(jsonPath("$.reviewTitle", is("Título actualizado")))
                .andExpect(jsonPath("$.rating", is(4.8)))
                .andExpect(jsonPath("$.likeCount", is(2)));
        
        verify(reviewService, times(1)).updateEntity(eq(1L), any(ReviewRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /reviews/{id} elimina una reseña")
    void deleteReview_returnsNoContent() throws Exception {
        doNothing().when(reviewService).deleteEntity(1L);

        mockMvc.perform(delete("/api/v1/reviews/1"))
                .andExpect(status().isNoContent());
        
        verify(reviewService, times(1)).deleteEntity(1L);
    }

    @Test
    @DisplayName("GET /reviews/user/{userId} retorna reseñas del usuario")
    void getReviewsByUser_returnsList() throws Exception {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                1L, 2L, "usuario1", null,
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Título 1", "Texto 1", 4.0,
                LocalDateTime.now(), LocalDateTime.now(), 1, false);
        
        when(reviewService.getReviewsByUserId(2L)).thenReturn(List.of(review1));

        mockMvc.perform(get("/api/v1/reviews/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(2)))
                .andExpect(jsonPath("$[0].userName", is("usuario1")));
        
        verify(reviewService, times(1)).getReviewsByUserId(2L);
    }

    @Test
    @DisplayName("GET /reviews/content retorna reseñas por tipo de contenido e ID")
    void getReviewsByContent_returnsList() throws Exception {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                1L, 2L, "usuario1", null,
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Título", "Texto", 4.0,
                LocalDateTime.now(), LocalDateTime.now(), 3, true);
        
        when(reviewService.getReviewsByContent(ContentType.MOVIE, "MOV123"))
                .thenReturn(List.of(review1));

        mockMvc.perform(get("/api/v1/reviews/content")
                .param("contentType", "MOVIE")
                .param("contentId", "MOV123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contentType", is("MOVIE")))
                .andExpect(jsonPath("$[0].contentId", is("MOV123")));
        
        verify(reviewService, times(1)).getReviewsByContent(ContentType.MOVIE, "MOV123");
    }

    @Test
    @DisplayName("GET /reviews/content/stats retorna estadísticas del contenido")
    void getContentStats_returnsStats() throws Exception {
        when(reviewService.getAverageRatingByContent(ContentType.MOVIE, "MOV123")).thenReturn(4.3);
        when(reviewService.getTotalReviewsByContent(ContentType.MOVIE, "MOV123")).thenReturn(5L);

        mockMvc.perform(get("/api/v1/reviews/content/stats")
                .param("contentType", "MOVIE")
                .param("contentId", "MOV123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentType", is("MOVIE")))
                .andExpect(jsonPath("$.contentId", is("MOV123")))
                .andExpect(jsonPath("$.averageRating", is(4.3)))
                .andExpect(jsonPath("$.totalReviews", is(5)));
        
        verify(reviewService, times(1)).getAverageRatingByContent(ContentType.MOVIE, "MOV123");
        verify(reviewService, times(1)).getTotalReviewsByContent(ContentType.MOVIE, "MOV123");
    }

    @Test
    @DisplayName("POST /reviews/{id}/like da like a una reseña")
    void likeReview_returnsOk() throws Exception {
        UserEntity mockUser = UserEntity.builder()
                .idUser(1L)
                .userName("usuario1")
                .build();
        
        when(userRepository.findByUserName("usuario1")).thenReturn(Optional.of(mockUser));
        doNothing().when(reviewService).likeReview(eq(1L), any(UserEntity.class));

        mockMvc.perform(post("/api/v1/reviews/1/like"))
                .andExpect(status().isOk());
        
        verify(userRepository, times(1)).findByUserName("usuario1");
        verify(reviewService, times(1)).likeReview(eq(1L), any(UserEntity.class));
    }

    @Test
    @DisplayName("DELETE /reviews/{id}/like quita el like de una reseña")
    void unlikeReview_returnsOk() throws Exception {
        UserEntity mockUser = UserEntity.builder()
                .idUser(1L)
                .userName("usuario1")
                .build();
        
        when(userRepository.findByUserName("usuario1")).thenReturn(Optional.of(mockUser));
        doNothing().when(reviewService).unlikeReview(eq(1L), any(UserEntity.class));

        mockMvc.perform(delete("/api/v1/reviews/1/like"))
                .andExpect(status().isOk());
        
        verify(userRepository, times(1)).findByUserName("usuario1");
        verify(reviewService, times(1)).unlikeReview(eq(1L), any(UserEntity.class));
    }

    @Test
    @DisplayName("GET /reviews responde con likeCount correcto para cada reseña")
    void getAllReviews_includesCorrectLikeCount() throws Exception {
        ReviewResponseDTO review1 = new ReviewResponseDTO(
                1L, 2L, "usuario1", null,
                ContentType.MOVIE, "MOV123", ApiSource.TMDB,
                "Título", "Texto", 4.0,
                LocalDateTime.now(), LocalDateTime.now(), 10, true);
        
        ReviewResponseDTO review2 = new ReviewResponseDTO(
                2L, 3L, "usuario2", null,
                ContentType.BOOK, "BOOK1", ApiSource.OPENLIBRARY,
                "Libro", "Excelente", 5.0,
                LocalDateTime.now(), LocalDateTime.now(), 0, false);
        
        when(reviewService.getEntities()).thenReturn(List.of(review1, review2));

        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].likeCount", is(10)))
                .andExpect(jsonPath("$[0].likedByCurrentUser", is(true)))
                .andExpect(jsonPath("$[1].likeCount", is(0)))
                .andExpect(jsonPath("$[1].likedByCurrentUser", is(false)));
    }
}
