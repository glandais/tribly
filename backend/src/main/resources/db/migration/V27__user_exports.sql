-- Self-service GDPR data export (DSAR). The row IS the job queue: POST /api/users/me/export
-- inserts a PENDING row and returns 202, a scheduler claims it with a compare-and-set on status,
-- builds the ZIP outside any transaction, stores it and emails a download link.
--
-- download_token_hash, not the token: the plaintext is a bearer credential for the user's entire
-- personal data set and exists only inside one email — the same treatment as auth_tokens.token_hash
-- and social_login_codes.code_hash, and unlike calendar_tokens.token, which is plaintext only
-- because the UI has to display it again.
--
-- base_url/site_name are snapshotted at request time. The scheduler runs with no HTTP request, so
-- DomainResolver (@RequestScoped, injects RoutingContext) is unavailable; and domains.base_url
-- would be the *parent* URL for a request that arrived on a domain alias — see ResolvedSite.ofAlias,
-- which takes host/baseUrl/name from the alias.
create table user_exports
(
    id                  bigint                      not null,
    domain_id           bigint                      not null,
    user_id             bigint                      not null,
    status              varchar(20)                 not null,
    requested_at        timestamp(6) with time zone not null,
    started_at          timestamp(6) with time zone,
    completed_at        timestamp(6) with time zone,
    expires_at          timestamp(6) with time zone,
    email_sent_at       timestamp(6) with time zone,
    storage_key         varchar(300),
    file_size           bigint,
    download_token_hash varchar(100),
    attempts            integer                     not null,
    error_message       varchar(500),
    base_url            varchar(500)                not null,
    site_name           varchar(250)                not null,
    language            varchar(5)                  not null,
    created_at          timestamp(6) with time zone not null,
    updated_at          timestamp(6) with time zone not null,
    version             bigint,
    primary key (id)
);

alter table user_exports
    add constraint uk_user_exports_token_hash unique (download_token_hash);
alter table user_exports
    add constraint fk_user_exports_domain foreign key (domain_id) references domains;
alter table user_exports
    add constraint fk_user_exports_user foreign key (user_id) references users on delete cascade;

-- Cooldown check and "my latest export" lookup.
create index idx_user_exports_user_requested on user_exports (user_id, requested_at desc);
-- The poller's claim query.
create index idx_user_exports_status_requested on user_exports (status, requested_at);
-- The retention sweep.
create index idx_user_exports_expires_at on user_exports (expires_at) where storage_key is not null;

-- At most one in-flight job per user. UserExportService checks this in Java too — tests build the
-- schema from the JPA mappings, which cannot express a partial index — so this is purely the
-- backstop for two concurrent POSTs slipping through the read-then-insert window.
create unique index uk_user_exports_active on user_exports (user_id)
    where status in ('PENDING', 'PROCESSING');

-- The export walks every user-owned table by created_by_id. These two are the ones that grow
-- without bound and have no index on it (assets carries only idx_assets_team_entity and
-- idx_assets_team), so an export would otherwise cost two sequential scans of the largest tables.
create index idx_assets_created_by on assets (created_by_id);
create index idx_team_entities_created_by on team_entities (created_by_id);
