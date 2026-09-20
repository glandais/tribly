-- A failed event waits before its next attempt instead of being retried within the same tick, where
-- a transient fault would burn every attempt in milliseconds. Same shape as notification_deliveries.
alter table notification_events
    add column next_attempt_at timestamp(6) with time zone;
update notification_events
set next_attempt_at = created_at;
alter table notification_events
    alter column next_attempt_at set not null;

drop index idx_notification_events_status_created;
create index idx_notification_events_queue on notification_events (status, next_attempt_at);
