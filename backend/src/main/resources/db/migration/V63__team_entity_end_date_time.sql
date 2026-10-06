-- When a ride, a trip or a trip stage is over (docs/LEDGER_*.md API-85). Nullable and without
-- constraint: the previous release, which never maps it, keeps running beside this one during a
-- start-first deploy. The precise value needs Java (route distances, group speeds), so it is filled
-- by PublicationEndBackfill at startup, and every query reads
-- coalesce(end_date_time, date_time + the default duration) meanwhile.
ALTER TABLE team_entities
    ADD COLUMN end_date_time timestamp(6) with time zone;

CREATE INDEX idx_team_entities_team_end ON team_entities (team_id, end_date_time);
