-- A rendered map thumbnail (gpx-previews/{id}/thumbnail-light.png) generated at create/update time
-- and served through imgproxy for Open Graph link previews. The flag records whether that PNG was
-- successfully produced and stored: previews created before this column, or whose rendering failed,
-- keep it false and fall back to the site-wide default OG image.
alter table gpx_previews
    add column has_thumbnail boolean not null default false;
