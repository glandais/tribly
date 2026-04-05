alter table auth_tokens
    add column domain_id bigint not null default 0;

alter table auth_tokens
    alter column domain_id drop default;
