package dev.ivan.reviewverso_back.lists;

import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ListEntityTest {

    @Test
    void builder_and_getters_should_work() {
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .userName("testuser")
                .build();

        ListEntity list = ListEntity.builder()
                .idList(10L)
                .user(user)
                .title("Mis Películas Favoritas")
                .description("Una colección de mis películas favoritas de todos los tiempos")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertThat(list.getIdList(), is(10L));
        assertThat(list.getUser(), is(user));
        assertThat(list.getTitle(), is("Mis Películas Favoritas"));
        assertThat(list.getDescription(), containsString("colección"));
        assertThat(list.getCreatedAt(), is(notNullValue()));
        assertThat(list.getUpdatedAt(), is(notNullValue()));
        assertThat(list.getItems(), is(notNullValue()));
    }

    @Test
    void addItem_should_add_item_and_set_position() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("user1").build();
        ListEntity list = ListEntity.builder()
                .idList(1L)
                .user(user)
                .title("Test List")
                .build();

        ListItemEntity item1 = ListItemEntity.builder()
                .idListItem(1L)
                .contentType(ContentType.MOVIE)
                .contentId("550")
                .apiSource(ApiSource.TMDB)
                .build();

        ListItemEntity item2 = ListItemEntity.builder()
                .idListItem(2L)
                .contentType(ContentType.SERIES)
                .contentId("1399")
                .apiSource(ApiSource.TMDB)
                .build();

        list.addItem(item1);
        list.addItem(item2);

        assertThat(list.getItems(), hasSize(2));
        assertThat(list.getItems().get(0), is(item1));
        assertThat(list.getItems().get(1), is(item2));
        assertThat(item1.getPosition(), is(0));
        assertThat(item2.getPosition(), is(1));
        assertThat(item1.getList(), is(list));
        assertThat(item2.getList(), is(list));
    }

    @Test
    void removeItem_should_remove_item_and_reorder_positions() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("user1").build();
        ListEntity list = ListEntity.builder()
                .idList(1L)
                .user(user)
                .title("Test List")
                .build();

        ListItemEntity item1 = ListItemEntity.builder()
                .idListItem(1L)
                .contentType(ContentType.MOVIE)
                .contentId("550")
                .apiSource(ApiSource.TMDB)
                .build();

        ListItemEntity item2 = ListItemEntity.builder()
                .idListItem(2L)
                .contentType(ContentType.SERIES)
                .contentId("1399")
                .apiSource(ApiSource.TMDB)
                .build();

        ListItemEntity item3 = ListItemEntity.builder()
                .idListItem(3L)
                .contentType(ContentType.GAME)
                .contentId("123")
                .apiSource(ApiSource.IGDB)
                .build();

        list.addItem(item1);
        list.addItem(item2);
        list.addItem(item3);

        // Remove middle item
        list.removeItem(item2);

        assertThat(list.getItems(), hasSize(2));
        assertThat(list.getItems(), contains(item1, item3));
        assertThat(item1.getPosition(), is(0));
        assertThat(item3.getPosition(), is(1));
        assertThat(item2.getList(), is(nullValue()));
    }

    @Test
    void items_should_be_empty_by_default() {
        ListEntity list = ListEntity.builder()
                .idList(1L)
                .title("Empty List")
                .build();

        assertThat(list.getItems(), is(notNullValue()));
        assertThat(list.getItems(), is(empty()));
    }

    @Test
    void cascade_all_should_propagate_to_items() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("user1").build();
        ListEntity list = ListEntity.builder()
                .user(user)
                .title("Test List")
                .build();

        ListItemEntity item = ListItemEntity.builder()
                .contentType(ContentType.MOVIE)
                .contentId("550")
                .apiSource(ApiSource.TMDB)
                .build();

        list.addItem(item);

        assertThat(item.getList(), is(list));
        assertThat(list.getItems(), contains(item));
    }
}
