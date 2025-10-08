package dev.ivan.reviewverso_back.register.exceptions;

public class RegisterIllegalArgument extends RegisterException{
    
    public RegisterIllegalArgument(String message) {
        super(message);
    }

    public RegisterIllegalArgument(String message, Throwable cause) {
        super(message, cause);
    }


}
