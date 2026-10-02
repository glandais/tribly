-- docs/LEDGER_*.md API-62: Garmin can be reached over OAuth 1.0a while its OAuth 2.0 programme
-- admits no new application (API-61).
--
-- A domain's credential says which protocol its client ID and secret belong to; every existing row
-- is an OAuth 2.0 one. An OAuth 1.0a request token and access token each come with a secret, kept
-- encrypted beside them. All three columns are nullable or defaulted: the previous release, which
-- knows none of them, keeps reading and writing these tables during a rolling deploy.
alter table domain_gps_credentials
    add column oauth_version varchar(10) not null default 'OAUTH2'
        check (oauth_version in ('OAUTH1', 'OAUTH2'));
alter table gps_oauth_states add column request_token_secret_encrypted bytea;
alter table gps_service_connections add column access_token_secret_encrypted bytea;
