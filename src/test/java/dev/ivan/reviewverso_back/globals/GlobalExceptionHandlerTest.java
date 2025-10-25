package dev.ivan.reviewverso_back.globals;

import dev.ivan.reviewverso_back.register.exceptions.RegisterIllegalArgumentException;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import dev.ivan.reviewverso_back.user.exceptions.UserAccessDeniedException;
import dev.ivan.reviewverso_back.user.exceptions.UserIllegalArgumentException;
import dev.ivan.reviewverso_back.reviews.exceptions.ReviewNotFoundException;
import dev.ivan.reviewverso_back.reviews.exceptions.DuplicateReviewException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("RegisterIllegalArgumentException retorna 400")
    void handleRegisterIllegalArgument() {
        ResponseEntity<GlobalExceptionResponseDTO> response = handler.handleRegisResponseEntity(new RegisterIllegalArgumentException("msg"));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().message(), is("msg"));
    }

    @Test
    @DisplayName("UserNotFoundException retorna 404")
    void handleUserNotFound() {
        ResponseEntity<GlobalExceptionResponseDTO> response = handler.handleUserNotFound(new UserNotFoundException("no user"));
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody().message(), is("no user"));
    }

    @Test
    @DisplayName("UserAccessDeniedException retorna 403")
    void handleUserAccessDenied() {
        ResponseEntity<GlobalExceptionResponseDTO> response = handler.handleUserAccessDenied(new UserAccessDeniedException("denied"));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        assertThat(response.getBody().message(), is("denied"));
    }

    @Test
    @DisplayName("UserIllegalArgumentException retorna 400")
    void handleUserIllegalArgument() {
        ResponseEntity<GlobalExceptionResponseDTO> response = handler.handleUserIllegalArgument(new UserIllegalArgumentException("bad arg"));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().message(), is("bad arg"));
    }

    @Test
    @DisplayName("ReviewNotFoundException retorna 404")
    void handleReviewNotFound() {
        ResponseEntity<GlobalExceptionResponseDTO> response = handler.handleReviewNotFound(new ReviewNotFoundException("no review"));
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody().message(), is("no review"));
    }

    @Test
    @DisplayName("DuplicateReviewException retorna 409")
    void handleDuplicateReview() {
        ResponseEntity<GlobalExceptionResponseDTO> response = handler.handleDuplicateReview(new DuplicateReviewException("dup"));
        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
        assertThat(response.getBody().message(), is("dup"));
    }

    @Test
    @DisplayName("IllegalArgumentException retorna 400")
    void handleIllegalArgument() {
        ResponseEntity<GlobalExceptionResponseDTO> response = handler.handleIllegalArgument(new IllegalArgumentException("illegal"));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody().message(), is("illegal"));
    }
}
