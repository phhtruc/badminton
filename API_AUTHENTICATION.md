# API Documentation - Authentication System

## 🔐 Tính năng Đăng ký & Đăng nhập

### 1. Đăng ký (Register)
**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A",
  "nickname": "VanA",  // Optional - có thể bỏ trống
  "email": "vana@example.com",
  "phone": "0123456789",  // 10-11 chữ số
  "password": "Password123"  // Tối thiểu 8 ký tự, có chữ thường, chữ hoa và số
}
```

**Response Success (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "accessTokenExpiry": 1703769600000,  // Timestamp (1 giờ)
  "refreshTokenExpiry": 1704374400000, // Timestamp (7 ngày)
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "vana@example.com",
  "fullName": "Nguyễn Văn A",
  "nickname": "VanA",
  "role": "USER"
}
```

**Validation Rules:**
- ✅ **Họ tên**: Bắt buộc, 2-100 ký tự
- ✅ **Biệt danh**: Không bắt buộc, tối đa 50 ký tự
- ✅ **Email**: Bắt buộc, định dạng email hợp lệ, unique
- ✅ **Số điện thoại**: 10-11 chữ số
- ✅ **Mật khẩu**: Tối thiểu 8 ký tự, phải có:
  - Ít nhất 1 chữ thường (a-z)
  - Ít nhất 1 chữ hoa (A-Z)
  - Ít nhất 1 chữ số (0-9)

---

### 2. Đăng nhập (Login)
**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "vana@example.com",
  "password": "Password123"
}
```

**Response Success (200):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "accessTokenExpiry": 1703769600000,
  "refreshTokenExpiry": 1704374400000,
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "vana@example.com",
  "fullName": "Nguyễn Văn A",
  "nickname": "VanA",
  "role": "USER"
}
```

**Error Responses:**
- `400`: "Email đã được sử dụng"
- `400`: "Tài khoản đã bị vô hiệu hóa"
- `401`: "Email hoặc mật khẩu không đúng"

---

### 3. Làm mới Access Token (Refresh Token)
**Endpoint:** `POST /api/auth/refresh-token`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response Success (200):**
```json
{
  "accessToken": "NEW_ACCESS_TOKEN...",
  "refreshToken": "SAME_REFRESH_TOKEN...",
  "tokenType": "Bearer",
  "accessTokenExpiry": 1703773200000,  // New expiry
  "refreshTokenExpiry": 1704374400000,
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "vana@example.com",
  "fullName": "Nguyễn Văn A",
  "nickname": "VanA",
  "role": "USER"
}
```

**Error Responses:**
- `400`: "Refresh token không hợp lệ"
- `400`: "Refresh token đã hết hạn"

---

### 4. Đăng xuất (Logout)
**Endpoint:** `POST /api/auth/logout`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response Success (200):**
```json
{
  "message": "Đăng xuất thành công"
}
```

---

### 5. Lấy thông tin user hiện tại
**Endpoint:** `GET /api/auth/me`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response Success (200):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "vana@example.com",
  "fullName": "Nguyễn Văn A",
  "nickname": "VanA",
  "phone": "0123456789",
  "role": "USER",
  "isActive": true
}
```

---

### 6. Cập nhật thông tin
**Endpoint:** `PUT /api/auth/profile`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn B",
  "phone": "0987654321"
}
```

---

### 7. Đổi mật khẩu
**Endpoint:** `PUT /api/auth/change-password`

**Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:**
```json
{
  "oldPassword": "Password123",
  "newPassword": "NewPassword456"
}
```

**Note:** Sau khi đổi mật khẩu, tất cả refresh tokens sẽ bị xóa và user cần đăng nhập lại.

---

## 🔑 JWT Token Details

### Access Token
- **Thời hạn**: 1 giờ (3600000 ms)
- **Lưu trữ**: LocalStorage/SessionStorage trong frontend
- **Sử dụng**: Gửi trong Authorization header cho mọi API request
- **Format**: `Authorization: Bearer <access_token>`

### Refresh Token
- **Thời hạn**: 7 ngày (604800000 ms)
- **Lưu trữ**: 
  - Backend: Lưu trong database (bảng users)
  - Frontend: LocalStorage (hoặc HttpOnly Cookie cho bảo mật tốt hơn)
- **Sử dụng**: Dùng để lấy access token mới khi hết hạn

---

## 🔒 Cấu hình trong .env

```env
# JWT Configuration
JWT_SECRET=YourVeryLongSecretKeyForJWTTokenGenerationAtLeast256BitsLong12345678901234567890
JWT_ACCESS_TOKEN_EXPIRATION=3600000       # 1 giờ
JWT_REFRESH_TOKEN_EXPIRATION=604800000    # 7 ngày
```

---

## 🧪 Test với cURL

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn A",
    "nickname": "VanA",
    "email": "vana@example.com",
    "phone": "0123456789",
    "password": "Password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vana@example.com",
    "password": "Password123"
  }'
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### Get Current User
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## 📊 Database Schema

### users table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),              -- NEW: Optional field
    phone VARCHAR(20),
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    refresh_token VARCHAR(500),        -- NEW: Store refresh token
    refresh_token_expiry TIMESTAMP,    -- NEW: Token expiry
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## 🎯 Luồng hoạt động

### Đăng ký/Đăng nhập thành công:
1. Frontend nhận được `accessToken` và `refreshToken`
2. Lưu `accessToken` vào memory/state (hoặc localStorage)
3. Lưu `refreshToken` vào localStorage (hoặc secure HttpOnly cookie)
4. Gửi `accessToken` trong header của mọi API request

### Khi Access Token hết hạn:
1. API trả về 401 Unauthorized
2. Frontend tự động gọi `/api/auth/refresh-token` với `refreshToken`
3. Nhận `accessToken` mới
4. Retry request bị lỗi với token mới

### Đăng xuất:
1. Gọi `/api/auth/logout` để xóa refresh token ở backend
2. Xóa tokens ở frontend
3. Redirect về trang login

---

## ⚠️ Lưu ý bảo mật

1. **Không bao giờ** lưu token trong localStorage nếu website có nguy cơ XSS
2. **Nên sử dụng** HttpOnly Cookies để lưu refresh token
3. **Luôn luôn** sử dụng HTTPS trong production
4. **Refresh token** chỉ được sử dụng 1 lần (rotation strategy - có thể implement sau)
5. **Logout** khi đổi mật khẩu để revoke tất cả sessions

