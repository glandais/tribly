-- backend/src/main/resources/db/migration/V15__add_password_auth.sql

ALTER TABLE users
  ADD COLUMN password_hash VARCHAR(100);

ALTER TABLE auth_tokens
  ADD COLUMN pending_password_hash VARCHAR(100);
