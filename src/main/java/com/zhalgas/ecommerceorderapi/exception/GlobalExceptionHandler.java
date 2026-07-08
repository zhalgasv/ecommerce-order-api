package com.zhalgas.ecommerceorderapi.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.time.LocalDateTime.now;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
          ApiError apiError = new ApiError(
                  HttpStatus.NOT_FOUND.value(),
                  exception.getMessage(),
                  request.getRequestURI(),
                  now()
          );
          return ResponseEntity.status(404).body(apiError);
    }
}



