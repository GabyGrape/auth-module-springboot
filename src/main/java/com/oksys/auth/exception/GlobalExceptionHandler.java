package com.oksys.auth.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// @RestControllerAdvice: Interceptor global untuk menangkap exception yang dilempar oleh Controller
@RestControllerAdvice
public class GlobalExceptionHandler {
    // @ExceptionHandler: Menentukan bahwa method ini khusus menangani ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        // Mengembalikan HTTP Status 404 NOT FOUND beserta body JSON error
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}
