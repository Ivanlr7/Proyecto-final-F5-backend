package dev.ivan.reviewverso_back.reviews.exceptions;

public class DuplicateReviewException extends RuntimeException {
    public DuplicateReviewException(String message) {
        super(message);
    }
}
