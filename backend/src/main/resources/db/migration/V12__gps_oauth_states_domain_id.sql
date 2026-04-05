alter table gps_oauth_states
    add column domain_id bigint not null default 0;

alter table gps_oauth_states
    alter column domain_id drop default;
