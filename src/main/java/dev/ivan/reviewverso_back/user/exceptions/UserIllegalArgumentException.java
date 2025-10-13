package dev.ivan.reviewverso_back.user.exceptions;

public class UserIllegalArgumentException extends UserException {
    public UserIllegalArgumentException(String message) {
        super(message);
    }
    public UserIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
