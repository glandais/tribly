-- In-app feedback and automatic error reports, published as issues of a private GitHub repository
-- by FeedbackGithubWorker. Rows are written first and published after: a report is never lost
-- because GitHub is slow, down, or not configured.

-- One distinct unhandled error, as ErrorFingerprint tells them apart. Not scoped to a domain: a bug
-- lives in the code, which every tenant shares. Its occurrences carry the domain.
create table error_signatures
(
    id                 bigint                      not null,
    fingerprint        varchar(64)                 not null,
    platform           varchar(20)                 not null,
    title              varchar(300)                not null,
    first_seen_at      timestamp(6) with time zone not null,
    last_seen_at       timestamp(6) with time zone not null,
    occurrence_count   bigint                      not null,
    versions           jsonb                       not null,
    github_status      varchar(20)                 not null,
    github_attempts    integer                     not null,
    github_issue_number integer,
    github_created_at  timestamp(6) with time zone,
    summarized_until   timestamp(6) with time zone,
    issue_closed       boolean                     not null,
    versions_at_close  jsonb                       not null,
    regression_version varchar(50),
    primary key (id)
);
alter table error_signatures
    add constraint uk_error_signatures_fingerprint unique (fingerprint);
create index idx_error_signatures_github_status on error_signatures (github_status, first_seen_at);

-- One automatic report. Stored redacted; purged after 90 days, and with the member's account.
create table error_occurrences
(
    id           bigint                      not null,
    signature_id bigint                      not null,
    domain_id    bigint                      not null,
    user_id      bigint                      not null,
    app_version  varchar(50)                 not null,
    context      jsonb                       not null,
    error        jsonb                       not null,
    logs         jsonb,
    created_at   timestamp(6) with time zone not null,
    primary key (id)
);
alter table error_occurrences
    add constraint fk_error_occurrences_signature foreign key (signature_id) references error_signatures on delete cascade;
alter table error_occurrences
    add constraint fk_error_occurrences_domain foreign key (domain_id) references domains on delete cascade;
alter table error_occurrences
    add constraint fk_error_occurrences_user foreign key (user_id) references users on delete cascade;
create index idx_error_occurrences_signature on error_occurrences (signature_id, created_at);
create index idx_error_occurrences_user_created on error_occurrences (user_id, created_at);
create index idx_error_occurrences_created on error_occurrences (created_at);

-- A bug report or a suggestion written by a member. Deleted with the member's account.
create table feedback_reports
(
    id                  bigint                      not null,
    domain_id           bigint                      not null,
    user_id             bigint                      not null,
    kind                varchar(20)                 not null,
    platform            varchar(20)                 not null,
    message             text                        not null,
    context             jsonb                       not null,
    error               jsonb,
    logs                jsonb,
    signature_id        bigint,
    github_status       varchar(20)                 not null,
    github_attempts     integer                     not null,
    github_issue_number integer,
    created_at          timestamp(6) with time zone not null,
    primary key (id)
);
alter table feedback_reports
    add constraint fk_feedback_reports_domain foreign key (domain_id) references domains on delete cascade;
alter table feedback_reports
    add constraint fk_feedback_reports_user foreign key (user_id) references users on delete cascade;
alter table feedback_reports
    add constraint fk_feedback_reports_signature foreign key (signature_id) references error_signatures on delete set null;
create index idx_feedback_reports_user_created on feedback_reports (user_id, created_at);
create index idx_feedback_reports_github_status on feedback_reports (github_status, created_at);
