package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.AuthResponseData;
import com.nandestech.meetingroom.dto.LoginRequest;
import com.nandestech.meetingroom.dto.RefreshRequest;
import com.nandestech.meetingroom.service.AuthService;
import com.nandestech.meetingroom.service.PasetoTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasetoTokenService pasetoTokenService;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void login_Success() throws Exception {
        AuthResponseData responseData = AuthResponseData.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        when(authService.login("user", "password")).thenReturn(responseData);

        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.access_token").value("access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("refresh-token"));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Invalid username or password"));

        LoginRequest request = new LoginRequest();
        request.setUsername("wrong");
        request.setPassword("wrong");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_ValidationError() throws Exception {
        LoginRequest request = new LoginRequest();
        // missing username and password

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_UnexpectedError() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }

    @Test
    void refresh_Success() throws Exception {
        AuthResponseData responseData = AuthResponseData.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();

        when(authService.refresh("old-refresh-token")).thenReturn(responseData);

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("old-refresh-token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.access_token").value("new-access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("new-refresh-token"));
    }

    @Test
    void refresh_InvalidToken() throws Exception {
        when(authService.refresh(anyString()))
                .thenThrow(new RuntimeException("Invalid token"));

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("invalid-token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Invalid token"));
    }

    @Test
    void refresh_ValidationError() throws Exception {
        RefreshRequest request = new RefreshRequest();
        // missing refresh token

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_UnexpectedError() throws Exception {
        when(authService.refresh(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("old-refresh-token");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}
