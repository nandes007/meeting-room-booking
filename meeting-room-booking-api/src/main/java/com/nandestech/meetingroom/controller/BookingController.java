package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.ApiResponse;
import com.nandestech.meetingroom.dto.BookingRequest;
import com.nandestech.meetingroom.dto.BookingResponse;
import com.nandestech.meetingroom.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestAttribute("X-Username") String username) {
        BookingResponse data = bookingService.createBooking(request, username);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings(
            @RequestAttribute("X-Username") String username,
            @RequestAttribute("X-Role") String role) {
        List<BookingResponse> data = bookingService.getAllBookings(role, username);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long id,
            @RequestAttribute("X-Username") String username,
            @RequestAttribute("X-Role") String role) {
        BookingResponse data = bookingService.getBookingById(id, role, username);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequest request,
            @RequestAttribute("X-Username") String username,
            @RequestAttribute("X-Role") String role) {
        BookingResponse data = bookingService.updateBooking(id, request, username, role);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(
            @PathVariable Long id,
            @RequestAttribute("X-Username") String username,
            @RequestAttribute("X-Role") String role) {
        bookingService.deleteBooking(id, username, role);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status("success")
                .message("Booking cancelled successfully")
                .build());
    }
}
