# Hướng dẫn kết nối Supabase PostgreSQL

## 🔑 Lấy thông tin Database Password

1. Đăng nhập vào [Supabase Dashboard](https://app.supabase.com)
2. Chọn project của bạn: **ufygceudhkfjihexldyd**
3. Vào **Settings** (icon bánh răng) > **Database**
4. Tìm phần **Connection string** hoặc **Database settings**
5. Lấy **Database password** (nếu quên, có thể reset)

## 📝 Cấu hình Application

### File: `src/main/resources/application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.ufygceudhkfjihexldyd.supabase.co:5432/postgres
    username: postgres
    password: [YOUR-DATABASE-PASSWORD]  # ← Thay bằng password từ Supabase
```

## 🗄️ Tạo Database Schema trên Supabase

### Cách 1: Sử dụng Supabase SQL Editor (Khuyến nghị)

1. Vào Supabase Dashboard > **SQL Editor**
2. Click **New Query**
3. Copy toàn bộ nội dung file `supabase_setup.sql`
4. Paste vào editor và click **Run**
5. Kiểm tra kết quả - sẽ hiển thị danh sách bảng đã tạo

### Cách 2: Tự động tạo bằng Hibernate (Đơn giản hơn)

Tôi đã cấu hình `ddl-auto: update` trong application.yaml, nghĩa là:
- Khi chạy Spring Boot lần đầu, Hibernate sẽ **tự động tạo tất cả các bảng**
- Bạn chỉ cần:
  1. Cập nhật password trong `application.yaml`
  2. Chạy: `./mvnw spring-boot:run`
  3. Hibernate sẽ tạo tất cả bảng và dữ liệu mẫu

## 🚀 Các bước chạy hệ thống

### 1. Cập nhật password
```bash
# Mở file
nano src/main/resources/application.yaml

# Thay dòng này:
password: badminton-booking-
# Thành:
password: [YOUR-SUPABASE-DB-PASSWORD]
```

### 2. Chạy Backend
```bash
# Từ thư mục gốc dự án
./mvnw spring-boot:run
```

Backend sẽ:
- Kết nối đến Supabase
- Tự động tạo các bảng (nếu chưa có)
- Tạo admin user mặc định
- Tạo 2 sân cầu lông mẫu

### 3. Chạy Frontend
```bash
cd frontend
npm install
npm run dev
```

### 4. Truy cập
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Supabase Dashboard: https://app.supabase.com

### 5. Đăng nhập
- Email: `admin@badminton.com`
- Password: `Admin@123`

## 🔍 Kiểm tra kết nối

### Test kết nối từ terminal:
```bash
psql "postgresql://postgres:[YOUR-PASSWORD]@db.ufygceudhkfjihexldyd.supabase.co:5432/postgres"
```

### Kiểm tra tables trong Supabase:
1. Vào **Table Editor** trong Supabase Dashboard
2. Bạn sẽ thấy các bảng:
   - roles
   - users
   - courts
   - bookings
   - notifications

## ⚠️ Lưu ý quan trọng

1. **Connection Pooling**: Supabase free tier giới hạn kết nối, đã cấu hình `maximum-pool-size: 5`

2. **Row Level Security (RLS)**: Nếu bạn bật RLS trong Supabase, cần disable cho các bảng này hoặc tạo policies phù hợp:
   ```sql
   -- Disable RLS (nếu cần)
   ALTER TABLE roles DISABLE ROW LEVEL SECURITY;
   ALTER TABLE users DISABLE ROW LEVEL SECURITY;
   ALTER TABLE courts DISABLE ROW LEVEL SECURITY;
   ALTER TABLE bookings DISABLE ROW LEVEL SECURITY;
   ALTER TABLE notifications DISABLE ROW LEVEL SECURITY;
   ```

3. **Timezone**: Đã cấu hình `time_zone: UTC` để đồng bộ với Supabase

## 🛠️ Troubleshooting

### Lỗi: "connection refused"
- Kiểm tra password đã đúng chưa
- Kiểm tra URL: `db.ufygceudhkfjihexldyd.supabase.co`
- Kiểm tra project còn active không

### Lỗi: "too many connections"
- Giảm `maximum-pool-size` xuống 3-5
- Upgrade Supabase plan nếu cần

### Lỗi: "permission denied"
- Kiểm tra RLS đã disable chưa
- Kiểm tra user `postgres` có quyền truy cập

## 📚 API Key sử dụng ở đâu?

**API Key của bạn (`sb_publishable_dpG_xH0s3GewUsbrwN7HjA_FJ-DpGCG`) không cần thiết cho backend Spring Boot.**

API Key chỉ dùng khi:
- Gọi Supabase REST API trực tiếp từ frontend
- Sử dụng Supabase Auth
- Sử dụng Supabase Storage

Trong project này, chúng ta:
- Backend: Kết nối trực tiếp đến PostgreSQL
- Authentication: Tự xây dựng với JWT
- Không cần Supabase API Key

## ✅ Checklist

- [ ] Lấy database password từ Supabase Dashboard
- [ ] Cập nhật password trong `application.yaml`
- [ ] (Tùy chọn) Chạy `supabase_setup.sql` trong SQL Editor
- [ ] Chạy `./mvnw spring-boot:run`
- [ ] Kiểm tra tables đã được tạo trong Supabase Table Editor
- [ ] Chạy frontend: `npm run dev`
- [ ] Login với admin: `admin@badminton.com` / `Admin@123`

