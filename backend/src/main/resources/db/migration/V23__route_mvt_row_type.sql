-- Composite type used as the row argument of ST_AsMVT when building route vector tiles.
-- Column names become the MVT feature properties; `geom` first so ST_AsMVT picks it up
-- as the geometry without an explicit geom_name argument.
-- Kept in sync with backend/src/main/resources/import-test.sql (tests run without Flyway).
CREATE TYPE route_mvt_row AS
(
    geom           geometry,
    slug           text,
    name           text,
    team_slug      text,
    distance       real,
    elevation_gain real
);
