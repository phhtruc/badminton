package com.example.badminton_booking.service;

import com.example.badminton_booking.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendBookingNotification(String to, Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Yêu cầu đặt sân mới - Badminton Booking");
            message.setText(String.format(
                    "Có yêu cầu đặt sân mới:\n\n" +
                    "Khách hàng: %s (%s)\n" +
                    "Sân: %s\n" +
                    "Ngày: %s\n" +
                    "Thời gian: %s - %s\n" +
                    "Ghi chú: %s\n\n" +
                    "Vui lòng đăng nhập hệ thống để xác nhận.",
                    booking.getUser().getFullName(),
                    booking.getUser().getEmail(),
                    booking.getCourt().getName(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getNote() != null ? booking.getNote() : "Không có"
            ));

            mailSender.send(message);
            log.info("Email notification sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    public void sendBookingConfirmation(String to, Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Xác nhận đặt sân - Badminton Booking");
            message.setText(String.format(
                    "Chào %s,\n\n" +
                    "Yêu cầu đặt sân của bạn đã được xác nhận:\n\n" +
                    "Sân: %s\n" +
                    "Ngày: %s\n" +
                    "Thời gian: %s - %s\n\n" +
                    "Cảm ơn bạn đã sử dụng dịch vụ!",
                    booking.getUser().getFullName(),
                    booking.getCourt().getName(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime()
            ));

            mailSender.send(message);
            log.info("Confirmation email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send confirmation email to: {}", to, e);
        }
    }
}

