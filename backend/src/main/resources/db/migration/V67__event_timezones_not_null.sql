-- Version N+1 of the event timezones (docs/LEDGER_*.md API-60, plan §8): every team entity has its
-- zone and every ride group its start_at, so both become NOT NULL.
--
-- The last catch-up first, for the rows the previous release could have left null. It cannot: since
-- V64 it writes both on every save (the zone from the constructor, start_at with the groups), and
-- EventTimezoneBackfill filled the older rows at startup — production and staging held none on
-- 2026-10-07. Kept as the safety net the NOT NULL needs, in SQL and with the team's zone: the
-- formula of V64, without its exclusion of DST-transition days (a group on such a day takes the
-- later offset of an ambiguous wall time, an hour off at worst, on a row that does not exist).
--
-- Safe under a start-first deploy: the previous release writes both columns on every insert and
-- update, so the constraints never refuse it.
--
-- ride_groups.time stays: the previous release still maps it for the minute of the deploy (dropping
-- it would fail every query it makes on ride_groups), and this release keeps writing it — never
-- reading it — so that a rollback finds it right. Dropped by the next version (API-60, lot 6).
UPDATE team_entities te
SET timezone = t.timezone
FROM teams t
WHERE t.id = te.team_id
  AND te.timezone IS NULL;

UPDATE ride_groups g
SET start_at = CASE
                   WHEN g.time IS NULL THEN te.date_time
                   ELSE (cast(te.date_time AT TIME ZONE te.timezone AS date) + g.time)
                       AT TIME ZONE te.timezone
    END
FROM team_entities te
WHERE te.id = g.ride_id
  AND g.start_at IS NULL;

ALTER TABLE team_entities ALTER COLUMN timezone SET NOT NULL;
ALTER TABLE ride_groups ALTER COLUMN start_at SET NOT NULL;
