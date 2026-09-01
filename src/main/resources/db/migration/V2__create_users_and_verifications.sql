-- V2__add_verification_and_user_status.sql

-- 1. Tambahkan kolom baru ke tabel users yang sudah ada
ALTER TABLE users
    ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- 2. Buat tabel baru verification_codes
CREATE TABLE verification_codes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);