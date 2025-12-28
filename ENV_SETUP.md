# 📋 Hướng dẫn sử dụng file .env

## 🎯 Tổng quan

Dự án đã được cấu hình để sử dụng file `.env` để lưu trữ các thông tin nhạy cảm và cấu hình môi trường. Điều này giúp:
- ✅ Bảo mật thông tin nhạy cảm (password, secret key)
- ✅ Dễ dàng chuyển đổi giữa các môi trường (dev, staging, production)
- ✅ Không commit thông tin nhạy cảm lên Git

## 📁 Cấu trúc file .env

```
badminton-booking/
├── .env                    # Backend - Development
├── .env.production         # Backend - Production
├── .gitignore             # Đã cấu hình để ignore .env files
└── frontend/
    └── .env               # Frontend - Development
```

## 🔧 Cấu hình Backend (.env)

### Bước 1: Copy file mẫu
```bash
cd /Users/phhtruc/Downloads/badminton-booking
```

File `.env` đã được tạo với cấu hình mặc định kết nối đến Neon PostgreSQL.

### Bước 2: Cập nhật thông tin Database

Mở file `.env` và cập nhật các giá trị:

```bash
nano local.env
```

**Các biến quan trọng cần cập nhật:**

```env
# Database - ĐÃ CẤU HÌNH SẴN cho Neon
DB_URL=jdbc:postgresql://ep-falling-paper-adtgusy1-pooler.c-2.us-east-1.aws.neon.tech:5432/badminton?sslmode=require
DB_USERNAME=neondb_owner
DB_PASSWORD=npg_hrDC6uqUsLl2

# JWT - NÊN ĐỔI SECRET KEY
JWT_SECRET=YourVeryLongSecretKeyForJWTTokenGenerationAtLeast256BitsLong12345678901234567890

# Admin - CÓ THỂ ĐỔI
ADMIN_EMAIL=admin@badminton.com
ADMIN_PASSWORD=Admin@123

# Email - CẦN CẬP NHẬT để gửi email
GMAIL_USERNAME=your-email@gmail.com
GMAIL_APP_PASSWORD=your-app-password
```

### Bước 3: Tạo Gmail App Password (cho tính năng gửi email)

1. Truy cập: https://myaccount.google.com/apppasswords
2. Tạo App Password mới
3. Copy password và cập nhật vào `GMAIL_APP_PASSWORD`

## 🎨 Cấu hình Frontend (frontend/.env)

Frontend cũng có file `.env` riêng:

```bash
cd frontend
nano local.env
```

File đã cấu hình sẵn:
```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Đặt Sân Cầu Lông
```

**Lưu ý:** VueJS với Vite yêu cầu biến môi trường phải bắt đầu bằng `VITE_`

## 🚀 Chạy ứng dụng

### Backend

```bash
# Spring Boot sẽ tự động đọc file local.env
./mvnw spring-boot:run
```

Hoặc nếu muốn dùng file production:
```bash
# Copy local.env.production thành local.env hoặc set profile
cp local.env.production local.env
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Vite sẽ tự động đọc file `.env` và expose các biến `VITE_*`

## 🔐 Bảo mật

### File .gitignore đã cấu hình:

```gitignore
# Environment files
local.env
.env.local
.env.production
.env.*.local
```

**❗ QUAN TRỌNG:**
- ✅ File `.env` đã được thêm vào `.gitignore`
- ✅ KHÔNG bao giờ commit file `.env` lên Git
- ✅ Chia sẻ file `.env.example` thay vì `.env` thật

## 📝 Tạo file .env.example

Để chia sẻ cấu trúc với team:

```bash
# Tạo file example từ file local.env (xóa các giá trị nhạy cảm)
cp local.env local.env.example

# Sửa local.env.example, thay các giá trị thật bằng placeholder
nano local.env.example
```

Ví dụ:
```env
DB_URL=jdbc:postgresql://your-db-host:5432/database_name
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_at_least_256_bits
```

## 🌍 Các môi trường khác nhau

### Development (Local)
```bash
# Sử dụng local.env
./mvnw spring-boot:run
```

### Production
```bash
# Option 1: Copy file production
cp local.env.production local.env
./mvnw spring-boot:run

# Option 2: Set biến môi trường trực tiếp
export DB_URL="jdbc:postgresql://..."
export DB_USERNAME="..."
./mvnw spring-boot:run

# Option 3: Deploy với Docker
docker run -e DB_URL="..." -e DB_USERNAME="..." your-app
```

## 🐳 Docker với .env

Nếu dùng Docker Compose:

```yaml
# docker-compose.yml
services:
  backend:
    build: .
    env_file:
      - local.env
    ports:
      - "8080:8080"
```

## 🔍 Kiểm tra cấu hình

### Test kết nối database:

```bash
# Build và chạy
./mvnw clean install
./mvnw spring-boot:run
```

Nếu thành công, bạn sẽ thấy:
```
Started BadmintonBookingApplication in X.XXX seconds
Roles initialized
Admin user created: admin@badminton.com
Courts initialized
```

### Test frontend:

```bash
cd frontend
npm run dev
```

Truy cập: http://localhost:5173

## ❓ Troubleshooting

### Lỗi: "DB_URL must be set"
- Kiểm tra file `.env` có tồn tại trong thư mục gốc
- Kiểm tra tên biến có đúng không
- Restart ứng dụng sau khi sửa `.env`

### Lỗi: Connection refused
- Kiểm tra `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` có đúng không
- Kiểm tra database có đang chạy không

### Frontend không đọc được biến môi trường
- Biến phải bắt đầu bằng `VITE_`
- Phải restart dev server sau khi sửa `.env`
- Kiểm tra file `.env` ở đúng trong thư mục `frontend/`

## 📚 Tài liệu tham khảo

- Spring Boot Environment Variables: https://docs.spring.io/spring-boot/reference/features/external-config.html
- Vite Environment Variables: https://vitejs.dev/guide/env-and-mode.html
- Spring Dotenv: https://github.com/paulschwarz/spring-dotenv

## ✅ Checklist

- [ ] File `.env` đã được tạo trong thư mục gốc
- [ ] Đã cập nhật `DB_PASSWORD` với password thật
- [ ] Đã cập nhật `GMAIL_USERNAME` và `GMAIL_APP_PASSWORD` (nếu cần email)
- [ ] Đã tạo `JWT_SECRET` mạnh cho production
- [ ] File `.env` đã được thêm vào `.gitignore`
- [ ] Frontend `.env` đã được tạo trong `frontend/`
- [ ] Test chạy backend: `./mvnw spring-boot:run`
- [ ] Test chạy frontend: `cd frontend && npm run dev`

---

**🎉 Hoàn tất! Hệ thống đã sẵn sàng với cấu hình .env file**

