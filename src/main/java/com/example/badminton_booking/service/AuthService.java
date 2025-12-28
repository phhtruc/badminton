package com.example.badminton_booking.service;

import com.example.badminton_booking.dto.*;
import com.example.badminton_booking.entity.Role;
import com.example.badminton_booking.entity.User;
import com.example.badminton_booking.repository.RoleRepository;
import com.example.badminton_booking.repository.UserRepository;
import com.example.badminton_booking.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Validate phone uniqueness
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng");
        }

        // Get USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setNickname(request.getNickname()); // Có thể null
        user.setPhone(request.getPhone());
        user.setRole(userRole);
        user.setIsActive(true);

        // Generate tokens
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // Save refresh token to database
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpiration() / 1000));

        user = userRepository.save(user);

        // Calculate token expiry timestamps
        long now = System.currentTimeMillis();
        long accessTokenExpiry = now + jwtTokenProvider.getAccessTokenExpiration();
        long refreshTokenExpiry = now + jwtTokenProvider.getRefreshTokenExpiration();

        return new AuthResponse(
                accessToken,
                refreshToken,
                accessTokenExpiry,
                refreshTokenExpiry,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getNickname(),
                user.getRole().getName()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        // Generate new tokens
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // Update refresh token in database
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusSeconds(
                jwtTokenProvider.getRefreshTokenExpiration() / 1000));
        userRepository.save(user);

        // Calculate token expiry timestamps
        long now = System.currentTimeMillis();
        long accessTokenExpiry = now + jwtTokenProvider.getAccessTokenExpiration();
        long refreshTokenExpiry = now + jwtTokenProvider.getRefreshTokenExpiration();

        return new AuthResponse(
                accessToken,
                refreshToken,
                accessTokenExpiry,
                refreshTokenExpiry,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getNickname(),
                user.getRole().getName()
        );
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token format
        try {
            String email = jwtTokenProvider.extractUsername(refreshToken);

            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            // Check if refresh token matches and not expired
            if (!refreshToken.equals(user.getRefreshToken())) {
                throw new RuntimeException("Refresh token không hợp lệ");
            }

            if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Refresh token đã hết hạn");
            }

            // Generate new access token
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities("ROLE_" + user.getRole().getName())
                    .build();

            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

            // Calculate token expiry timestamps
            long now = System.currentTimeMillis();
            long accessTokenExpiry = now + jwtTokenProvider.getAccessTokenExpiration();
            long refreshTokenExpiry = user.getRefreshTokenExpiry()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            return new AuthResponse(
                    newAccessToken,
                    refreshToken, // Keep the same refresh token
                    accessTokenExpiry,
                    refreshTokenExpiry,
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getNickname(),
                    user.getRole().getName()
            );

        } catch (Exception e) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Clear refresh token
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public User updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Clear all refresh tokens when password changes
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);

        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }
}
