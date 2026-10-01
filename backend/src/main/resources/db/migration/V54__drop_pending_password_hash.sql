-- docs/LEDGER_*.md API-58: auth_tokens.pending_password_hash has not been written since SEC-24
-- (7.0.0) nor mapped since API-57 (10.0.0). It is dropped one release after the field, never in
-- the same one: during a start-first deploy the previous release still writes every mapped column.
alter table auth_tokens drop column if exists pending_password_hash;
