-- The optional average speed of a trip stage, in km/h, like ride_groups.average_speed. A nullable
-- column only: the previous release, which never maps it, keeps running beside this one during a
-- start-first deploy.
ALTER TABLE team_entities
    ADD COLUMN average_speed float4;
