package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Invalid request body: " + e.getMessage());
        response.put("error", "INVALID_REQUEST_BODY");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        System.err.println("=== HTTP MESSAGE NOT READABLE ===");
        System.err.println("Error: " + e.getMessage());
        e.printStackTrace();
        System.err.println("==================================");
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Database error: " + e.getMessage());
        response.put("error", "DATABASE_ERROR");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        if (e.getRootCause() != null) {
            response.put("rootCause", e.getRootCause().getMessage());
        }
        
        System.err.println("=== DATABASE ACCESS EXCEPTION ===");
        System.err.println("Exception: " + e.getClass().getName());
        System.err.println("Message: " + e.getMessage());
        if (e.getRootCause() != null) {
            System.err.println("Root Cause: " + e.getRootCause().getMessage());
        }
        e.printStackTrace();
        System.err.println("=================================");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Error: " + e.getMessage());
        response.put("error", e.getClass().getSimpleName());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        // Log the error
        System.err.println("=== GLOBAL EXCEPTION HANDLER ===");
        System.err.println("Exception Type: " + e.getClass().getName());
        System.err.println("Exception Message: " + e.getMessage());
        if (e.getCause() != null) {
            System.err.println("Cause: " + e.getCause().getMessage());
        }
        e.printStackTrace();
        System.err.println("================================");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

