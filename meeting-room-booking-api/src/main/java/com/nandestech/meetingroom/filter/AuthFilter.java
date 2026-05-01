package com.nandestech.meetingroom.filter;

import tools.jackson.databind.ObjectMapper;
import com.nandestech.meetingroom.dto.ApiResponse;
import com.nandestech.meetingroom.service.PasetoTokenService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Autowired
    private PasetoTokenService pasetoTokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Skip auth for login and refresh endpoints
        if (path.startsWith("/api/v1/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedResponse(httpResponse);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String username = pasetoTokenService.verifyToken(token);
            httpRequest.setAttribute("X-Username", username);
            chain.doFilter(request, response);
        } catch (RuntimeException e) {
            sendUnauthorizedResponse(httpResponse);
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status("unauthorized")
                .message("Unauthorized")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
