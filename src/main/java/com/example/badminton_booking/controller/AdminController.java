package com.example.badminton_booking.controller;

import com.example.badminton_booking.dto.BookingResponse;
import com.example.badminton_booking.entity.User;
import com.example.badminton_booking.service.AuthService;
import com.example.badminton_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final BookingService bookingService;
    private final AuthService authService;

    @GetMapping("/bookings/pending")
    public ResponseEntity<?> getPendingBookings() {
        try {
            List<BookingResponse> bookings = bookingService.getPendingBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/bookings/{bookingId}/approve")
    public ResponseEntity<?> approveBooking(@PathVariable UUID bookingId) {
        try {
            User admin = authService.getCurrentUser();
            BookingResponse response = bookingService.approveBooking(bookingId, admin.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/bookings/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable UUID bookingId) {
        try {
            User admin = authService.getCurrentUser();
            BookingResponse response = bookingService.rejectBooking(bookingId, admin.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

