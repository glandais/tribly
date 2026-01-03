-- Add enable_ads column to teams table
ALTER TABLE teams ADD COLUMN IF NOT EXISTS enable_ads BOOLEAN NOT NULL DEFAULT TRUE;

-- Add new columns to team_entities for Ad type
ALTER TABLE team_entities ADD COLUMN IF NOT EXISTS price DECIMAL(10,2);
ALTER TABLE team_entities ADD COLUMN IF NOT EXISTS ad_type VARCHAR(20);
ALTER TABLE team_entities ADD COLUMN IF NOT EXISTS rental_period VARCHAR(20);
ALTER TABLE team_entities ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION;
ALTER TABLE team_entities ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;
ALTER TABLE team_entities ADD COLUMN IF NOT EXISTS location_description VARCHAR(255);

-- Add index for ad listings
CREATE INDEX IF NOT EXISTS idx_team_entities_ads
  ON team_entities (entity_type, deleted, team_id, ad_type)
  WHERE entity_type = 7;
