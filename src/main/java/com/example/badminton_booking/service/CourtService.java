package com.example.badminton_booking.service;

import com.example.badminton_booking.entity.Court;
import com.example.badminton_booking.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository courtRepository;

    public List<Court> getAllCourts() {
        return courtRepository.findAll();
    }

    public List<Court> getActiveCourts() {
        return courtRepository.findByIsActiveTrue();
    }

    public Court getCourtById(Integer id) {
        return courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sân không tồn tại"));
    }
}

