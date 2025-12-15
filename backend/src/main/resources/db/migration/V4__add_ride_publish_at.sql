-- Add publish_at column for scheduled auto-publication of rides
ALTER TABLE rides ADD COLUMN publish_at TIMESTAMP;

-- Index for efficient scheduler queries (DRAFT rides with publish_at set)
CREATE INDEX idx_rides_publish_at ON rides (status, publish_at) WHERE deleted = false AND publish_at IS NOT NULL;
