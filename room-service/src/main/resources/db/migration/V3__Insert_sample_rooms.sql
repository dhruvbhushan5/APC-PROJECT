-- Room Service Database Schema
-- V3__Insert_sample_rooms.sql

INSERT INTO rooms (
    room_number, room_type, price_per_night, status, description, 
    capacity, amenities, floor_number, view, smoking_allowed, pet_friendly, area
) VALUES 
-- Standard Rooms
('101', 'STANDARD', 150.00, 'AVAILABLE', 'Comfortable standard room with essential amenities', 2, 'WiFi, AC, TV, Mini-fridge', 1, 'Garden', false, false, 25.5),
('102', 'STANDARD', 150.00, 'AVAILABLE', 'Comfortable standard room with essential amenities', 2, 'WiFi, AC, TV, Mini-fridge', 1, 'Garden', false, false, 25.5),
('103', 'STANDARD', 150.00, 'OCCUPIED', 'Comfortable standard room with essential amenities', 2, 'WiFi, AC, TV, Mini-fridge', 1, 'Parking', false, false, 25.5),

-- Deluxe Rooms
('201', 'DELUXE', 250.00, 'AVAILABLE', 'Spacious deluxe room with premium amenities', 3, 'WiFi, AC, TV, Mini-bar, Coffee machine, Balcony', 2, 'City', false, true, 35.0),
('202', 'DELUXE', 250.00, 'AVAILABLE', 'Spacious deluxe room with premium amenities', 3, 'WiFi, AC, TV, Mini-bar, Coffee machine, Balcony', 2, 'Ocean', false, true, 35.0),
('203', 'DELUXE', 250.00, 'MAINTENANCE', 'Spacious deluxe room with premium amenities', 3, 'WiFi, AC, TV, Mini-bar, Coffee machine, Balcony', 2, 'Ocean', false, true, 35.0),

-- Suite Rooms
('301', 'SUITE', 450.00, 'AVAILABLE', 'Luxurious suite with separate living area', 4, 'WiFi, AC, TV, Mini-bar, Coffee machine, Balcony, Living room, Jacuzzi', 3, 'Ocean', false, true, 60.0),
('302', 'SUITE', 450.00, 'RESERVED', 'Luxurious suite with separate living area', 4, 'WiFi, AC, TV, Mini-bar, Coffee machine, Balcony, Living room, Jacuzzi', 3, 'Ocean', false, true, 60.0),

-- Presidential Suite
('401', 'PRESIDENTIAL', 800.00, 'AVAILABLE', 'Presidential suite with premium services', 6, 'WiFi, AC, TV, Mini-bar, Coffee machine, Balcony, Living room, Jacuzzi, Butler service, Kitchen', 4, 'Ocean', true, true, 120.0),

-- Family Rooms
('105', 'FAMILY', 200.00, 'AVAILABLE', 'Family room with bunk beds for children', 5, 'WiFi, AC, TV, Mini-fridge, Bunk beds, Play area', 1, 'Garden', false, true, 40.0),
('205', 'FAMILY', 200.00, 'AVAILABLE', 'Family room with bunk beds for children', 5, 'WiFi, AC, TV, Mini-fridge, Bunk beds, Play area', 2, 'City', false, true, 40.0);
