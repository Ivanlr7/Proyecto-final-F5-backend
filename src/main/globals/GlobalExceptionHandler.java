package dev.ivan.reviewverso_back.register.globals;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.ivan.reviewverso_back.register.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
   
    @ExceptionHandler(IllegalArgumentException.class)
    public GlobalExceptionResponseDTO handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

}
