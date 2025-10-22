package dev.ivan.reviewverso_back.reviews;

import dev.ivan.reviewverso_back.reviews.enums.ApiSource;
import dev.ivan.reviewverso_back.reviews.enums.ContentType;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ReviewEntityTest {
    @Test
    void builder_and_getters_should_work() {
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .userName("testuser")
                .build();

        ReviewEntity review = ReviewEntity.builder()
                .idReview(10L)
                .user(user)
                .contentType(ContentType.MOVIE)
                .contentId("12345")
                .apiSource(ApiSource.TMDB)
                .reviewTitle("Gran película")
                .reviewText("Me encantó la trama y los personajes.")
                .rating(4.5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertThat(review.getIdReview(), is(10L));
        assertThat(review.getUser(), is(user));
        assertThat(review.getContentType(), is(ContentType.MOVIE));
        assertThat(review.getContentId(), is("12345"));
        assertThat(review.getApiSource(), is(ApiSource.TMDB));
        assertThat(review.getReviewTitle(), is("Gran película"));
        assertThat(review.getReviewText(), containsString("trama"));
        assertThat(review.getRating(), closeTo(4.5, 0.01));
        assertThat(review.getCreatedAt(), is(notNullValue()));
        assertThat(review.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    void likedByUsers_should_add_and_remove_users_correctly() {
        UserEntity user1 = UserEntity.builder().idUser(1L).userName("user1").build();
        UserEntity user2 = UserEntity.builder().idUser(2L).userName("user2").build();
        ReviewEntity review = ReviewEntity.builder().idReview(20L).build();

        // Initially empty
        assertThat(review.getLikedByUsers(), is(empty()));

        // Add user1
        review.getLikedByUsers().add(user1);
        assertThat(review.getLikedByUsers(), contains(user1));

        // Add user2
        review.getLikedByUsers().add(user2);
        assertThat(review.getLikedByUsers(), containsInAnyOrder(user1, user2));

        // Remove user1
        review.getLikedByUsers().remove(user1);
        assertThat(review.getLikedByUsers(), contains(user2));
    }

    @Test
    void likeCount_should_reflect_likedByUsers_size() {
        UserEntity user1 = UserEntity.builder().idUser(1L).userName("user1").build();
        UserEntity user2 = UserEntity.builder().idUser(2L).userName("user2").build();
        ReviewEntity review = ReviewEntity.builder().idReview(30L).build();

        assertThat(review.getLikedByUsers().size(), is(0));
        review.getLikedByUsers().add(user1);
        assertThat(review.getLikedByUsers().size(), is(1));
        review.getLikedByUsers().add(user2);
        assertThat(review.getLikedByUsers().size(), is(2));
        review.getLikedByUsers().remove(user1);
        assertThat(review.getLikedByUsers().size(), is(1));
    }

    @Test
    void likedByUsers_should_not_duplicate_users_with_same_id() {
        UserEntity user1a = UserEntity.builder().idUser(1L).userName("user1").build();
        UserEntity user1b = UserEntity.builder().idUser(1L).userName("user1").build();
        ReviewEntity review = ReviewEntity.builder().idReview(40L).build();

        review.getLikedByUsers().add(user1a);
        review.getLikedByUsers().add(user1b); // Should not duplicate
        assertThat(review.getLikedByUsers().size(), is(1));
        assertThat(review.getLikedByUsers(), contains(user1a));
    }
}
