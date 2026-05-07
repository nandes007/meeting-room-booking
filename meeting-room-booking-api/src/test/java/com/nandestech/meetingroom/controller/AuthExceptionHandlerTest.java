package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.service.AuthService;
import com.nandestech.meetingroom.service.PasetoTokenService;
import com.nandestech.meetingroom.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(AuthController.class)
public class AuthExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasetoTokenService pasetoTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void login_MissingBody_ReturnsFriendlyMessage() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Required request body is missing or invalid"));
    }

    @Test
    void login_InvalidFields_ReturnsValidationMessages() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message", containsString("Username is required")))
                .andExpect(jsonPath("$.message", containsString("Password is required")));
    }
}
