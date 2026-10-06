-- The zone a notification's subject date reads in, frozen with it at fan-out (docs/LEDGER_*.md
-- API-60): texts, webhooks and clients write the rendezvous in the subject's own zone.
--
-- Nullable, no default, no backfill: the previous release, still running for the minute of a
-- start-first deploy, does not map the column and leaves it null on what it fans out, as on every
-- row before it. Readers fall back on Paris (texts, when every team was Paris), the team's zone
-- (webhooks) or the reader's (clients).
ALTER TABLE notification_events ADD COLUMN subject_timezone varchar(64);
