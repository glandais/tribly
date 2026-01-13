-- OIDC DB Token State Manager table
-- Stores OIDC tokens server-side instead of in cookies
CREATE TABLE oidc_db_token_state_manager (
    id VARCHAR(255) PRIMARY KEY,
    id_token TEXT,
    access_token TEXT,
    refresh_token TEXT,
    expires_in BIGINT,
    access_token_expires_in BIGINT,
    access_token_scope TEXT
);
