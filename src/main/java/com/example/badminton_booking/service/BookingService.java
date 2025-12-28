package com.example.badminton_booking.service;

import com.example.badminton_booking.dto.BookingRequest;
import com.example.badminton_booking.dto.BookingResponse;
import com.example.badminton_booking.entity.Booking;
import com.example.badminton_booking.entity.Court;
import com.example.badminton_booking.entity.Notification;
import com.example.badminton_booking.entity.User;
import com.example.badminton_booking.repository.BookingRepository;
import com.example.badminton_booking.repository.CourtRepository;
import com.example.badminton_booking.repository.NotificationRepository;
import com.example.badminton_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public BookingResponse createBooking(UUID userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new RuntimeException("Sân không tồn tại"));

        if (!court.getIsActive()) {
            throw new RuntimeException("Sân không hoạt động");
        }

        if (request.getStartTime().isBefore(court.getOpenTime()) ||
            request.getEndTime().isAfter(court.getCloseTime())) {
            throw new RuntimeException("Thời gian đặt sân không hợp lệ");
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("Giờ bắt đầu phải trước giờ kết thúc");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCourt(court);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setNote(request.getNote());
        booking.setStatus("PENDING");

        booking = bookingRepository.save(booking);

        // Gửi thông báo cho admin
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole().getName().equals("ADMIN"))
                .collect(Collectors.toList());

        for (User admin : admins) {
            Notification notification = new Notification();
            notification.setReceiver(admin);
            notification.setBooking(booking);
            notification.setTitle("Yêu cầu đặt sân mới");
            notification.setContent(String.format("%s vừa đặt %s vào ngày %s từ %s đến %s",
                    user.getFullName(), court.getName(), request.getBookingDate(),
                    request.getStartTime(), request.getEndTime()));
            notification.setType("BOOKING_REQUEST");
            notificationRepository.save(notification);

            // Gửi email cho admin
            emailService.sendBookingNotification(admin.getEmail(), booking);
        }

        return convertToResponse(booking);
    }

    public List<BookingResponse> getWeeklyBookings(LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = bookingRepository.findApprovedBookingsByDateRange(startDate, endDate);
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsByCourtAndWeek(Integer courtId, LocalDate startDate, LocalDate endDate) {
        List<Booking> bookings = bookingRepository.findApprovedBookingsByCourtAndDateRange(courtId, startDate, endDate);
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getUserBookings(UUID userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<BookingResponse> getPendingBookings() {
        List<Booking> bookings = bookingRepository.findByStatus("PENDING");
        return bookings.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse approveBooking(UUID bookingId, UUID adminId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

        if (!admin.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Bạn không có quyền thực hiện hành động này");
        }

        booking.setStatus("APPROVED");
        booking.setApprovedAt(LocalDateTime.now());
        booking.setApprovedBy(admin);

        booking = bookingRepository.save(booking);

        // Gửi thông báo cho user
        Notification notification = new Notification();
        notification.setReceiver(booking.getUser());
        notification.setBooking(booking);
        notification.setTitle("Đặt sân thành công");
        notification.setContent(String.format("Yêu cầu đặt %s vào ngày %s từ %s đến %s đã được xác nhận",
                booking.getCourt().getName(), booking.getBookingDate(),
                booking.getStartTime(), booking.getEndTime()));
        notification.setType("BOOKING_APPROVED");
        notificationRepository.save(notification);

        // Gửi email xác nhận cho user
        emailService.sendBookingConfirmation(booking.getUser().getEmail(), booking);

        return convertToResponse(booking);
    }

    @Transactional
    public BookingResponse rejectBooking(UUID bookingId, UUID adminId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

        if (!admin.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("Bạn không có quyền thực hiện hành động này");
        }

        booking.setStatus("REJECTED");
        booking = bookingRepository.save(booking);

        // Gửi thông báo cho user
        Notification notification = new Notification();
        notification.setReceiver(booking.getUser());
        notification.setBooking(booking);
        notification.setTitle("Đặt sân bị từ chối");
        notification.setContent(String.format("Yêu cầu đặt %s vào ngày %s từ %s đến %s đã bị từ chối",
                booking.getCourt().getName(), booking.getBookingDate(),
                booking.getStartTime(), booking.getEndTime()));
        notification.setType("BOOKING_REJECTED");
        notificationRepository.save(notification);

        return convertToResponse(booking);
    }

    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUser().getId());
        response.setUserFullName(booking.getUser().getFullName());
        response.setUserEmail(booking.getUser().getEmail());
        response.setUserPhone(booking.getUser().getPhone());
        response.setCourtId(booking.getCourt().getId());
        response.setCourtName(booking.getCourt().getName());
        response.setBookingDate(booking.getBookingDate());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setStatus(booking.getStatus());
        response.setNote(booking.getNote());
        response.setCreatedAt(booking.getCreatedAt());
        response.setApprovedAt(booking.getApprovedAt());
        if (booking.getApprovedBy() != null) {
            response.setApprovedByName(booking.getApprovedBy().getFullName());
        }
        return response;
    }
}

