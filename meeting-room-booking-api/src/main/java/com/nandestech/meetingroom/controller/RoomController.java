package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.ApiResponse;
import com.nandestech.meetingroom.dto.RoomRequest;
import com.nandestech.meetingroom.dto.RoomResponse;
import com.nandestech.meetingroom.service.RoomService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Room Management", description = "CRUD operations for meeting rooms (Admin only for create/update/delete)")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    @Operation(
            summary = "Create a new room",
            description = "Create a new meeting room. Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room created successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "name": "Meeting Room A",
                        "capacity": 10,
                        "location": "Floor 3, Building A",
                        "is_available": true,
                        "created_at": "2026-05-04T10:00:00",
                        "updated_at": "2026-05-04T10:00:00"
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Validation failed",
                    "data": {
                        "name": "Room name is required",
                        "capacity": "Room capacity is required"
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "unauthorized",
                    "message": "Unauthorized"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin only",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Access denied: Only administrators can perform this action"
                }
                """)))
    })
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody RoomRequest request,
            @Parameter(hidden = true) @RequestAttribute("X-Role") String role) {
        RoomResponse data = roomService.createRoom(request, role);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    @Operation(
            summary = "Get all rooms",
            description = "Retrieve a list of all meeting rooms. Any authenticated user can access this."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rooms retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "content": [
                            {
                                "name": "Meeting Room A",
                                "capacity": 10,
                                "location": "Floor 3, Building A",
                                "is_available": true,
                                "created_at": "2026-05-04T10:00:00",
                                "updated_at": "2026-05-04T10:00:00"
                            }
                        ],
                        "page": {
                            "size": 10,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
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
    public ResponseEntity<ApiResponse<Page<RoomResponse>>> getAllRooms(
            @Parameter(description = "Page number (1-indexed)", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Items per page", example = "10") @RequestParam(defaultValue = "10") int limit) {
        Page<RoomResponse> data = roomService.getAllRooms(page, limit);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get room by ID",
            description = "Retrieve a specific meeting room by its ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "name": "Meeting Room A",
                        "capacity": 10,
                        "location": "Floor 3, Building A",
                        "is_available": true,
                        "created_at": "2026-05-04T10:00:00",
                        "updated_at": "2026-05-04T10:00:00"
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Room not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Room not found"
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
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        RoomResponse data = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a room",
            description = "Update an existing meeting room. Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room updated successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "name": "Updated Room Name",
                        "capacity": 20,
                        "location": "Floor 5, Building B",
                        "is_available": false,
                        "created_at": "2026-05-04T10:00:00",
                        "updated_at": "2026-05-04T15:30:00"
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or room not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Room not found"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
    })
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request,
            @Parameter(hidden = true) @RequestAttribute("X-Role") String role) {
        RoomResponse data = roomService.updateRoom(id, request, role);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a room",
            description = "Delete a meeting room by ID. Requires ADMIN role."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Room deleted successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "message": "room deleted successfully"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Room not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Room not found"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("X-Role") String role) {
        roomService.deleteRoom(id, role);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .message("room deleted successfully")
                .build());
    }
}
