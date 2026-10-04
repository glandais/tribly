-- docs/LEDGER_*.md API-71: AuthTokenType gained EMAIL_CHANGE without a migration, so every
-- POST /api/auth/email/change-request failed on this constraint (500). Tests never saw it: they
-- build the schema from the entities, not from Flyway. Widening only — the previous release, which
-- never writes EMAIL_CHANGE either, keeps working during the rolling update.
ALTER TABLE auth_tokens
    DROP CONSTRAINT IF EXISTS auth_tokens_token_type_check;

ALTER TABLE auth_tokens
    ADD CONSTRAINT auth_tokens_token_type_check
    CHECK (token_type IN ('EMAIL_VERIFICATION', 'OTP', 'PASSWORD_RESET', 'EMAIL_CHANGE'));
