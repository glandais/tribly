alter table gps_oauth_states
    add constraint fk_gps_oauth_states_user_id foreign key (user_id) references users (id) on delete cascade;

create index idx_gps_oauth_states_expires_at on gps_oauth_states (expires_at);
