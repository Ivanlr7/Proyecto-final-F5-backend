package dev.ivan.reviewverso_back.globals;

import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.ivan.reviewverso_back.register.exceptions.*;
import dev.ivan.reviewverso_back.user.exceptions.*;


@RestControllerAdvice
public class GlobalExceptionHandler {
   
  @ExceptionHandler(RegisterIllegalArgumentException.class)
  public ResponseEntity<GlobalExceptionResponseDTO> handleRegisResponseEntity(RegisterIllegalArgumentException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<GlobalExceptionResponseDTO> handleUserNotFound(UserNotFoundException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

      @ExceptionHandler(UserAccessDeniedException.class)
public ResponseEntity<GlobalExceptionResponseDTO> handleUserAccessDenied(UserAccessDeniedException ex) {
    return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
}

  @ExceptionHandler(UserIllegalArgumentException.class)
  public ResponseEntity<GlobalExceptionResponseDTO> handleUserIllegalArgument(UserIllegalArgumentException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

      private ResponseEntity<GlobalExceptionResponseDTO> buildResponse(HttpStatus status, String message) {
        GlobalExceptionResponseDTO error = new GlobalExceptionResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }

    
}
