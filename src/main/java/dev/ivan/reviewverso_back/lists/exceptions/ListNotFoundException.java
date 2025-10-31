package dev.ivan.reviewverso_back.lists.exceptions;

public class ListNotFoundException extends RuntimeException {
    public ListNotFoundException(String message) {
        super(message);
    }
}
