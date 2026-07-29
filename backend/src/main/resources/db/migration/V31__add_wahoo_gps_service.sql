-- Allow WAHOO as a GPS service type alongside HAMMERHEAD and GARMIN.
-- The service_type CHECK constraints were created inline (unnamed) in V2; Postgres
-- auto-named them <table>_service_type_check, so we drop and recreate them widened.

alter table domain_gps_credentials
    drop constraint if exists domain_gps_credentials_service_type_check;
alter table domain_gps_credentials
    add constraint domain_gps_credentials_service_type_check
        check (service_type in ('HAMMERHEAD', 'GARMIN', 'WAHOO'));

alter table gps_service_connections
    drop constraint if exists gps_service_connections_service_type_check;
alter table gps_service_connections
    add constraint gps_service_connections_service_type_check
        check (service_type in ('HAMMERHEAD', 'GARMIN', 'WAHOO'));
