create table gps_oauth_states (
    id bigint not null,
    user_id bigint not null,
    state varchar(100) not null unique,
    service_type varchar(20) not null,
    expires_at timestamp(6) with time zone not null,
    code_verifier varchar(200),
    redirect_uri varchar(500) not null,
    created_at timestamp(6) with time zone not null,
    primary key (id)
);
