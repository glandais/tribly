-- Enable pg_trgm extension for trigram-based text search
-- This extension provides operators and indexes for fast partial text matching
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Teams table indexes for search performance
-- GIN index on name for fast partial matching
CREATE INDEX idx_teams_name_gin_trgm ON teams USING GIN (name gin_trgm_ops);

-- GIN index on description for fast partial matching
CREATE INDEX idx_teams_description_gin_trgm ON teams USING GIN (description gin_trgm_ops);

-- Team entities table indexes for search performance
-- GIN index on name for fast partial matching (rides, posts, routes)
CREATE INDEX idx_team_entities_name_gin_trgm ON team_entities USING GIN (name gin_trgm_ops);

-- GIN index on description for fast partial matching
CREATE INDEX idx_team_entities_description_gin_trgm ON team_entities USING GIN (description gin_trgm_ops);

-- Note: These indexes dramatically improve LIKE '%search%' query performance
-- from O(n) table scan to O(log n) index lookup
