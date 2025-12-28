package com.example.badminton_booking.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class BookingResponse {
    private UUID id;
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private String userPhone;
    private Integer courtId;
    private String courtName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String approvedByName;
}

