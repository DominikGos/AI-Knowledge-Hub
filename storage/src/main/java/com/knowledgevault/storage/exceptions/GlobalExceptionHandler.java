package com.knowledgevault.storage.exceptions;

import com.knowledgevault.storage.dto.StorageServiceErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public StorageServiceErrorResponse handleFileNotFound(FileNotFoundException e) {
        return new StorageServiceErrorResponse(e.getMessage(), Instant.now());
    }
}
