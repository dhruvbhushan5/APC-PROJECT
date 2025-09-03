-- Room Service Database Schema
-- V1__Create_rooms_table.sql

CREATE TABLE IF NOT EXISTS rooms (
    id BIGSERIAL PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type VARCHAR(50) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    capacity INTEGER NOT NULL,
    amenities VARCHAR(1000),
    floor_number INTEGER NOT NULL,
    view VARCHAR(50),
    smoking_allowed BOOLEAN NOT NULL DEFAULT false,
    pet_friendly BOOLEAN NOT NULL DEFAULT false,
    area DECIMAL(8,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_room_number ON rooms(room_number);
CREATE INDEX IF NOT EXISTS idx_room_type ON rooms(room_type);
CREATE INDEX IF NOT EXISTS idx_room_status ON rooms(status);
