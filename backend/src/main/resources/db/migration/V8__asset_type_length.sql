-- Increase asset type column length to accommodate ROUTE_THUMBNAIL_LIGHT (21 chars)
ALTER TABLE assets ALTER COLUMN type TYPE varchar(25);
