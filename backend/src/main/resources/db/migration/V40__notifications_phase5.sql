-- Notifications, phase 5. See §12 of docs/plans/2026-09-18-notifications.md.

-- What a RIDE_UPDATED says changed (comma-separated NotificationChange names), frozen at fan-out
-- with the rest of the snapshot.
alter table notification_events
    add column changes varchar(100);

-- An e-mail held for the recipient's daily digest. The ordinary sender skips these; the digest tick
-- claims them per recipient, once due.
alter table notification_deliveries
    add column digest boolean not null default false;
create index idx_notification_deliveries_digest on notification_deliveries (digest, status, next_attempt_at)
    where digest;

-- "Nothing more from this team": a muted team produces no broadcast notification for this member,
-- inbox included. Personal types (a cancellation of a ride they joined…) are never muted.
create table notification_team_mutes
(
    id         bigint                      not null,
    user_id    bigint                      not null,
    team_id    bigint                      not null,
    created_at timestamp(6) with time zone not null,
    primary key (id)
);
alter table notification_team_mutes
    add constraint uk_notification_team_mutes_user_team unique (user_id, team_id);
alter table notification_team_mutes
    add constraint fk_notification_team_mutes_user foreign key (user_id) references users on delete cascade;
alter table notification_team_mutes
    add constraint fk_notification_team_mutes_team foreign key (team_id) references teams on delete cascade;
create index idx_notification_team_mutes_team on notification_team_mutes (team_id);

-- Member-wide notification settings. One row at most per user; absent means every default.
create table notification_settings
(
    id           bigint                      not null,
    user_id      bigint                      not null,
    email_digest boolean                     not null,
    created_at   timestamp(6) with time zone not null,
    updated_at   timestamp(6) with time zone not null,
    primary key (id)
);
alter table notification_settings
    add constraint uk_notification_settings_user unique (user_id);
alter table notification_settings
    add constraint fk_notification_settings_user foreign key (user_id) references users on delete cascade;

-- One outgoing webhook per team, fed from the events rather than per recipient. The URL is a
-- secret: whoever holds a Slack or Discord webhook URL can post to that channel.
create table team_webhooks
(
    id              bigint                      not null,
    team_id         bigint                      not null,
    url             varchar(1000)               not null,
    language        varchar(10)                 not null,
    enabled         boolean                     not null,
    last_status     varchar(20),
    last_error      varchar(500),
    last_attempt_at timestamp(6) with time zone,
    created_at      timestamp(6) with time zone not null,
    updated_at      timestamp(6) with time zone not null,
    primary key (id)
);
alter table team_webhooks
    add constraint uk_team_webhooks_team unique (team_id);
alter table team_webhooks
    add constraint fk_team_webhooks_team foreign key (team_id) references teams on delete cascade;

create table team_webhook_deliveries
(
    id              bigint                      not null,
    event_id        bigint                      not null,
    webhook_id      bigint                      not null,
    status          varchar(20)                 not null,
    attempts        integer                     not null,
    next_attempt_at timestamp(6) with time zone not null,
    last_attempt_at timestamp(6) with time zone,
    sent_at         timestamp(6) with time zone,
    error_message   varchar(500),
    created_at      timestamp(6) with time zone not null,
    version         bigint,
    primary key (id)
);
alter table team_webhook_deliveries
    add constraint uk_team_webhook_deliveries_event_webhook unique (event_id, webhook_id);
alter table team_webhook_deliveries
    add constraint fk_team_webhook_deliveries_event foreign key (event_id) references notification_events on delete cascade;
alter table team_webhook_deliveries
    add constraint fk_team_webhook_deliveries_webhook foreign key (webhook_id) references team_webhooks on delete cascade;
create index idx_team_webhook_deliveries_queue on team_webhook_deliveries (status, next_attempt_at);
