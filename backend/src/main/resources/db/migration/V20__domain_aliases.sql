create table domain_aliases (
    id bigint not null,
    domain_id bigint not null,
    pinned_team_id bigint not null,
    active boolean not null,
    deleted boolean not null,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    version bigint,
    hostname varchar(250) not null unique,
    name varchar(250) not null,
    base_url varchar(500) not null,
    android_fingerprints varchar(1000),
    primary key (id)
);

alter table domain_aliases
    add constraint fk_domain_aliases_domain_id foreign key (domain_id) references domains (id);

alter table domain_aliases
    add constraint fk_domain_aliases_pinned_team_id foreign key (pinned_team_id) references teams (id);

create index idx_domain_aliases_hostname on domain_aliases (hostname, active, deleted);
create index idx_domain_aliases_domain_id on domain_aliases (domain_id);
create index idx_domain_aliases_pinned_team_id on domain_aliases (pinned_team_id);
