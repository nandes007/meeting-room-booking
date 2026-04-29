package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.ApiResponse;
import com.nandestech.meetingroom.dto.AuthResponseData;
import com.nandestech.meetingroom.dto.LoginRequest;
import com.nandestech.meetingroom.dto.RefreshRequest;
import com.nandestech.meetingroom.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseData>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponseData data = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed(e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseData>> refresh(@Valid @RequestBody RefreshRequest request) {
        try {
            AuthResponseData data = authService.refresh(request.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed(e.getMessage()));
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failed(e.getMessage()));
    }
}
