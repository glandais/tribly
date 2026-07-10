-- GPX files uploaded through the GPX tools page: analysed, rendered, but attached to no team.
-- Purged after 30 days by GpxPreviewCleanupScheduler, which also removes the S3 objects
-- stored under gpx-previews/{id}/.
--
-- public_id, not the TSID, is what appears in URLs: TSIDs are sequential enough to enumerate,
-- and any authenticated user of the domain may open a preview by link.
create table gpx_previews
(
    id             bigint                      not null,
    public_id      uuid                        not null,
    domain_id      bigint                      not null,
    created_by_id  bigint                      not null,
    name           varchar(250)                not null,
    distance       float4                      not null,
    elevation_gain float4                      not null,
    elevation_loss float4                      not null,
    hilliness      float4                      not null,
    tracks         jsonb                       not null,
    waypoints      jsonb                       not null,
    created_at     timestamp(6) with time zone not null,
    updated_at     timestamp(6) with time zone not null,
    version        bigint,
    primary key (id)
);

alter table gpx_previews
    add constraint fk_gpx_previews_domain foreign key (domain_id) references domains;
alter table gpx_previews
    add constraint fk_gpx_previews_created_by foreign key (created_by_id) references users;

create unique index idx_gpx_previews_public_id on gpx_previews (public_id);
-- Scanned by the retention job.
create index idx_gpx_previews_created_at on gpx_previews (created_at);
