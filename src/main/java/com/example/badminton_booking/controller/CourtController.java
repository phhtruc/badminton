package com.example.badminton_booking.controller;

import com.example.badminton_booking.entity.Court;
import com.example.badminton_booking.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    @GetMapping("/list")
    public ResponseEntity<?> getAllCourts() {
        try {
            List<Court> courts = courtService.getActiveCourts();
            return ResponseEntity.ok(courts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourtById(@PathVariable Integer id) {
        try {
            Court court = courtService.getCourtById(id);
            return ResponseEntity.ok(court);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

