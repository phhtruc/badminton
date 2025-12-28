-- =========================================
-- BADMINTON BOOKING SYSTEM - DATABASE SETUP
-- PostgreSQL 13+
-- =========================================

BEGIN;

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "btree_gist";

-- =========================================
-- 2. ROLES
-- =========================================
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES
('ADMIN'),
('USER');

-- =========================================
-- 3. USERS
-- =========================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
);

-- =========================================
-- 4. COURTS
-- =========================================
CREATE TABLE courts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO courts (name, description, open_time, close_time)
VALUES
('Sân cầu lông 1', 'Sân tiêu chuẩn thi đấu', '06:00', '22:00'),
('Sân cầu lông 2', 'Sân tập luyện', '06:00', '22:00');

-- =========================================
-- 5. BOOKINGS
-- =========================================
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,
    court_id INT NOT NULL,

    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by UUID,

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT fk_booking_court
        FOREIGN KEY (court_id) REFERENCES courts(id),

    CONSTRAINT fk_booking_admin
        FOREIGN KEY (approved_by) REFERENCES users(id),

    CONSTRAINT chk_time_valid
        CHECK (start_time < end_time)
);

-- =========================================
-- 6. PREVENT OVERLAPPING BOOKINGS
-- =========================================
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
-- 7. NOTIFICATIONS
-- =========================================
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    receiver_id UUID NOT NULL,
    booking_id UUID NOT NULL,

    title VARCHAR(200),
    content TEXT,
    type VARCHAR(20),
    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notify_user
        FOREIGN KEY (receiver_id) REFERENCES users(id),

    CONSTRAINT fk_notify_booking
        FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- =========================================
-- 8. INDEXES
-- =========================================
CREATE INDEX idx_booking_calendar
ON bookings (court_id, booking_date, start_time, end_time)
WHERE status = 'APPROVED';

CREATE INDEX idx_booking_pending
ON bookings (status, created_at);

CREATE INDEX idx_notifications_user
ON notifications (receiver_id, is_read, created_at);

COMMIT;

