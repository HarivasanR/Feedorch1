package com.feedorch1.feedorch1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catches validation failures triggered by the @Valid annotation on Controller endpoints.
     * Combines both HTTP status metadata and specific field errors into a unified response body.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponseBody = new HashMap<>();
        
        // 1. Embed explicit status indicators directly inside the JSON body payload
        errorResponseBody.put("status", HttpStatus.BAD_REQUEST.value()); // Outputs: 400
        errorResponseBody.put("error", "Bad Request");

        // 2. Extract specific field-level validation constraint violations
        Map<String, String> validationMessages = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationMessages.put(fieldName, errorMessage);
        });
        
        errorResponseBody.put("messages", validationMessages);
        
        // 3. Return the unified JSON map payload along with the actual HTTP 400 status header
        return new ResponseEntity<>(errorResponseBody, HttpStatus.BAD_REQUEST);
    }
}