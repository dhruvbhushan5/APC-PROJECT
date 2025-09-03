package com.hotel.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response wrapper for all microservices
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;
    
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(data, "Operation completed successfully");
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return error(message, null, status);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, String errorCode, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }
}
