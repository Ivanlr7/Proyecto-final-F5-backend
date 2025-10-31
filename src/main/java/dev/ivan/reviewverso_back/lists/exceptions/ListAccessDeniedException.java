package dev.ivan.reviewverso_back.lists.exceptions;

public class ListAccessDeniedException extends RuntimeException {
    public ListAccessDeniedException(String message) {
        super(message);
    }
}
