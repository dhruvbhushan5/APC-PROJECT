-- Payment Service Database Schema
-- V1__Create_payments_table.sql

CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100) UNIQUE,
    description VARCHAR(500),
    payment_date TIMESTAMP,
    gateway_transaction_id VARCHAR(100),
    gateway_name VARCHAR(50),
    gateway_response VARCHAR(1000),
    gateway_status VARCHAR(50),
    refund_amount DECIMAL(10,2),
    refund_date TIMESTAMP,
    refund_reason VARCHAR(500),
    card_last_four_digits VARCHAR(20),
    card_type VARCHAR(50),
    customer_name VARCHAR(100),
    customer_email VARCHAR(100),
    failure_code VARCHAR(100),
    failure_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_payment_booking ON payments(booking_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payment_method ON payments(payment_method);
CREATE INDEX IF NOT EXISTS idx_payment_transaction ON payments(transaction_id);
CREATE INDEX IF NOT EXISTS idx_payment_date ON payments(payment_date);
