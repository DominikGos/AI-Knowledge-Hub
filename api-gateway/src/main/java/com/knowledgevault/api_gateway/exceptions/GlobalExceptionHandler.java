package com.knowledgevault.api_gateway.exceptions;

import com.knowledgevault.api_gateway.dto.AuthErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<AuthErrorResponse> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new AuthErrorResponse(e.getMessage())
                );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new AuthErrorResponse(e.getMessage())
                );
    }
}
