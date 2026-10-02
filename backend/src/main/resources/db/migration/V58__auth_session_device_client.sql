-- docs/LEDGER_*.md API-64: the client of a session opened by a device pairing (RFC 8628) —
-- 'karoo', 'garmin'… — so the profile can list paired devices and unpair one. Null for the site and
-- the app. Nullable: during a start-first deploy the previous release still pairs without it.
alter table auth_sessions add column device_client varchar(50);

-- Pairings made before this column: createTokenResponse wrote "<clientId> Device" / "device".
update auth_sessions
set device_client = left(user_agent, length(user_agent) - length(' Device'))
where ip_address = 'device'
  and user_agent like '% Device';

create index idx_auth_sessions_device_user on auth_sessions (user_id) where device_client is not null;
