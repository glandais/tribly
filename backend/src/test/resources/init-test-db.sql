-- Dev Services init script (test profile only): the test DB is drop-and-create,
-- durability is irrelevant. Disabling fsync/synchronous_commit/full_page_writes
-- removes per-commit and per-TRUNCATE disk waits (these are SIGHUP-context, so
-- pg_reload_conf() applies them without restart).
ALTER SYSTEM SET fsync = off;
ALTER SYSTEM SET synchronous_commit = off;
ALTER SYSTEM SET full_page_writes = off;
ALTER SYSTEM SET checkpoint_timeout = '30min';
SELECT pg_reload_conf();
