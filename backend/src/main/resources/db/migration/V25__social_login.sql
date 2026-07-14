-- Social login (Strava) — identities, OAuth CSRF state, and one-time session hand-off codes.

create table user_social_identities (
    id bigint not null,
    user_id bigint not null,
    domain_id bigint not null,
    provider varchar(20) not null,
    external_user_id varchar(100) not null,
    created_at timestamp(6) with time zone not null,
    last_login_at timestamp(6) with time zone,
    primary key (id),
    constraint uk_social_identity unique (domain_id, provider, external_user_id),
    constraint fk_user_social_identities_user_id foreign key (user_id) references users (id) on delete cascade
);

create index idx_user_social_identities_user_id on user_social_identities (user_id);

create table social_oauth_states (
    id bigint not null,
    user_id bigint,
    state varchar(100) not null unique,
    provider varchar(20) not null,
    purpose varchar(10) not null,
    expires_at timestamp(6) with time zone not null,
    redirect_uri varchar(500) not null,
    return_base_url varchar(500) not null,
    domain_id bigint not null,
    created_at timestamp(6) with time zone not null,
    primary key (id),
    constraint fk_social_oauth_states_user_id foreign key (user_id) references users (id) on delete cascade
);

create index idx_social_oauth_states_expires_at on social_oauth_states (expires_at);

create table social_login_codes (
    id bigint not null,
    user_id bigint not null,
    domain_id bigint not null,
    code_hash varchar(100) not null unique,
    needs_email boolean not null,
    expires_at timestamp(6) with time zone not null,
    used_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone not null,
    primary key (id),
    constraint fk_social_login_codes_user_id foreign key (user_id) references users (id) on delete cascade
);

create index idx_social_login_codes_expires_at on social_login_codes (expires_at);
