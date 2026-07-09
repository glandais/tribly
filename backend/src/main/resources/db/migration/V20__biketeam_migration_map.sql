CREATE TABLE biketeam_migration_map (
  entity_type VARCHAR(40)  NOT NULL,
  biketeam_id VARCHAR(255) NOT NULL,
  tribly_id   BIGINT       NOT NULL,
  synced_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  PRIMARY KEY (entity_type, biketeam_id)
);

CREATE INDEX idx_biketeam_migration_map_target
  ON biketeam_migration_map(entity_type, tribly_id);
