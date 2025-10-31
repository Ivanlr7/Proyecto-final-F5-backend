package dev.ivan.reviewverso_back.lists.dtos;

import dev.ivan.reviewverso_back.lists.ListEntity;
import dev.ivan.reviewverso_back.lists.ListItemEntity;
import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListMapperTest {

    private ListMapper listMapper;

    @BeforeEach
    void setUp() {
        listMapper = new ListMapper();
    }

    @Test
    void testListRequestDtoToListEntity_withItems() {
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .userName("testuser")
                .build();

        ListItemDTO item1 = new ListItemDTO(ContentType.MOVIE, "550", ApiSource.TMDB);
        ListItemDTO item2 = new ListItemDTO(ContentType.SERIES, "1399", ApiSource.TMDB);

        ListRequestDTO dto = new ListRequestDTO(
                "Mis Favoritas",
                "Lista de contenido favorito",
                List.of(item1, item2)
        );

        ListEntity entity = listMapper.listRequestDtoToListEntity(dto, user);

        assertEquals(user, entity.getUser());
        assertEquals("Mis Favoritas", entity.getTitle());
        assertEquals("Lista de contenido favorito", entity.getDescription());
        assertEquals(2, entity.getItems().size());

        ListItemEntity entityItem1 = entity.getItems().get(0);
        assertEquals(ContentType.MOVIE, entityItem1.getContentType());
        assertEquals("550", entityItem1.getContentId());
        assertEquals(ApiSource.TMDB, entityItem1.getApiSource());
        assertEquals(0, entityItem1.getPosition());
        assertEquals(entity, entityItem1.getList());

        ListItemEntity entityItem2 = entity.getItems().get(1);
        assertEquals(ContentType.SERIES, entityItem2.getContentType());
        assertEquals("1399", entityItem2.getContentId());
        assertEquals(ApiSource.TMDB, entityItem2.getApiSource());
        assertEquals(1, entityItem2.getPosition());
        assertEquals(entity, entityItem2.getList());
    }

    @Test
    void testListRequestDtoToListEntity_withoutItems() {
        UserEntity user = UserEntity.builder()
                .idUser(2L)
                .userName("user2")
                .build();

        ListRequestDTO dto = new ListRequestDTO(
                "Lista Vacía",
                "Sin items todavía",
                null
        );

        ListEntity entity = listMapper.listRequestDtoToListEntity(dto, user);

        assertEquals(user, entity.getUser());
        assertEquals("Lista Vacía", entity.getTitle());
        assertEquals("Sin items todavía", entity.getDescription());
        assertNotNull(entity.getItems());
        assertTrue(entity.getItems().isEmpty());
    }

    @Test
    void testListRequestDtoToListEntity_withEmptyItems() {
        UserEntity user = UserEntity.builder()
                .idUser(3L)
                .userName("user3")
                .build();

        ListRequestDTO dto = new ListRequestDTO(
                "Lista Con Items Vacíos",
                "Con lista vacía de items",
                List.of()
        );

        ListEntity entity = listMapper.listRequestDtoToListEntity(dto, user);

        assertEquals(user, entity.getUser());
        assertEquals("Lista Con Items Vacíos", entity.getTitle());
        assertNotNull(entity.getItems());
        assertTrue(entity.getItems().isEmpty());
    }

    @Test
    void testListEntityToListResponseDto() {
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .userName("testuser")
                .build();

        ListEntity list = ListEntity.builder()
                .idList(10L)
                .user(user)
                .title("Mi Lista")
                .description("Descripción de la lista")
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 2, 15, 30))
                .build();

        ListItemEntity item1 = ListItemEntity.builder()
                .idListItem(1L)
                .list(list)
                .contentType(ContentType.MOVIE)
                .contentId("550")
                .apiSource(ApiSource.TMDB)
                .position(0)
                .build();

        ListItemEntity item2 = ListItemEntity.builder()
                .idListItem(2L)
                .list(list)
                .contentType(ContentType.BOOK)
                .contentId("abc123")
                .apiSource(ApiSource.OPENLIBRARY)
                .position(1)
                .build();

        list.getItems().add(item1);
        list.getItems().add(item2);

        ListResponseDTO dto = listMapper.listEntityToListResponseDto(list);

        assertEquals(10L, dto.idList());
        assertEquals(1L, dto.userId());
        assertEquals("testuser", dto.userName());
        assertEquals("Mi Lista", dto.title());
        assertEquals("Descripción de la lista", dto.description());
        assertEquals(2, dto.items().size());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), dto.createdAt());
        assertEquals(LocalDateTime.of(2024, 1, 2, 15, 30), dto.updatedAt());

        ListItemResponseDTO itemDto1 = dto.items().get(0);
        assertEquals(1L, itemDto1.idListItem());
        assertEquals(ContentType.MOVIE, itemDto1.contentType());
        assertEquals("550", itemDto1.contentId());
        assertEquals(ApiSource.TMDB, itemDto1.apiSource());
        assertEquals(0, itemDto1.position());

        ListItemResponseDTO itemDto2 = dto.items().get(1);
        assertEquals(2L, itemDto2.idListItem());
        assertEquals(ContentType.BOOK, itemDto2.contentType());
        assertEquals("abc123", itemDto2.contentId());
        assertEquals(ApiSource.OPENLIBRARY, itemDto2.apiSource());
        assertEquals(1, itemDto2.position());
    }

    @Test
    void testListEntityToListResponseDto_withEmptyItems() {
        UserEntity user = UserEntity.builder()
                .idUser(5L)
                .userName("user5")
                .build();

        ListEntity list = ListEntity.builder()
                .idList(20L)
                .user(user)
                .title("Lista Sin Items")
                .description("Vacía")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ListResponseDTO dto = listMapper.listEntityToListResponseDto(list);

        assertEquals(20L, dto.idList());
        assertEquals(5L, dto.userId());
        assertEquals("user5", dto.userName());
        assertEquals("Lista Sin Items", dto.title());
        assertNotNull(dto.items());
        assertTrue(dto.items().isEmpty());
    }
}
