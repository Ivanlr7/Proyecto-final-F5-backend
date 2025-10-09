package dev.ivan.reviewverso_back.register.exceptions;

public class RegisterIllegalArgumentException extends RegisterException{
    
    public RegisterIllegalArgumentException(String message) {
        super(message);
    }

    public RegisterIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }


}
