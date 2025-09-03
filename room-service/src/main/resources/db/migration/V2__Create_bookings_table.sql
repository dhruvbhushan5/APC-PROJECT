-- Room Service Database Schema
-- V2__Create_bookings_table.sql

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    guest_email VARCHAR(100) NOT NULL,
    guest_phone VARCHAR(20),
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_nights INTEGER NOT NULL,
    number_of_guests INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2),
    status VARCHAR(50) NOT NULL,
    special_requests VARCHAR(1000),
    cancellation_reason VARCHAR(500),
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_booking_guest_email ON bookings(guest_email);
CREATE INDEX IF NOT EXISTS idx_booking_dates ON bookings(check_in_date, check_out_date);
CREATE INDEX IF NOT EXISTS idx_booking_status ON bookings(status);
CREATE INDEX IF NOT EXISTS idx_booking_room ON bookings(room_id);
