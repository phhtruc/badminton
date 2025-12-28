package com.example.badminton_booking.config;

import com.example.badminton_booking.entity.Court;
import com.example.badminton_booking.entity.Role;
import com.example.badminton_booking.entity.User;
import com.example.badminton_booking.repository.CourtRepository;
import com.example.badminton_booking.repository.RoleRepository;
import com.example.badminton_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles
        if (roleRepository.count() == 0) {
            Role adminRole = new Role(null, "ADMIN");
            Role userRole = new Role(null, "USER");
            roleRepository.save(adminRole);
            roleRepository.save(userRole);
            System.out.println("Roles initialized");
        }

        // Initialize admin user
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFullName(adminName);
            admin.setRole(adminRole);
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("Admin user created: " + adminEmail);
        }

        // Initialize courts
        if (courtRepository.count() == 0) {
            Court court1 = new Court();
            court1.setName("Sân cầu lông 1");
            court1.setDescription("Sân tiêu chuẩn thi đấu");
            court1.setOpenTime(LocalTime.of(6, 0));
            court1.setCloseTime(LocalTime.of(22, 0));
            court1.setIsActive(true);

            Court court2 = new Court();
            court2.setName("Sân cầu lông 2");
            court2.setDescription("Sân tập luyện");
            court2.setOpenTime(LocalTime.of(6, 0));
            court2.setCloseTime(LocalTime.of(22, 0));
            court2.setIsActive(true);

            courtRepository.save(court1);
            courtRepository.save(court2);
            System.out.println("Courts initialized");
        }
    }
}

