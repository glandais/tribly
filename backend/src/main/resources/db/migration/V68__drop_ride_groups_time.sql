-- The last step of the event timezones (docs/LEDGER_*.md API-60, lot 7): a group's departure is
-- ride_groups.start_at alone since V67, and its own wall time is read back from it.
--
-- Safe under a start-first deploy only because the previous release (lot 6) no longer maps the
-- column: dropped while a release still mapped it, it would have failed every query that release
-- made on ride_groups.
ALTER TABLE ride_groups DROP COLUMN time;
