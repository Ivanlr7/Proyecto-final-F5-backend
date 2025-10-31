package dev.ivan.reviewverso_back.lists;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivan.reviewverso_back.lists.dtos.*;
import dev.ivan.reviewverso_back.lists.service.ListService;
import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "testuser", roles = {"USER"})
class ListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListService listService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /lists crea una lista y responde con 201")
    void createList_returnsCreated() throws Exception {
        ListItemDTO itemDTO = new ListItemDTO(ContentType.MOVIE, "550", ApiSource.TMDB);
        ListRequestDTO request = new ListRequestDTO(
                "Mi Lista de Películas",
                "Mis películas favoritas de todos los tiempos",
                List.of(itemDTO)
        );

        ListItemResponseDTO itemResponseDTO = new ListItemResponseDTO(
                1L, ContentType.MOVIE, "550", ApiSource.TMDB, 0);
        ListResponseDTO response = new ListResponseDTO(
                1L, 1L, "testuser",
                "Mi Lista de Películas",
                "Mis películas favoritas de todos los tiempos",
                List.of(itemResponseDTO),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listService.createEntity(any(ListRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idList", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.userName", is("testuser")))
                .andExpect(jsonPath("$.title", is("Mi Lista de Películas")))
                .andExpect(jsonPath("$.description", is("Mis películas favoritas de todos los tiempos")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].contentType", is("MOVIE")))
                .andExpect(jsonPath("$.items[0].contentId", is("550")));

        verify(listService, times(1)).createEntity(any(ListRequestDTO.class));
    }

    @Test
    @DisplayName("GET /lists retorna lista de todas las listas")
    void getAllLists_returnsList() throws Exception {
        ListItemResponseDTO item1 = new ListItemResponseDTO(
                1L, ContentType.MOVIE, "550", ApiSource.TMDB, 0);
        ListItemResponseDTO item2 = new ListItemResponseDTO(
                2L, ContentType.SERIES, "1399", ApiSource.TMDB, 1);

        ListResponseDTO list1 = new ListResponseDTO(
                1L, 1L, "testuser",
                "Películas Acción",
                "Mis películas de acción favoritas",
                List.of(item1),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ListResponseDTO list2 = new ListResponseDTO(
                2L, 1L, "testuser",
                "Series para ver",
                "Series pendientes",
                List.of(item2),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listService.getEntities()).thenReturn(List.of(list1, list2));

        mockMvc.perform(get("/api/v1/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idList", is(1)))
                .andExpect(jsonPath("$[0].title", is("Películas Acción")))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[1].idList", is(2)))
                .andExpect(jsonPath("$[1].title", is("Series para ver")));

        verify(listService, times(1)).getEntities();
    }

    @Test
    @DisplayName("GET /lists/{id} retorna una lista específica")
    void getListById_returnsList() throws Exception {
        ListItemResponseDTO item = new ListItemResponseDTO(
                1L, ContentType.GAME, "1234", ApiSource.IGDB, 0);

        ListResponseDTO response = new ListResponseDTO(
                1L, 1L, "testuser",
                "Juegos Indie",
                "Mis juegos indie favoritos",
                List.of(item),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listService.getByID(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/lists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idList", is(1)))
                .andExpect(jsonPath("$.title", is("Juegos Indie")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].contentType", is("GAME")));

        verify(listService, times(1)).getByID(1L);
    }

    @Test
    @DisplayName("GET /lists/user/{userId} retorna listas del usuario")
    void getListsByUser_returnsList() throws Exception {
        ListResponseDTO list1 = new ListResponseDTO(
                1L, 2L, "otheruser",
                "Mi Lista 1",
                "Descripción 1",
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ListResponseDTO list2 = new ListResponseDTO(
                2L, 2L, "otheruser",
                "Mi Lista 2",
                "Descripción 2",
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listService.getListsByUserId(2L)).thenReturn(List.of(list1, list2));

        mockMvc.perform(get("/api/v1/lists/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userId", is(2)))
                .andExpect(jsonPath("$[0].userName", is("otheruser")))
                .andExpect(jsonPath("$[1].userId", is(2)));

        verify(listService, times(1)).getListsByUserId(2L);
    }

    @Test
    @DisplayName("PUT /lists/{id} actualiza una lista")
    void updateList_returnsUpdated() throws Exception {
        ListItemDTO newItem = new ListItemDTO(ContentType.BOOK, "abc123", ApiSource.OPENLIBRARY);
        ListRequestDTO request = new ListRequestDTO(
                "Lista Actualizada",
                "Descripción actualizada",
                List.of(newItem)
        );

        ListItemResponseDTO itemResponse = new ListItemResponseDTO(
                1L, ContentType.BOOK, "abc123", ApiSource.OPENLIBRARY, 0);
        ListResponseDTO response = new ListResponseDTO(
                1L, 1L, "testuser",
                "Lista Actualizada",
                "Descripción actualizada",
                List.of(itemResponse),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listService.updateEntity(eq(1L), any(ListRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/lists/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idList", is(1)))
                .andExpect(jsonPath("$.title", is("Lista Actualizada")))
                .andExpect(jsonPath("$.description", is("Descripción actualizada")))
                .andExpect(jsonPath("$.items[0].contentType", is("BOOK")));

        verify(listService, times(1)).updateEntity(eq(1L), any(ListRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /lists/{id} elimina una lista")
    void deleteList_returnsNoContent() throws Exception {
        doNothing().when(listService).deleteEntity(1L);

        mockMvc.perform(delete("/api/v1/lists/1"))
                .andExpect(status().isNoContent());

        verify(listService, times(1)).deleteEntity(1L);
    }

    @Test
    @DisplayName("POST /lists crea una lista vacía sin items")
    void createEmptyList_returnsCreated() throws Exception {
        ListRequestDTO request = new ListRequestDTO(
                "Lista Vacía",
                "Sin items todavía",
                null
        );

        ListResponseDTO response = new ListResponseDTO(
                1L, 1L, "testuser",
                "Lista Vacía",
                "Sin items todavía",
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listService.createEntity(any(ListRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idList", is(1)))
                .andExpect(jsonPath("$.title", is("Lista Vacía")))
                .andExpect(jsonPath("$.items", hasSize(0)));

        verify(listService, times(1)).createEntity(any(ListRequestDTO.class));
    }

    @Test
    @DisplayName("POST /lists crea una lista con múltiples items de diferentes tipos")
    void createMixedContentList_returnsCreated() throws Exception {
        ListItemDTO item1 = new ListItemDTO(ContentType.MOVIE, "550", ApiSource.TMDB);
        ListItemDTO item2 = new ListItemDTO(ContentType.SERIES, "1399", ApiSource.TMDB);
        ListItemDTO item3 = new ListItemDTO(ContentType.GAME, "1234", ApiSource.IGDB);
        ListItemDTO item4 = new ListItemDTO(ContentType.BOOK, "abc", ApiSource.OPENLIBRARY);

        ListRequestDTO request = new ListRequestDTO(
                "Contenido Mixto",
                "Una lista con todo tipo de contenido",
                List.of(item1, item2, item3, item4)
        );

        ListItemResponseDTO respItem1 = new ListItemResponseDTO(1L, ContentType.MOVIE, "550", ApiSource.TMDB, 0);
        ListItemResponseDTO respItem2 = new ListItemResponseDTO(2L, ContentType.SERIES, "1399", ApiSource.TMDB, 1);
        ListItemResponseDTO respItem3 = new ListItemResponseDTO(3L, ContentType.GAME, "1234", ApiSource.IGDB, 2);
        ListItemResponseDTO respItem4 = new ListItemResponseDTO(4L, ContentType.BOOK, "abc", ApiSource.OPENLIBRARY, 3);

        ListResponseDTO response = new ListResponseDTO(
                1L, 1L, "testuser",
                "Contenido Mixto",
                "Una lista con todo tipo de contenido",
                List.of(respItem1, respItem2, respItem3, respItem4),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(listService.createEntity(any(ListRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(4)))
                .andExpect(jsonPath("$.items[0].contentType", is("MOVIE")))
                .andExpect(jsonPath("$.items[1].contentType", is("SERIES")))
                .andExpect(jsonPath("$.items[2].contentType", is("GAME")))
                .andExpect(jsonPath("$.items[3].contentType", is("BOOK")))
                .andExpect(jsonPath("$.items[0].position", is(0)))
                .andExpect(jsonPath("$.items[3].position", is(3)));

        verify(listService, times(1)).createEntity(any(ListRequestDTO.class));
    }
}
