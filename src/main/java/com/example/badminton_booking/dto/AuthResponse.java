package com.example.badminton_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long accessTokenExpiry;  // Timestamp in milliseconds
    private Long refreshTokenExpiry; // Timestamp in milliseconds
    private UUID id;
    private String email;
    private String fullName;
    private String nickname;
    private String role;

    public AuthResponse(String accessToken, String refreshToken, Long accessTokenExpiry,
                       Long refreshTokenExpiry, UUID id, String email, String fullName,
                       String nickname, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.nickname = nickname;
        this.role = role;
    }
}
