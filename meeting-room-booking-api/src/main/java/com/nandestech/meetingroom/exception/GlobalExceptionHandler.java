package com.nandestech.meetingroom.exception;

import com.nandestech.meetingroom.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = errors.values().stream().collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .status("failed")
                        .message(message)
                        .data(errors)
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failed("Required request body is missing or invalid"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if ("Unauthorized".equalsIgnoreCase(message)) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (message != null && message.toLowerCase().contains("forbidden") || message != null && message.toLowerCase().contains("access denied")) {
            status = HttpStatus.FORBIDDEN;
        }

        return ResponseEntity.status(status)
                .body(ApiResponse.failed(message));
    }
}
