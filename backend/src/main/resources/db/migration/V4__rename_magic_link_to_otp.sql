-- Rename MAGIC_LINK to OTP in auth_tokens table
-- This is a simple value rename, no schema change needed since it's a check constraint

-- Drop the old constraint first
ALTER TABLE auth_tokens
    DROP CONSTRAINT IF EXISTS auth_tokens_token_type_check;

-- Update existing MAGIC_LINK tokens to OTP (before adding new constraint)
UPDATE auth_tokens
SET token_type = 'OTP'
WHERE token_type = 'MAGIC_LINK';

-- Add the new constraint with OTP instead of MAGIC_LINK
ALTER TABLE auth_tokens
    ADD CONSTRAINT auth_tokens_token_type_check
    CHECK (token_type IN ('EMAIL_VERIFICATION', 'OTP', 'PASSWORD_RESET'));
