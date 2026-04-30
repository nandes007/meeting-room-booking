package com.nandestech.meetingroom.controller;

import com.nandestech.meetingroom.dto.ApiResponse;
import com.nandestech.meetingroom.dto.RoomRequest;
import com.nandestech.meetingroom.dto.RoomResponse;
import com.nandestech.meetingroom.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        try {
            RoomResponse data = roomService.createRoom(request);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed("failed to create room"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        try {
            List<RoomResponse> data = roomService.getAllRooms();
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed("failed to get rooms"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        try {
            RoomResponse data = roomService.getRoomById(id);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed("failed to get room"));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        try {
            RoomResponse data = roomService.updateRoom(id, request);
            return ResponseEntity.ok(ApiResponse.success(data));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed("failed to update room"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status("success")
                    .message("room deleted successfully")
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failed("failed to delete room"));
        }
    }
}
