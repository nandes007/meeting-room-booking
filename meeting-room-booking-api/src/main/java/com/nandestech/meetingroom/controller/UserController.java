package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.ApiResponse;
import com.nandestech.meetingroom.dto.UserResponse;
import com.nandestech.meetingroom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for user profile and management")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/current")
    @Operation(
            summary = "Get current logged-in user",
            description = "Fetch details of the user associated with the provided authentication token."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "id": 1,
                        "name": "test name",
                        "username": "tes username",
                        "email": "test@example.com",
                        "role": "SUPERADMIN",
                        "created_at": "2022-01-01T00:00:00Z",
                        "updated_at": "2022-01-01T00:00:00Z",
                        "bookings": []
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Failed to retrieve user",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "failed to get current user"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "unauthorized",
                    "message": "Unauthorized"
                }
                """)))
    })
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @Parameter(hidden = true) @RequestAttribute("X-Username") String username) {
        try {
            UserResponse data = userService.getCurrentUser(username);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed(e.getMessage()));
        }
    }
}
