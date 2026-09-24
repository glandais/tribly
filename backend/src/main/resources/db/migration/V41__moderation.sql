-- Reporting and blocking, App Store guideline 1.2. See ReportService and UserBlockService.

-- A member's report of a comment, a publication, an ad, a route or another member. The target is
-- kept by type and id rather than by foreign key: a comment is hard-deleted when a moderator removes
-- it, and the report must outlive it as the record of what was decided.
create table content_reports
(
    id             bigint                      not null,
    domain_id      bigint                      not null,
    team_id        bigint                      not null,
    -- Null once the reporter's account is erased: the report stays, for the decision it led to.
    reporter_id    bigint,
    target_type    varchar(20)                 not null,
    target_id      bigint                      not null,
    -- The author of the content, or the member reported: who the moderation is about.
    target_user_id bigint                      not null,
    reason         varchar(30)                 not null,
    message        varchar(500),
    -- The reported text as it was, so the moderator reads what was reported even if it is edited.
    excerpt        varchar(1000),
    status         varchar(20)                 not null,
    resolved_by_id bigint,
    resolved_at    timestamp(6) with time zone,
    created_at     timestamp(6) with time zone not null,
    primary key (id)
);
alter table content_reports
    add constraint uk_content_reports_reporter_target unique (reporter_id, target_type, target_id);
alter table content_reports
    add constraint fk_content_reports_domain foreign key (domain_id) references domains on delete cascade;
alter table content_reports
    add constraint fk_content_reports_team foreign key (team_id) references teams on delete cascade;
alter table content_reports
    add constraint fk_content_reports_reporter foreign key (reporter_id) references users on delete set null;
alter table content_reports
    add constraint fk_content_reports_target_user foreign key (target_user_id) references users on delete cascade;
alter table content_reports
    add constraint fk_content_reports_resolved_by foreign key (resolved_by_id) references users on delete set null;
create index idx_content_reports_team_status on content_reports (team_id, status, created_at);
create index idx_content_reports_domain_status on content_reports (domain_id, status, created_at);
create index idx_content_reports_target on content_reports (target_type, target_id);
create index idx_content_reports_target_user on content_reports (target_user_id);

-- A member who no longer wants to see another's comments, posts and ads. One-way and silent: the
-- blocked member is never told, and sees nothing change.
create table user_blocks
(
    id         bigint                      not null,
    blocker_id bigint                      not null,
    blocked_id bigint                      not null,
    created_at timestamp(6) with time zone not null,
    primary key (id)
);
alter table user_blocks
    add constraint uk_user_blocks_blocker_blocked unique (blocker_id, blocked_id);
alter table user_blocks
    add constraint fk_user_blocks_blocker foreign key (blocker_id) references users on delete cascade;
alter table user_blocks
    add constraint fk_user_blocks_blocked foreign key (blocked_id) references users on delete cascade;
create index idx_user_blocks_blocked on user_blocks (blocked_id);

-- Set when enough distinct members reported the content: hidden from everyone but the team's
-- moderators until one of them decides.
alter table team_entities
    add column moderation_hidden_at timestamp(6) with time zone;
alter table comments
    add column moderation_hidden_at timestamp(6) with time zone;

-- When the member accepted the terms of service, at sign-up. Null for accounts created before the
-- sign-up form asked.
alter table users
    add column terms_accepted_at timestamp(6) with time zone;

-- When the sign-up form's terms checkbox was accepted, carried by the email-verification token until
-- the account exists. Null on a token issued before the form asked: verifying it records no consent.
alter table auth_tokens
    add column pending_terms_accepted_at timestamp(6) with time zone;
