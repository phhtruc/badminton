# Hệ Thống Đặt Sân Cầu Lông

## Mô tả
Website đặt sân cầu lông với giao diện màu vàng nhạt và xanh lá đặc trưng của cầu lông, responsive trên mọi thiết bị.

## Công nghệ sử dụng

### Backend
- **Java Spring Boot 4.0.1** với Java 21
- **PostgreSQL** - Database
- **Spring Security + JWT** - Authentication
- **Spring Mail** - Gửi email thông báo
- **JPA/Hibernate** - ORM

### Frontend
- **VueJS 3** với Composition API
- **Vue Router** - Routing
- **Pinia** - State management
- **Axios** - HTTP client
- **Vite** - Build tool

## Cài đặt

### 1. Cài đặt PostgreSQL
```bash
# MacOS với Homebrew
brew install postgresql@16
brew services start postgresql@16

# Tạo database
psql postgres
CREATE DATABASE badminton_booking;
\q
```

### 2. Chạy SQL Script
Chạy script trong file `badminton_booking_database_design.txt` để tạo bảng:
```bash
psql -U postgres -d badminton_booking -f badminton_booking_database_design.txt
```

### 3. Cấu hình Backend
Sửa file `src/main/resources/application.yaml`:
- Đổi username/password PostgreSQL nếu cần
- Cấu hình email (Gmail):
  - Bật 2-Step Verification trong Google Account
  - Tạo App Password tại: https://myaccount.google.com/apppasswords
  - Cập nhật `spring.mail.username` và `spring.mail.password`

### 4. Chạy Backend
```bash
# Cài dependencies và chạy
./mvnw clean install
./mvnw spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

### 5. Cài đặt Frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:5173`

## Tài khoản mặc định

**Admin:**
- Email: `admin@badminton.com`
- Password: `Admin@123`

## Tính năng

### Người dùng (USER)
- ✅ Đăng ký, đăng nhập
- ✅ Xem danh sách sân
- ✅ Đặt sân qua lịch tuần (giống Google Calendar)
- ✅ Xem lịch đặt của mình
- ✅ Cập nhật thông tin tài khoản
- ✅ Đổi mật khẩu

### Quản trị viên (ADMIN)
- ✅ Xem danh sách đơn đặt sân chờ xác nhận
- ✅ Duyệt/Từ chối đơn đặt sân
- ✅ Nhận thông báo email khi có đơn mới
- ✅ Gửi email xác nhận cho khách hàng

### Giao diện
- 🎨 Màu sắc: Vàng nhạt (#FFD700, #FFF8DC) và Xanh lá (#32CD32, #228B22)
- 📱 Responsive: Hoạt động tốt trên desktop, tablet và mobile
- 📅 Lịch đặt sân: Giao diện lịch tuần với time slots từ 6h-22h
- ✨ Hiệu ứng: Smooth transitions, hover effects

## Cấu trúc Database

### Bảng chính:
1. **roles** - Vai trò (ADMIN, USER)
2. **users** - Người dùng
3. **courts** - Sân cầu lông (2 sân)
4. **bookings** - Đơn đặt sân
5. **notifications** - Thông báo

### Trạng thái booking:
- `PENDING` - Chờ xác nhận
- `APPROVED` - Đã xác nhận
- `REJECTED` - Bị từ chối

## API Endpoints

### Auth
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/login` - Đăng nhập
- `GET /api/auth/me` - Thông tin user hiện tại
- `PUT /api/auth/profile` - Cập nhật thông tin
- `PUT /api/auth/change-password` - Đổi mật khẩu

### Courts
- `GET /api/courts/list` - Danh sách sân (public)
- `GET /api/courts/{id}` - Chi tiết sân

### Bookings
- `POST /api/bookings` - Tạo đơn đặt sân
- `GET /api/bookings/weekly?startDate&endDate` - Lịch tuần
- `GET /api/bookings/court/{courtId}/weekly?startDate&endDate` - Lịch tuần theo sân
- `GET /api/bookings/my-bookings` - Đơn của tôi

### Admin
- `GET /api/admin/bookings/pending` - Đơn chờ xác nhận
- `PUT /api/admin/bookings/{id}/approve` - Duyệt đơn
- `PUT /api/admin/bookings/{id}/reject` - Từ chối đơn

### Notifications
- `GET /api/notifications` - Danh sách thông báo
- `GET /api/notifications/unread` - Thông báo chưa đọc
- `PUT /api/notifications/{id}/read` - Đánh dấu đã đọc
- `PUT /api/notifications/read-all` - Đánh dấu tất cả đã đọc

## Lưu ý

1. **Email**: Để tính năng email hoạt động, cần cấu hình Gmail App Password
2. **Database**: Constraint `no_overlap_booking` ngăn đặt trùng giờ cùng sân
3. **JWT**: Token có hiệu lực 24 giờ
4. **CORS**: Đã cấu hình cho phép frontend chạy trên port 5173

## Troubleshooting

### Lỗi kết nối database
```yaml
# Kiểm tra PostgreSQL đang chạy
brew services list

# Kiểm tra connection
psql -U postgres -d badminton_booking
```

### Lỗi email không gửi được
- Kiểm tra đã bật 2-Step Verification
- Tạo App Password mới
- Cập nhật đúng email và app password trong application.yaml

### Frontend không kết nối được backend
- Kiểm tra backend đang chạy tại port 8080
- Kiểm tra CORS trong SecurityConfig

## Mở rộng

Có thể mở rộng thêm:
- Thanh toán online
- WebSocket cho real-time notifications
- Admin quản lý sân, giá cả
- Thống kê, báo cáo
- Mobile app với React Native/Flutter

