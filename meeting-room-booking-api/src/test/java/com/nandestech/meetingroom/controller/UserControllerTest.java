package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.UserResponse;
import com.nandestech.meetingroom.entity.User;
import com.nandestech.meetingroom.repository.UserRepository;
import com.nandestech.meetingroom.service.PasetoTokenService;
import com.nandestech.meetingroom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasetoTokenService pasetoTokenService;

    @MockitoBean
    private UserRepository userRepository;

    private static final String TOKEN = "valid-token";
    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        when(pasetoTokenService.verifyToken(TOKEN)).thenReturn(USERNAME);
        
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setRole("ADMIN");
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    }

    @Test
    void getCurrentUser_Success() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .name("test name")
                .username(USERNAME)
                .email("test@example.com")
                .role("SUPERADMIN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.getCurrentUser(eq(USERNAME))).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/current")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.username").value(USERNAME))
                .andExpect(jsonPath("$.data.role").value("SUPERADMIN"));
    }

    @Test
    void getCurrentUser_Failed() throws Exception {
        when(userService.getCurrentUser(eq(USERNAME)))
                .thenThrow(new RuntimeException("failed to get current user"));

        mockMvc.perform(get("/api/v1/users/current")
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("failed to get current user"));
    }

    @Test
    void getCurrentUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/current"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("unauthorized"))
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }
}
