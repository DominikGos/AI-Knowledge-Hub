package com.knowledgevault.storage.exceptions;

import com.knowledgevault.storage.dto.StorageServiceErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public StorageServiceErrorResponse handleFileNotFound(FileNotFoundException e) {
        return new StorageServiceErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(FileValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public StorageServiceErrorResponse handleFileValidation(FileValidationException e) {
        return new StorageServiceErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public StorageServiceErrorResponse handleStorageException(StorageException e) {
        return new StorageServiceErrorResponse(e.getMessage(), Instant.now());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StorageServiceErrorResponse handleUnexpectedException(Exception e) {
        return new StorageServiceErrorResponse(e.getMessage(), Instant.now());
    }
}
