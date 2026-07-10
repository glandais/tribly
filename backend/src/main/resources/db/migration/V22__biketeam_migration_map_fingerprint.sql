-- Digest of the biketeam source file a row was last built from, when it has one. Only routes use it
-- today: it lets a replay skip the whole GPX pipeline (parse, SRTM, simplify, FIT, two thumbnails,
-- five S3 uploads) for a map whose .gpx has not changed. NULL means "never fully processed", so the
-- replay redoes the work — which is what a run interrupted mid-upload needs.
ALTER TABLE biketeam_migration_map
  ADD COLUMN source_fingerprint VARCHAR(64);
