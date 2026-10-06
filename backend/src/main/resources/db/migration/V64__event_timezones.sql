-- Event timezones, version N (docs/LEDGER_*.md API-60, docs/plans/2026-10-06-event-timezones.md §8).
--
-- Safe under a start-first deploy: the previous release maps none of the new columns.
-- teams.timezone has a default, so its inserts still succeed; team_entities.timezone and
-- ride_groups.start_at stay nullable for one version, and the rows the previous release writes
-- meanwhile are filled by EventTimezoneBackfill at startup (readers fall back on the team's zone
-- and on ride_groups.time until then). users.timezone only widens.

ALTER TABLE teams
    ADD COLUMN timezone varchar(64) NOT NULL DEFAULT 'Europe/Paris';

-- The entry zone of each team entity: frozen when it is saved, never a cache.
ALTER TABLE team_entities
    ADD COLUMN timezone varchar(64);

-- A group's departure as an instant; ride_groups.time is kept (and still written) until N+1.
ALTER TABLE ride_groups
    ADD COLUMN start_at timestamp(6) with time zone;

ALTER TABLE users
    ALTER COLUMN timezone TYPE varchar(64);

-- Backfill (§8.2). Every production team is French, so the team's zone is every entity's zone.
UPDATE team_entities te
SET timezone = t.timezone
FROM teams t
WHERE t.id = te.team_id
  AND te.timezone IS NULL;

-- A group without a time leaves with its ride; otherwise its time is read on the ride's local date,
-- in the ride's zone. On a day with a DST transition this is NOT the formula of
-- RideWeatherCalculator.legStart: PostgreSQL gives an ambiguous wall time (the autumn overlap) the
-- offset in force after the transition, java.time the earlier one. Those groups are left null here,
-- their local day being shorter or longer than 24 h, and EventTimezoneBackfill fills them in Java at
-- startup, with legStart's semantics.
UPDATE ride_groups g
SET start_at = CASE
                   WHEN g.time IS NULL THEN te.date_time
                   ELSE (cast(te.date_time AT TIME ZONE te.timezone AS date) + g.time)
                       AT TIME ZONE te.timezone
    END
FROM team_entities te
WHERE te.id = g.ride_id
  AND g.start_at IS NULL
  AND (g.time IS NULL
    OR ((cast(te.date_time AT TIME ZONE te.timezone AS date) + 1) + time '00:00') AT TIME ZONE te.timezone
           - (cast(te.date_time AT TIME ZONE te.timezone AS date) + time '00:00') AT TIME ZONE te.timezone
           = interval '24 hours');

-- §8.3, deliberately not run: upcoming entities whose start place or route lies in another zone
-- than their team's would need timeshape (Java) to be re-resolved at constant instant. Expected to
-- be zero rows in production; measure it with (rides; trips and stages likewise through their
-- places and routes):
--   select te.id, te.name, p.geometry, r."start"
--   from team_entities te
--   left join places p on p.id = te.place_start_id
--   left join team_entities r on r.id = te.route_id and not r.deleted
--   where te.entity_type = 1 and not te.deleted and te.date_time > now()
--     and (p.geometry is not null or r."start" is not null);
-- and check the points it returns against the team's zone before writing any catch-up.
