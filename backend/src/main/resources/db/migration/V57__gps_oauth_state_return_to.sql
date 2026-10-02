-- docs/LEDGER_*.md API-63: where the OAuth callback sends the browser back to. Nullable, read as
-- PROFILE: during a start-first deploy the previous release still inserts rows without it.
alter table gps_oauth_states add column return_to varchar(20);
