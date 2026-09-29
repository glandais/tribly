-- docs/LEDGER_*.md API-43: storage now strips images of their metadata (EXIF with the GPS
-- position, XMP, IPTC, comments) on write. The files stored before still have it; this flag marks
-- them for AssetMetadataBackfill, which rewrites them one batch at a time and clears it.
--
-- Every image, and every asset of a type users upload into (logos, images, attachments) whatever
-- its declared content type — the backfill reads the format from the bytes, so a JPEG stored under
-- a generic type (an attachment, an import whose type came from the file name) is found too. GPX
-- and FIT files are not images. New rows get false: the entity always writes it.
alter table assets add column metadata_pending boolean not null default false;

update assets set metadata_pending = true
where content_type like 'image/%' or type in ('LOGO', 'IMAGE', 'ATTACHMENT');

create index idx_assets_metadata_pending on assets (id) where metadata_pending;
