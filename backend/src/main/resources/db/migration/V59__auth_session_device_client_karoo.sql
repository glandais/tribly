-- docs/LEDGER_*.md API-65: the Karoo app never sent its clientId (kotlinx.serialization leaves out
-- a property equal to its default), so its pairings were recorded under the backend's default,
-- 'device', and listed as « Autre ». Garmin always sends 'garmin': every 'device' pairing is a Karoo.
update auth_sessions
set device_client = 'karoo', user_agent = 'karoo Device'
where device_client = 'device';
