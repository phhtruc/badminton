-- =========================================
-- BADMINTON BOOKING SYSTEM - SUPABASE SETUP
-- PostgreSQL (Supabase)
-- =========================================

-- Chạy script này trong Supabase SQL Editor
-- Dashboard > SQL Editor > New Query

BEGIN;

-- =========================================
-- 1. ROLES TABLE
-- =========================================
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

-- Insert default roles
INSERT INTO roles (name)
VALUES ('ADMIN'), ('USER')
ON CONFLICT (name) DO NOTHING;

-- =========================================
-- 2. USERS TABLE
-- =========================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role_id INT NOT NULL REFERENCES roles(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookup
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- =========================================
-- 3. COURTS TABLE
-- =========================================
CREATE TABLE IF NOT EXISTS courts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default courts
INSERT INTO courts (name, description, open_time, close_time, is_active)
VALUES
    ('Sân cầu lông 1', 'Sân tiêu chuẩn thi đấu', '06:00', '22:00', true),
    ('Sân cầu lông 2', 'Sân tập luyện', '06:00', '22:00', true)
ON CONFLICT DO NOTHING;

-- =========================================
-- 4. BOOKINGS TABLE
-- =========================================
CREATE TABLE IF NOT EXISTS bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    court_id INT NOT NULL REFERENCES courts(id) ON DELETE CASCADE,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by UUID REFERENCES users(id),
    CONSTRAINT chk_time_valid CHECK (start_time < end_time)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_booking_calendar
ON bookings (court_id, booking_date, start_time, end_time)
WHERE status = 'APPROVED';

CREATE INDEX IF NOT EXISTS idx_booking_pending
ON bookings (status, created_at);

CREATE INDEX IF NOT EXISTS idx_booking_user
ON bookings (user_id);

-- =========================================
-- 5. PREVENT OVERLAPPING BOOKINGS (Supabase compatible)
-- =========================================
-- Supabase hỗ trợ btree_gist extension
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Drop constraint nếu đã tồn tại
ALTER TABLE bookings DROP CONSTRAINT IF EXISTS no_overlap_booking;

-- Add exclusion constraint để ngăn booking trùng lặp
ALTER TABLE bookings
ADD CONSTRAINT no_overlap_booking
EXCLUDE USING gist (
    court_id WITH =,
    booking_date WITH =,
    tsrange(
        (booking_date + start_time)::timestamp,
        (booking_date + end_time)::timestamp
    ) WITH &&
)
WHERE (status = 'APPROVED');

-- =========================================
-- 6. NOTIFICATIONS TABLE
-- =========================================
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    title VARCHAR(200),
    content TEXT,
    type VARCHAR(20),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for notifications
CREATE INDEX IF NOT EXISTS idx_notifications_user
ON notifications (receiver_id, is_read, created_at);

-- =========================================
-- 7. FUNCTION TO UPDATE updated_at
-- =========================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for users table
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMIT;

-- =========================================
-- VERIFICATION QUERIES
-- =========================================
-- Kiểm tra các bảng đã được tạo
SELECT
    schemaname,
    tablename
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;

-- Kiểm tra số lượng record
SELECT 'roles' as table_name, COUNT(*) as count FROM roles
UNION ALL
SELECT 'courts', COUNT(*) FROM courts;

