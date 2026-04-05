-- Increase password hash column lengths to be algorithm-agnostic (e.g. Argon2 hashes are longer)
ALTER TABLE users
  ALTER COLUMN password_hash TYPE VARCHAR(255);

ALTER TABLE auth_tokens
  ALTER COLUMN pending_password_hash TYPE VARCHAR(255);
