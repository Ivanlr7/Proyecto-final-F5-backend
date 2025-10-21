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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {
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
