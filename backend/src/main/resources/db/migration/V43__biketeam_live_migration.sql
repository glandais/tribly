-- Biketeam live migration, team by team. See docs/plans/2026-09-22-biketeam-live-migration.md §8.1.

-- One row per confirmed biketeam request. The row is the grant (hashed) and, once biketeam has
-- redeemed it, the job queue — the same "row is the queue" shape as user_exports.
create table biketeam_migrations
(
    id                 bigint                      not null,
    domain_id          bigint                      not null,
    user_id            bigint                      not null,
    biketeam_team_id   varchar(255)                not null,
    biketeam_team_name varchar(250)                not null,
    request_id         varchar(64)                 not null,
    dry_run            boolean                     not null,
    reset              boolean                     not null,
    return_url         varchar(1000)               not null,
    base_url           varchar(500)                not null, -- domains.base_url at confirm time
    grant_hash         varchar(100)                not null,
    grant_expires_at   timestamp(6) with time zone not null,
    status             varchar(20)                 not null, -- GRANTED, EXPIRED, QUEUED, RUNNING, SUCCEEDED, FAILED
    attempts           integer                     not null default 0,
    next_attempt_at    timestamp(6) with time zone,
    heartbeat_at       timestamp(6) with time zone,
    progress           jsonb,
    target_team_id     bigint,
    result             jsonb,                                  -- counts, warnings, urlMap
    error_code         varchar(60),
    error_message      varchar(1000),
    queued_at          timestamp(6) with time zone,
    started_at         timestamp(6) with time zone,
    finished_at        timestamp(6) with time zone,
    created_at         timestamp(6) with time zone not null,
    updated_at         timestamp(6) with time zone not null,
    version            bigint,
    primary key (id)
);
alter table biketeam_migrations add constraint uk_biketeam_migrations_request unique (request_id);
alter table biketeam_migrations add constraint uk_biketeam_migrations_grant unique (grant_hash);
alter table biketeam_migrations add constraint fk_biketeam_migrations_domain foreign key (domain_id) references domains;
alter table biketeam_migrations add constraint fk_biketeam_migrations_user foreign key (user_id) references users on delete cascade;
alter table biketeam_migrations add constraint fk_biketeam_migrations_team foreign key (target_team_id) references teams;
-- One active job per biketeam team, whatever the domain.
create unique index uk_biketeam_migrations_active on biketeam_migrations (biketeam_team_id)
    where status in ('QUEUED', 'RUNNING');
create index idx_biketeam_migrations_status on biketeam_migrations (status, next_attempt_at);

-- Which biketeam team a mapping row came from, so a reset can forget exactly that team. NULL on the
-- rows the legacy import wrote; the live path fills it on every upsert.
alter table biketeam_migration_map add column biketeam_team_id varchar(255);
create index idx_biketeam_migration_map_team on biketeam_migration_map (biketeam_team_id);
