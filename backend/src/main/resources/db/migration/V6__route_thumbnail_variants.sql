-- Widen type column for new asset type names (ROUTE_THUMBNAIL_LIGHT = 21 chars)
ALTER TABLE assets ALTER COLUMN type TYPE varchar(30);

-- Delete legacy ROUTE_THUMBNAIL assets (replaced by light/dark variants)
DELETE FROM assets WHERE type = 'ROUTE_THUMBNAIL';

-- Drop existing check constraint and add updated one with new thumbnail types
-- The constraint was created inline in V2 and Hibernate names it based on the column
ALTER TABLE assets DROP CONSTRAINT IF EXISTS assets_type_check;
ALTER TABLE assets ADD CONSTRAINT assets_type_check
  CHECK (type IN (
    'LOGO', 'IMAGE', 'ATTACHMENT',
    'ROUTE_ORIGINAL_GPX', 'ROUTE_FILTERED_GPX', 'ROUTE_FIT',
    'ROUTE_THUMBNAIL_LIGHT', 'ROUTE_THUMBNAIL_DARK'
  ));
