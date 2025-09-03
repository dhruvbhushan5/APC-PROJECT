-- Insert default admin user
INSERT INTO users (
    id,
    first_name,
    last_name,
    email,
    password,
    phone_number,
    address,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    email_verified
) VALUES (
    uuid_generate_v4(),
    'Admin',
    'User',
    'admin@hotelreservation.com',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- password: admin123
    '+1234567890',
    '123 Admin Street, Admin City',
    true,
    true,
    true,
    true,
    true
);

-- Get the admin user ID for role assignment
WITH admin_user AS (
    SELECT id FROM users WHERE email = 'admin@hotelreservation.com'
)
INSERT INTO user_role_assignments (
    user_id,
    role,
    assigned_by,
    active
)
SELECT 
    admin_user.id,
    'ADMIN',
    'SYSTEM',
    true
FROM admin_user;

-- Insert sample staff user
INSERT INTO users (
    id,
    first_name,
    last_name,
    email,
    password,
    phone_number,
    address,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    email_verified
) VALUES (
    uuid_generate_v4(),
    'Staff',
    'Member',
    'staff@hotelreservation.com',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- password: admin123
    '+1234567891',
    '456 Staff Avenue, Staff City',
    true,
    true,
    true,
    true,
    true
);

-- Get the staff user ID for role assignment
WITH staff_user AS (
    SELECT id FROM users WHERE email = 'staff@hotelreservation.com'
)
INSERT INTO user_role_assignments (
    user_id,
    role,
    assigned_by,
    active
)
SELECT 
    staff_user.id,
    'STAFF',
    'SYSTEM',
    true
FROM staff_user;

-- Insert sample customer user
INSERT INTO users (
    id,
    first_name,
    last_name,
    email,
    password,
    phone_number,
    address,
    enabled,
    account_non_expired,
    account_non_locked,
    credentials_non_expired,
    email_verified
) VALUES (
    uuid_generate_v4(),
    'John',
    'Doe',
    'customer@hotelreservation.com',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', -- password: admin123
    '+1234567892',
    '789 Customer Road, Customer City',
    true,
    true,
    true,
    true,
    true
);

-- Get the customer user ID for role assignment
WITH customer_user AS (
    SELECT id FROM users WHERE email = 'customer@hotelreservation.com'
)
INSERT INTO user_role_assignments (
    user_id,
    role,
    assigned_by,
    active
)
SELECT 
    customer_user.id,
    'CUSTOMER',
    'SYSTEM',
    true
FROM customer_user;
