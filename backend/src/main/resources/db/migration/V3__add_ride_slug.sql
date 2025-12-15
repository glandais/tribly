-- Flyway Migration V3: Add slug column to rides table
-- Slug is unique per team for human-readable URLs

-- Add slug column to rides (nullable initially)
ALTER TABLE rides ADD COLUMN slug VARCHAR(100);

-- Generate slugs for existing rides from title
-- Convert to lowercase, replace spaces/special chars with hyphens
UPDATE rides SET slug = LOWER(REGEXP_REPLACE(REGEXP_REPLACE(title, '[^a-zA-Z0-9]+', '-', 'g'), '^-|-$', '', 'g')) || '-' || id
WHERE slug IS NULL;

-- Make column NOT NULL
ALTER TABLE rides ALTER COLUMN slug SET NOT NULL;

-- Add unique constraint for team + slug combination
ALTER TABLE rides ADD CONSTRAINT uk_rides_team_slug UNIQUE (team_id, slug);
