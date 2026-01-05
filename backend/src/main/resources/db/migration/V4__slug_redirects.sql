-- Team slug redirects (globally unique)
CREATE TABLE team_slug_redirects (
    id bigint NOT NULL PRIMARY KEY,
    old_slug varchar(250) NOT NULL UNIQUE,
    team_id bigint NOT NULL REFERENCES teams(id),
    created_at timestamptz NOT NULL DEFAULT now()
);

-- TeamEntity slug redirects (unique per team + entity_type)
CREATE TABLE team_entity_slug_redirects (
    id bigint NOT NULL PRIMARY KEY,
    old_slug varchar(250) NOT NULL,
    team_id bigint NOT NULL REFERENCES teams(id),
    entity_type integer NOT NULL,
    entity_id bigint NOT NULL REFERENCES team_entities(id),
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT uk_team_entity_slug_redirect UNIQUE (team_id, entity_type, old_slug)
);

CREATE INDEX idx_team_entity_slug_redirects ON team_entity_slug_redirects(team_id, entity_type, old_slug);
