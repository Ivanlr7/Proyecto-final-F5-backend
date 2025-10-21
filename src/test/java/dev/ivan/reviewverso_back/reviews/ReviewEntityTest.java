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
}
