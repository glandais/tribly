-- Event-driven notifications. See docs/plans/2026-09-18-notifications.md.
--
-- Three stages, three tables:
--   notification_events      the outbox — one row per business event, written in the business
--                            transaction, fanned out by a scheduler;
--   notifications            one row per (event, recipient) — the in-app inbox;
--   notification_deliveries  one row per (notification, out-of-app channel) — each channel's send
--                            queue.
-- Plus notification_preferences, which only stores the cells a member changed.
--
-- domain_id is denormalised onto events and notifications: the inbox queries filter on the
-- notifications table alone, and the dispatcher has no request to resolve a domain from.

create table notification_events
(
    id                bigint                      not null,
    domain_id         bigint                      not null,
    type              varchar(40)                 not null,
    status            varchar(20)                 not null,
    dedup_key         varchar(200)                not null,
    payload           jsonb                       not null,
    actor_id          bigint,
    team_id           bigint,
    attempts          integer                     not null,
    created_at        timestamp(6) with time zone not null,
    started_at        timestamp(6) with time zone,
    processed_at      timestamp(6) with time zone,
    error_message     varchar(500),
    actor_name        varchar(250),
    team_slug         varchar(250),
    team_name         varchar(250),
    subject_type      varchar(20),
    subject_slug      varchar(250),
    subject_name      varchar(250),
    subject_date_time timestamp(6) with time zone,
    excerpt           varchar(500),
    base_url          varchar(500),
    site_name         varchar(250),
    version           bigint,
    primary key (id)
);

-- The publisher inserts with ON CONFLICT (dedup_key) DO NOTHING: a ride published twice notifies
-- once, and the duplicate never raises inside the business transaction.
alter table notification_events
    add constraint uk_notification_events_dedup_key unique (dedup_key);
alter table notification_events
    add constraint fk_notification_events_domain foreign key (domain_id) references domains;
-- Informative only: the snapshot already holds the actor's name.
alter table notification_events
    add constraint fk_notification_events_actor foreign key (actor_id) references users on delete set null;
alter table notification_events
    add constraint fk_notification_events_team foreign key (team_id) references teams on delete cascade;
create index idx_notification_events_status_created on notification_events (status, created_at);
create index idx_notification_events_created on notification_events (created_at);

create table notifications
(
    id           bigint                      not null,
    event_id     bigint                      not null,
    recipient_id bigint                      not null,
    domain_id    bigint                      not null,
    type         varchar(40)                 not null,
    created_at   timestamp(6) with time zone not null,
    read_at      timestamp(6) with time zone,
    primary key (id)
);

alter table notifications
    add constraint uk_notifications_event_recipient unique (event_id, recipient_id);
alter table notifications
    add constraint fk_notifications_event foreign key (event_id) references notification_events on delete cascade;
alter table notifications
    add constraint fk_notifications_recipient foreign key (recipient_id) references users on delete cascade;
create index idx_notifications_recipient_created on notifications (recipient_id, created_at);
create index idx_notifications_recipient_read on notifications (recipient_id, read_at);

create table notification_deliveries
(
    id              bigint                      not null,
    notification_id bigint                      not null,
    channel         varchar(20)                 not null,
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

alter table notification_deliveries
    add constraint uk_notification_deliveries_notification_channel unique (notification_id, channel);
alter table notification_deliveries
    add constraint fk_notification_deliveries_notification foreign key (notification_id) references notifications on delete cascade;
create index idx_notification_deliveries_queue on notification_deliveries (channel, status, next_attempt_at);

create table notification_preferences
(
    id         bigint                      not null,
    user_id    bigint                      not null,
    type       varchar(40)                 not null,
    channel    varchar(20)                 not null,
    enabled    boolean                     not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    primary key (id)
);

alter table notification_preferences
    add constraint uk_notification_preferences_user_type_channel unique (user_id, type, channel);
alter table notification_preferences
    add constraint fk_notification_preferences_user foreign key (user_id) references users on delete cascade;
