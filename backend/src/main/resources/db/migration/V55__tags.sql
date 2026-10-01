-- docs/LEDGER_*.md API-59 (docs/plans/archive/2026-10-01-tags.md): a team's tags on its rides, posts,
-- trips, routes and ads, one vocabulary per kind. New tables only: the previous release, which
-- knows none of them, keeps running beside this one during a start-first deploy.

-- A word of a team's vocabulary for one kind of content. No trash: a deleted tag is detached
-- everywhere and gone.
create table tags
(
    id            bigint                      not null,
    team_id       bigint                      not null,
    type          varchar(20)                 not null check (type in ('RIDE', 'POST', 'TRIP', 'ROUTE', 'AD')),
    label         varchar(32)                 not null,
    color         varchar(20)                 not null check (color in
        ('INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY')),
    created_by_id bigint                      not null,
    created_at    timestamp(6) with time zone not null,
    updated_at    timestamp(6) with time zone not null,
    version       bigint,
    primary key (id)
);
alter table tags
    add constraint fk_tags_team foreign key (team_id) references teams;
alter table tags
    add constraint fk_tags_created_by foreign key (created_by_id) references users;
-- Unique per team and kind, whatever the case: « Gravel » and « gravel » are one tag. TagService
-- checks it first; this settles two admins creating the same label at once.
create unique index uk_tags_team_type_label on tags (team_id, type, lower(label));
create index idx_tags_team_type on tags (team_id, type);

-- A tag on a ride, post, trip, route or ad (all in team_entities). Removed with either end.
create table team_entity_tags
(
    id             bigint not null,
    team_entity_id bigint not null,
    tag_id         bigint not null,
    primary key (id)
);
alter table team_entity_tags
    add constraint uk_team_entity_tags_entity_tag unique (team_entity_id, tag_id);
alter table team_entity_tags
    add constraint fk_team_entity_tags_entity foreign key (team_entity_id) references team_entities on delete cascade;
alter table team_entity_tags
    add constraint fk_team_entity_tags_tag foreign key (tag_id) references tags on delete cascade;
create index idx_team_entity_tags_tag on team_entity_tags (tag_id);

-- A RIDE tag on a ride template, copied onto the rides created from it. Ride templates are
-- hard-deleted: their links go with them.
create table ride_template_tags
(
    id               bigint not null,
    ride_template_id bigint not null,
    tag_id           bigint not null,
    primary key (id)
);
alter table ride_template_tags
    add constraint uk_ride_template_tags_template_tag unique (ride_template_id, tag_id);
alter table ride_template_tags
    add constraint fk_ride_template_tags_template foreign key (ride_template_id) references ride_templates on delete cascade;
alter table ride_template_tags
    add constraint fk_ride_template_tags_tag foreign key (tag_id) references tags on delete cascade;
create index idx_ride_template_tags_tag on ride_template_tags (tag_id);
