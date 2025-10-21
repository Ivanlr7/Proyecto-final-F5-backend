package dev.ivan.reviewverso_back.reviews;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewRequestDTO;
import dev.ivan.reviewverso_back.reviews.dtos.ReviewResponseDTO;
import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.reviews.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {
    @Test
    @DisplayName("GET /reviews/user/{userId} responde con las reseñas del usuario")
    void getReviewsByUser_returnsList() throws Exception {
        ReviewResponseDTO r1 = new ReviewResponseDTO(1L, 2L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto", 4.0, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(reviewService.getReviewsByUserId(2L)).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/v1/reviews/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userName", is("usuario1")));
    }

    @Test
    @DisplayName("GET /reviews/content responde con las reseñas del contenido")
    void getReviewsByContent_returnsList() throws Exception {
        ReviewResponseDTO r1 = new ReviewResponseDTO(1L, 2L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto", 4.0, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(reviewService.getReviewsByContent(ContentType.MOVIE, "MOV123")).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/v1/reviews/content")
                .param("contentType", "MOVIE")
                .param("contentId", "MOV123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contentId", is("MOV123")));
    }

    @Test
    @DisplayName("GET /reviews/content/stats responde con stats del contenido")
    void getContentStats_returnsStats() throws Exception {
        Mockito.when(reviewService.getAverageRatingByContent(ContentType.MOVIE, "MOV123")).thenReturn(4.5);
        Mockito.when(reviewService.getTotalReviewsByContent(ContentType.MOVIE, "MOV123")).thenReturn(3L);

        mockMvc.perform(get("/api/v1/reviews/content/stats")
                .param("contentType", "MOVIE")
                .param("contentId", "MOV123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contentType", is("MOVIE")))
                .andExpect(jsonPath("$.contentId", is("MOV123")))
                .andExpect(jsonPath("$.averageRating", is(4.5)))
                .andExpect(jsonPath("$.totalReviews", is(3)));
    }

    @Test
    @DisplayName("PUT /reviews/{id} actualiza y responde con la reseña actualizada")
    void updateReview_returnsUpdated() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Nuevo título", "Texto actualizado", 4.5);
        ReviewResponseDTO response = new ReviewResponseDTO(1L, 2L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Nuevo título", "Texto actualizado", 4.5, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(reviewService.updateEntity(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReview", is(1)))
                .andExpect(jsonPath("$.reviewTitle", is("Nuevo título")))
                .andExpect(jsonPath("$.rating", is(4.5)));
    }

    @Test
    @DisplayName("DELETE /reviews/{id} elimina la reseña y responde 204")
    void deleteReview_returnsNoContent() throws Exception {
        Mockito.doNothing().when(reviewService).deleteEntity(1L);

        mockMvc.perform(delete("/api/v1/reviews/1"))
                .andExpect(status().isNoContent());
    }
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ReviewService reviewService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /reviews crea una reseña y responde con 201 y el body correcto")
    void createReview_returnsCreated() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto", 4.0);
        ReviewResponseDTO response = new ReviewResponseDTO(1L, 2L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto", 4.0, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(reviewService.createEntity(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReview", is(1)))
                .andExpect(jsonPath("$.userName", is("usuario1")))
                .andExpect(jsonPath("$.contentType", is("MOVIE")))
                .andExpect(jsonPath("$.contentId", is("MOV123")))
                .andExpect(jsonPath("$.reviewTitle", is("Titulo")))
                .andExpect(jsonPath("$.rating", is(4.0)));
    }

    @Test
    @DisplayName("GET /reviews responde con lista de reseñas")
    void getAllReviews_returnsList() throws Exception {
        ReviewResponseDTO r1 = new ReviewResponseDTO(1L, 2L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto", 4.0, LocalDateTime.now(), LocalDateTime.now());
        ReviewResponseDTO r2 = new ReviewResponseDTO(2L, 3L, "usuario2", null, ContentType.BOOK, "BOOK1", ApiSource.OPENLIBRARY, "Libro", "Muy bueno", 5.0, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(reviewService.getEntities()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName", is("usuario1")))
                .andExpect(jsonPath("$[1].contentType", is("BOOK")));
    }

    @Test
    @DisplayName("GET /reviews/{id} responde con la reseña esperada")
    void getReviewById_returnsReview() throws Exception {
        ReviewResponseDTO r = new ReviewResponseDTO(1L, 2L, "usuario1", null, ContentType.MOVIE, "MOV123", ApiSource.TMDB, "Titulo", "Texto", 4.0, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(reviewService.getByID(1L)).thenReturn(r);

        mockMvc.perform(get("/api/v1/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReview", is(1)))
                .andExpect(jsonPath("$.userName", is("usuario1")));
    }
}
