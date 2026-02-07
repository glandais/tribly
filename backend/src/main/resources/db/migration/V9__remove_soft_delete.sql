-- Remove soft-delete (deleted column) from entities that now use hard delete.
-- Entities keeping soft-delete: team_entities, teams, users, domains.

-- =====================================================
-- 1. Hard-delete all rows that were soft-deleted
-- =====================================================

-- Ride participations in deleted groups + deleted participations
DELETE FROM ride_participations WHERE ride_group_id IN (SELECT id FROM ride_groups WHERE deleted = true);
DELETE FROM ride_participations WHERE deleted = true;

-- Ride groups
DELETE FROM ride_groups WHERE deleted = true;

-- Trip participations
DELETE FROM trip_participations WHERE deleted = true;

-- Comments (recursive: children of deleted parents, then deleted comments)
WITH RECURSIVE deleted_tree AS (
    SELECT id FROM comments WHERE deleted = true
    UNION ALL
    SELECT c.id FROM comments c JOIN deleted_tree dt ON c.parent_id = dt.id
)
DELETE FROM comments WHERE id IN (SELECT id FROM deleted_tree);

-- Assets
DELETE FROM assets WHERE deleted = true;

-- GPX tracks & waypoints
DELETE FROM gpx_tracks WHERE deleted = true;
DELETE FROM gpx_waypoints WHERE deleted = true;

-- Ride template groups in deleted templates + deleted groups
DELETE FROM ride_template_groups WHERE template_id IN (SELECT id FROM ride_templates WHERE deleted = true);
DELETE FROM ride_template_groups WHERE deleted = true;
DELETE FROM ride_templates WHERE deleted = true;

-- Places: nullify FK references from team_entities first
UPDATE team_entities SET place_start_id = NULL WHERE place_start_id IN (SELECT id FROM places WHERE deleted = true);
UPDATE team_entities SET place_end_id = NULL WHERE place_end_id IN (SELECT id FROM places WHERE deleted = true);
DELETE FROM places WHERE deleted = true;

-- Calendar tokens
DELETE FROM calendar_tokens WHERE deleted = true;

-- Passkeys
DELETE FROM passkeys WHERE deleted = true;

-- GPS service connections
DELETE FROM gps_service_connections WHERE deleted = true;

-- User teams
DELETE FROM user_teams WHERE deleted = true;

-- =====================================================
-- 2. Drop old indexes that include deleted
-- =====================================================

DROP INDEX IF EXISTS idx_assets_team_entity_deleted;
DROP INDEX IF EXISTS idx_assets_team_deleted;
DROP INDEX IF EXISTS IDXryn234ylmua6os660w5avbkgy;  -- comments (team_entity_id, deleted, created_at)
DROP INDEX IF EXISTS IDXg9o7ansoiy9pm6nkpb9brn44o;  -- comments (parent_id, deleted)
DROP INDEX IF EXISTS idx_gpx_tracks_route_deleted;
DROP INDEX IF EXISTS idx_gpx_waypoints_route_deleted;
DROP INDEX IF EXISTS idx_places_team_deleted;
DROP INDEX IF EXISTS IDXe5fskel3tfc9ce4a4xu3vn4f5;  -- ride_groups (ride_id, deleted)
DROP INDEX IF EXISTS idx_ride_participations_user_group_deleted;
DROP INDEX IF EXISTS IDXi9tkt14pnxn8jlc7a8tnijxpt;  -- ride_template_groups (template_id, deleted)
DROP INDEX IF EXISTS IDXcqoh6mr47q7qjqrkrg959v4ue;  -- ride_templates (team_id, deleted)
DROP INDEX IF EXISTS IDXldn4q7s3s14kl5v91wx6fclw6;  -- ride_templates (team_id, slug, deleted)
DROP INDEX IF EXISTS idx_trip_participations_user_trip_deleted;
DROP INDEX IF EXISTS idx_user_teams_user_team_deleted;

-- =====================================================
-- 3. Drop deleted column
-- =====================================================

ALTER TABLE assets DROP COLUMN deleted;
ALTER TABLE calendar_tokens DROP COLUMN deleted;
ALTER TABLE comments DROP COLUMN deleted;
ALTER TABLE gps_service_connections DROP COLUMN deleted;
ALTER TABLE gpx_tracks DROP COLUMN deleted;
ALTER TABLE gpx_waypoints DROP COLUMN deleted;
ALTER TABLE passkeys DROP COLUMN deleted;
ALTER TABLE places DROP COLUMN deleted;
ALTER TABLE ride_groups DROP COLUMN deleted;
ALTER TABLE ride_participations DROP COLUMN deleted;
ALTER TABLE ride_template_groups DROP COLUMN deleted;
ALTER TABLE ride_templates DROP COLUMN deleted;
ALTER TABLE trip_participations DROP COLUMN deleted;
ALTER TABLE user_teams DROP COLUMN deleted;

-- =====================================================
-- 4. Create new indexes without deleted
-- =====================================================

CREATE INDEX idx_assets_team_entity ON assets (team_entity_id);
CREATE INDEX idx_assets_team ON assets (team_id);
CREATE INDEX IDXnelhrd0j62l4s3uo65v7gqv0c ON comments (team_entity_id, created_at);
CREATE INDEX IDX29ieo90ejocayoeb49npnud06 ON comments (parent_id);
CREATE INDEX idx_gpx_tracks_route ON gpx_tracks (route_id);
CREATE INDEX idx_gpx_waypoints_route ON gpx_waypoints (route_id);
CREATE INDEX idx_places_team ON places (team_id);
CREATE INDEX IDXiqqw4vnlra5w75y0a9lw34at5 ON ride_groups (ride_id);
CREATE INDEX idx_ride_participations_user_group ON ride_participations (user_id, ride_group_id);
CREATE INDEX IDXi387ucrpfs899m8ek9ellpo8o ON ride_template_groups (template_id);
CREATE INDEX IDXphcm5ndcqeqcjgyfhf07iklaq ON ride_templates (team_id);
CREATE INDEX IDXmgwfmota9i0gnxc0eukxl6kcr ON ride_templates (team_id, slug);
CREATE INDEX idx_trip_participations_user_trip ON trip_participations (user_id, trip_id);
CREATE INDEX idx_user_teams_user_team ON user_teams (user_id, team_id);
