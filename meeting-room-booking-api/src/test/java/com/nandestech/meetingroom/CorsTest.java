package com.nandestech.meetingroom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.nandestech.meetingroom.config.WebConfig;
import com.nandestech.meetingroom.controller.AuthController;
import com.nandestech.meetingroom.filter.AuthFilter;
import com.nandestech.meetingroom.service.AuthService;
import com.nandestech.meetingroom.service.PasetoTokenService;
import com.nandestech.meetingroom.repository.UserRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({WebConfig.class, AuthFilter.class})
public class CorsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasetoTokenService pasetoTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void optionsRequest_ShouldReturnCorsHeaders() throws Exception {
        mockMvc.perform(options("/api/v1/auth/login")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS,PATCH"));
    }
}
