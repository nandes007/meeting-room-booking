package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.ApiResponse;
import com.nandestech.meetingroom.dto.BookingRequest;
import com.nandestech.meetingroom.dto.BookingResponse;
import com.nandestech.meetingroom.service.BookingService;
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
@RequestMapping("/api/v1/bookings")
@Tag(name = "Booking Management", description = "CRUD operations for room bookings. Admin can manage all; users can create and cancel their own.")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    @Operation(
            summary = "Create a new booking",
            description = "Book a meeting room. The user is automatically identified from the authentication token. Validates: room availability, time overlap, past dates, and end time must be after start time."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking created successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "id": 1,
                        "user_id": 1,
                        "room_id": 1,
                        "start_time": "2026-05-10 09:00:00",
                        "end_time": "2026-05-10 10:00:00",
                        "status": "pending",
                        "description": "Sprint Planning Meeting",
                        "created_at": "2026-05-04 21:00:00",
                        "updated_at": "2026-05-04 21:00:00"
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error, overlap, or past date",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "The room is already booked for the selected time slot"
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
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @Parameter(hidden = true) @RequestAttribute("X-Username") String username) {
        BookingResponse data = bookingService.createBooking(request, username);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    @Operation(
            summary = "Get all bookings",
            description = "Admin sees all bookings. Normal users see only their own bookings."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookings retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": [
                        {
                            "id": 1,
                            "user_id": 1,
                            "room_id": 1,
                            "start_time": "2026-05-10 09:00:00",
                            "end_time": "2026-05-10 10:00:00",
                            "status": "pending",
                            "description": "Sprint Planning Meeting",
                            "created_at": "2026-05-04 21:00:00",
                            "updated_at": "2026-05-04 21:00:00"
                        }
                    ]
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Page<BookingResponse>>> getAllBookings(
            @Parameter(hidden = true) @RequestAttribute("X-Username") String username,
            @Parameter(hidden = true) @RequestAttribute("X-Role") String role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<BookingResponse> data = bookingService.getAllBookings(role, username, page, limit);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get booking by ID",
            description = "Retrieve a specific booking. Admin can view any booking. Normal users can only view their own."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "id": 1,
                        "user_id": 1,
                        "room_id": 1,
                        "start_time": "2026-05-10 09:00:00",
                        "end_time": "2026-05-10 10:00:00",
                        "status": "pending",
                        "description": "Sprint Planning Meeting",
                        "created_at": "2026-05-04 21:00:00",
                        "updated_at": "2026-05-04 21:00:00"
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Booking not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Booking not found"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - not your booking",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Access denied: You can only view your own bookings"
                }
                """)))
    })
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("X-Username") String username,
            @Parameter(hidden = true) @RequestAttribute("X-Role") String role) {
        BookingResponse data = bookingService.getBookingById(id, role, username);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a booking",
            description = "Update an existing booking. Only ADMIN role is allowed to edit bookings."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking updated successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "data": {
                        "id": 1,
                        "user_id": 1,
                        "room_id": 2,
                        "start_time": "2026-05-10 14:00:00",
                        "end_time": "2026-05-10 15:00:00",
                        "status": "confirmed",
                        "description": "Updated: Design Review",
                        "created_at": "2026-05-04 21:00:00",
                        "updated_at": "2026-05-04 22:00:00"
                    }
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or overlap",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "End time must be after start time"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Admin only",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Access denied: Only administrators can edit bookings"
                }
                """)))
    })
    public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequest request,
            @Parameter(hidden = true) @RequestAttribute("X-Username") String username,
            @Parameter(hidden = true) @RequestAttribute("X-Role") String role) {
        BookingResponse data = bookingService.updateBooking(id, request, username, role);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancel a booking",
            description = "Cancel (delete) a booking. Admin can cancel any booking. Normal users can only cancel their own."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Booking cancelled successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "success",
                    "message": "Booking cancelled successfully"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Booking not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Booking not found"
                }
                """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - not your booking",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {
                    "status": "failed",
                    "message": "Access denied: You can only cancel your own bookings"
                }
                """)))
    })
    public ResponseEntity<ApiResponse<Void>> deleteBooking(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute("X-Username") String username,
            @Parameter(hidden = true) @RequestAttribute("X-Role") String role) {
        bookingService.deleteBooking(id, username, role);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .message("Booking cancelled successfully")
                .build());
    }
}
