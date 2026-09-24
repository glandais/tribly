-- Test profile runs with schema-management.strategy=drop-and-create and no Flyway, so database
-- objects Hibernate does not create must be declared here too. Mirrors V23__route_mvt_row_type.sql.
-- Hibernate splits this file on semicolons, so each statement must be a single semicolon-free
-- statement: no DO blocks, no dollar quoting. DROP first, since the type outlives drop-and-create.
DROP TYPE IF EXISTS route_mvt_row CASCADE;
CREATE TYPE route_mvt_row AS (geom geometry, slug text, name text, team_slug text, distance real, elevation_gain real);
-- Mirrors the partial unique index of V43__biketeam_live_migration.sql: one active job per biketeam
-- team. Hibernate cannot express a partial index, and the trigger's 409 relies on it.
CREATE UNIQUE INDEX uk_biketeam_migrations_active ON biketeam_migrations (biketeam_team_id) WHERE status IN ('QUEUED', 'RUNNING');
