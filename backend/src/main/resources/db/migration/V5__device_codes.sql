-- Device codes for OAuth device flow (RFC 8628)
-- Used by GPS devices (Karoo, Garmin) that can't handle browser-based auth

CREATE TABLE device_codes (
    id BIGINT PRIMARY KEY,
    device_code_hash VARCHAR(100) NOT NULL UNIQUE,
    user_code VARCHAR(10) NOT NULL UNIQUE,
    client_id VARCHAR(50) NOT NULL,
    domain_id BIGINT NOT NULL REFERENCES domains(id),
    user_id BIGINT REFERENCES users(id),
    authorized BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Index for polling by device code hash
CREATE INDEX idx_device_codes_device_code_hash ON device_codes(device_code_hash);

-- Index for user code lookup during verification
CREATE INDEX idx_device_codes_user_code ON device_codes(user_code);

-- Index for cleanup of expired codes
CREATE INDEX idx_device_codes_expires_at ON device_codes(expires_at);
