-- docs/LEDGER_*.md SEC-27 (audit M7): the refresh token is rotated at every refresh.
--
-- The session keeps the token it held before its last rotation, honoured for a short grace (the
-- other tabs, the SSR render, an app resuming), and when that rotation happened. Presented after the
-- grace, the previous token revokes the session. Both columns are nullable: the previous release,
-- which does not know them, still reads and writes sessions during a rolling deploy — it simply
-- never rotates.
alter table auth_sessions add column previous_refresh_token_hash varchar(100);
alter table auth_sessions add column rotated_at timestamp(6) with time zone;

-- Every refresh looks a session up by one of these two hashes; neither was indexed.
create index idx_auth_sessions_refresh_token_hash on auth_sessions (refresh_token_hash);
create index idx_auth_sessions_previous_refresh_token_hash
    on auth_sessions (previous_refresh_token_hash);
