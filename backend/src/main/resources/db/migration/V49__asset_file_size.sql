-- docs/LEDGER_*.md API-7: the size of an asset's stored file, exposed as AssetDto.size so a client
-- can show « PDF · 240 Ko ». Recorded on every write from now on; null for the rows written before,
-- which AssetSizeBackfill fills from the bucket (one HEAD each), and for any row a previous
-- release writes during a rolling deploy — the backfill picks those up too. Nullable for that
-- reason, and so the previous release, which does not know the column, can still insert.
alter table assets add column file_size bigint;

create index idx_assets_size_unknown on assets (id) where file_size is null;
