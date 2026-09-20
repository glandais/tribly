-- The PUSH channel's address book. See docs/plans/2026-09-18-notifications.md §5, phase 4.
--
-- One row per app install that accepted notifications, not one per user: a member with a phone and
-- a tablet gets both. The pipeline itself is unchanged — PushNotificationSender resolves a
-- recipient's devices at send time, exactly as EmailNotificationSender reads their address.

create table push_devices
(
    id           bigint                      not null,
    user_id      bigint                      not null,
    platform     varchar(20)                 not null,
    -- FCM registration tokens run ~160 characters today; the ceiling is generous on purpose, and
    -- still well under the btree limit the unique index below needs.
    token        varchar(512)                not null,
    device_name  varchar(120),
    app_version  varchar(40),
    created_at   timestamp(6) with time zone not null,
    last_seen_at timestamp(6) with time zone not null,
    version      bigint,
    primary key (id)
);

-- Unique on the token alone, not on (user, token): a token addresses one app install. When someone
-- else signs in on that phone, registration moves the row to them rather than adding a second one —
-- otherwise the previous user would keep receiving notifications on a device they logged out of.
alter table push_devices
    add constraint uk_push_devices_token unique (token);
alter table push_devices
    add constraint fk_push_devices_user foreign key (user_id) references users on delete cascade;
create index idx_push_devices_user on push_devices (user_id);
