-- The stored end of a ride now starts from its groups' start_at (docs/LEDGER_*.md API-60, plan §4
-- « une seule source de fuseau par lieu »). Until now PublicationEndCalculator read a group's time
-- in the zone of the departure point, UTC for a ride with no point at all: those ends can be off by
-- the zone's offset. Clearing them hands them to PublicationEndBackfill, which recomputes them at
-- startup with the new rule.
--
-- Only rides not long over, and only those with a timed group (the others never read a zone).
-- Safe under a start-first deploy: every reader takes coalesce(end_date_time, date_time + 3 h)
-- meanwhile, and the version column is left alone.
UPDATE team_entities te
SET end_date_time = NULL
WHERE te.end_date_time > now() - interval '1 day'
  AND EXISTS (SELECT 1 FROM ride_groups g WHERE g.ride_id = te.id AND g.time IS NOT NULL);
