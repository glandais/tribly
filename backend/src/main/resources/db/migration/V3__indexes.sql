-- Teams table indexes for search performance
-- GIN index on name for fast partial matching
CREATE INDEX idx_teams_name_gin_trgm ON teams USING GIN (name gin_trgm_ops);

-- Team entities table indexes for search performance
-- GIN index on name for fast partial matching (rides, posts, routes)
CREATE INDEX idx_team_entities_name_gin_trgm ON team_entities USING GIN (name gin_trgm_ops);

-- GIN index on description for fast partial matching
CREATE INDEX idx_team_entities_markdown_gin_trgm ON team_entities USING GIN (markdown gin_trgm_ops);

-- Note: These indexes dramatically improve LIKE '%search%' query performance
-- from O(n) table scan to O(log n) index lookup

CREATE INDEX idx_gpx_tracks_geometry
    ON gpx_tracks
    USING GIST (geometry);

CREATE INDEX idx_places_geometry
    ON places
    USING GIST (geometry);

CREATE INDEX idx_team_entities_start
    ON team_entities
    USING GIST ("start");

CREATE INDEX idx_team_entities_end
    ON team_entities
    USING GIST ("end");
