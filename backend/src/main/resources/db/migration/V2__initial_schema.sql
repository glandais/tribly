
create table assets (
                        deleted boolean not null,
                        height integer,
                        sort_order integer not null,
                        width integer,
                        created_at timestamp(6) with time zone not null,
                        created_by_id bigint not null,
                        file_id bigint not null,
                        id bigint not null,
                        team_entity_id bigint,
                        team_id bigint not null,
                        updated_at timestamp(6) with time zone not null,
                        version bigint,
                        type varchar(20) not null check ((type in ('LOGO','IMAGE','VIDEO','ATTACHMENT','ROUTE_ORIGINAL_GPX','ROUTE_FILTERED_GPX','ROUTE_FIT','ROUTE_THUMBNAIL'))),
                        content_type varchar(255) not null,
                        file_name varchar(255) not null,
                        primary key (id)
);

create table gpx_tracks (
                            deleted boolean not null,
                            distance integer not null,
                            elevation_gain integer not null,
                            elevation_loss integer not null,
                            created_at timestamp(6) with time zone not null,
                            created_by_id bigint not null,
                            id bigint not null,
                            route_id bigint not null,
                            updated_at timestamp(6) with time zone not null,
                            version bigint,
                            name varchar(255) not null,
                            climbs jsonb not null,
                            geometry geometry(LineString,4326) not null,
                            track_points jsonb not null,
                            primary key (id)
);

create table gpx_waypoints (
                               deleted boolean not null,
                               created_at timestamp(6) with time zone not null,
                               created_by_id bigint not null,
                               id bigint not null,
                               route_id bigint not null,
                               updated_at timestamp(6) with time zone not null,
                               version bigint,
                               name varchar(255) not null,
                               geometry geometry(Point,4326) not null,
                               primary key (id)
);

create table places (
                        deleted boolean not null,
                        end_place boolean not null,
                        start_place boolean not null,
                        created_at timestamp(6) with time zone not null,
                        created_by_id bigint not null,
                        id bigint not null,
                        team_id bigint not null,
                        updated_at timestamp(6) with time zone not null,
                        version bigint,
                        address varchar(255),
                        link varchar(255),
                        name varchar(255) not null,
                        geometry geometry(Point,4326),
                        primary key (id)
);

create table ride_groups (
                             average_speed integer,
                             deleted boolean not null,
                             max_participants integer,
                             sort_order integer not null,
                             time time(0),
                             created_at timestamp(6) with time zone not null,
                             created_by_id bigint not null,
                             id bigint not null,
                             ride_id bigint not null,
                             route_id bigint,
                             updated_at timestamp(6) with time zone not null,
                             version bigint,
                             name varchar(255) not null,
                             primary key (id)
);

create table ride_participations (
                                     deleted boolean not null,
                                     created_at timestamp(6) with time zone not null,
                                     created_by_id bigint not null,
                                     id bigint not null,
                                     registered_at timestamp(6) with time zone not null,
                                     ride_group_id bigint not null,
                                     updated_at timestamp(6) with time zone not null,
                                     user_id bigint not null,
                                     version bigint,
                                     primary key (id),
                                     unique (ride_group_id, user_id)
);

create table ride_template_groups (
                                      average_speed integer,
                                      deleted boolean not null,
                                      max_participants integer,
                                      sort_order integer not null,
                                      time time(0),
                                      created_at timestamp(6) with time zone not null,
                                      created_by_id bigint not null,
                                      id bigint not null,
                                      template_id bigint not null,
                                      updated_at timestamp(6) with time zone not null,
                                      version bigint,
                                      name varchar(255) not null,
                                      primary key (id)
);

create table ride_templates (
                                deleted boolean not null,
                                created_at timestamp(6) with time zone not null,
                                created_by_id bigint not null,
                                id bigint not null,
                                team_id bigint not null,
                                updated_at timestamp(6) with time zone not null,
                                version bigint,
                                status varchar(20) not null check ((status in ('DRAFT','PUBLISHED','CANCELLED'))),
                                visibility varchar(20) not null check ((visibility in ('TEAM','PUBLIC'))),
                                markdown TEXT,
                                name varchar(255) not null,
                                slug varchar(255) not null,
                                primary key (id),
                                constraint uk_ride_template_slug unique (team_id, slug)
);

create table team_entities (
                               deleted boolean not null,
                               distance integer,
                               elevation_gain integer,
                               elevation_loss integer,
                               entity_type integer not null check ((entity_type in (1,3,5,4,6,2))),
                               sort_order integer,
                               created_at timestamp(6) with time zone not null,
                               created_by_id bigint not null,
                               date_time timestamp(6) with time zone not null,
                               id bigint not null,
                               place_end_id bigint,
                               place_start_id bigint,
                               publish_at timestamp(6) with time zone,
                               route_id bigint,
                               team_id bigint not null,
                               trip_id bigint,
                               updated_at timestamp(6) with time zone not null,
                               version bigint,
                               status varchar(20) not null check ((status in ('DRAFT','PUBLISHED','CANCELLED'))),
                               surface_type varchar(20) check ((surface_type in ('ROAD','GRAVEL','MTB','MIXED'))),
                               visibility varchar(20) not null check ((visibility in ('TEAM','PUBLIC'))),
                               markdown TEXT,
                               name varchar(255) not null,
                               slug varchar(255) not null,
                               "end" geometry(Point,4326),
                               "start" geometry(Point,4326),
                               primary key (id),
                               constraint uk_team_entity_slug unique (team_id, entity_type, slug),
                               check (entity_type <> 6 or (sort_order is not null))
);

create table teams (
                       deleted boolean not null,
                       enable_trips boolean not null,
                       created_at timestamp(6) with time zone not null,
                       created_by_id bigint not null,
                       description_id bigint,
                       id bigint not null,
                       updated_at timestamp(6) with time zone not null,
                       version bigint,
                       visibility varchar(20) not null check ((visibility in ('TEAM','PUBLIC'))),
                       name varchar(255) not null,
                       slug varchar(255) not null unique,
                       primary key (id)
);

create table trip_participations (
                                     deleted boolean not null,
                                     created_at timestamp(6) with time zone not null,
                                     created_by_id bigint not null,
                                     id bigint not null,
                                     registered_at timestamp(6) with time zone not null,
                                     trip_id bigint not null,
                                     updated_at timestamp(6) with time zone not null,
                                     user_id bigint not null,
                                     version bigint,
                                     primary key (id),
                                     unique (trip_id, user_id)
);

create table user_teams (
                            deleted boolean not null,
                            created_at timestamp(6) with time zone not null,
                            created_by_id bigint not null,
                            id bigint not null,
                            joined_at timestamp(6) with time zone not null,
                            team_id bigint not null,
                            updated_at timestamp(6) with time zone not null,
                            user_id bigint not null,
                            version bigint,
                            role varchar(20) not null check ((role in ('ADMIN','ORGANIZER','MEMBER'))),
                            primary key (id),
                            unique (user_id, team_id)
);

create table users (
                       deleted boolean not null,
                       created_at timestamp(6) with time zone not null,
                       created_by_id bigint not null,
                       id bigint not null,
                       last_login_at timestamp(6) with time zone,
                       updated_at timestamp(6) with time zone not null,
                       version bigint,
                       avatar_url varchar(500),
                       display_name varchar(255) not null,
                       email varchar(255) not null unique,
                       primary key (id)
);

create index IDXe5fskel3tfc9ce4a4xu3vn4f5
    on ride_groups (ride_id, deleted);

create index IDXi9tkt14pnxn8jlc7a8tnijxpt
    on ride_template_groups (template_id, deleted);

create index IDXcqoh6mr47q7qjqrkrg959v4ue
    on ride_templates (team_id, deleted);

create index IDXldn4q7s3s14kl5v91wx6fclw6
    on ride_templates (team_id, slug, deleted);

create index IDXab1hjyv07yec90acqoniimhsl
    on team_entities (team_id, deleted);

create index IDXodn4216raq36ttg36f0n5qeso
    on team_entities (entity_type, deleted, date_time);

create index IDXk5d576vibi9v36bqwm9k6pi5i
    on team_entities (entity_type, deleted, team_id);

create index IDXojj7g1qddcwjo12oap9qoshtv
    on team_entities (publish_at, status, deleted);

create index IDXtgtejwmqna5rvtlr0ho3kgl6m
    on team_entities (entity_type, deleted, team_id, date_time);

create index IDXngsx2ujy92deb6wsf1bg6wd4a
    on team_entities (entity_type, deleted, team_id, slug);

create index IDXo3hbo8wlcgmi4dqwrqm9jtm8f
    on teams (slug, deleted);

alter table if exists assets
    add constraint FKcifafsjy81jtl602qr99flp5u
    foreign key (created_by_id)
    references users;

alter table if exists assets
    add constraint FKjr39lgci2ppfv4xnn1lohpb1t
    foreign key (team_id)
    references teams;

alter table if exists assets
    add constraint FKenrk244dfrua38w17igcj3g
    foreign key (team_entity_id)
    references team_entities;

alter table if exists gpx_tracks
    add constraint FKlwsi0pakto3tlsgriae6exsqa
    foreign key (created_by_id)
    references users;

alter table if exists gpx_tracks
    add constraint FK8caclj6tlkl3wydyngervcys
    foreign key (route_id)
    references team_entities;

alter table if exists gpx_waypoints
    add constraint FKe5qntgukwly8p449erk45inef
    foreign key (created_by_id)
    references users;

alter table if exists gpx_waypoints
    add constraint FK5cv6u4vigp9if7mvs8utt07m4
    foreign key (route_id)
    references team_entities;

alter table if exists places
    add constraint FKt5u395u1lpaabeikbk3lbbvud
    foreign key (created_by_id)
    references users;

alter table if exists places
    add constraint FKjpbds7clrhctishgyn1ggocs4
    foreign key (team_id)
    references teams;

alter table if exists ride_groups
    add constraint FK8qjxje3lbfu8xlx62gosyert8
    foreign key (created_by_id)
    references users;

alter table if exists ride_groups
    add constraint FKrue29m0taoth51b6fw4ct2n37
    foreign key (ride_id)
    references team_entities;

alter table if exists ride_groups
    add constraint FKhx6lu9t9061cog8ckjb6xi4dx
    foreign key (route_id)
    references team_entities;

alter table if exists ride_participations
    add constraint FKo7ivuecfw1yfyoa5ygbhpgqa7
    foreign key (created_by_id)
    references users;

alter table if exists ride_participations
    add constraint FK2lx9qhfwm4uogwj8f2spepcwd
    foreign key (ride_group_id)
    references ride_groups;

alter table if exists ride_participations
    add constraint FKfs60sriol494mpo27wwxhrmrn
    foreign key (user_id)
    references users;

alter table if exists ride_template_groups
    add constraint FKjjpkdknn7n3mkde0oiops3q9b
    foreign key (created_by_id)
    references users;

alter table if exists ride_template_groups
    add constraint FKa9viktsanmu0xro69911d5il1
    foreign key (template_id)
    references ride_templates;

alter table if exists ride_templates
    add constraint FK1j2jm60j2s32vm8nxookvqyiw
    foreign key (created_by_id)
    references users;

alter table if exists ride_templates
    add constraint FKvxj9mkg2b3mw5fk464fkxk8x
    foreign key (team_id)
    references teams;

alter table if exists team_entities
    add constraint FKdrfmubd9rkuh74qb5huontpaq
    foreign key (created_by_id)
    references users;

alter table if exists team_entities
    add constraint FKdajojv9f175wc1mpjtm73vnxs
    foreign key (team_id)
    references teams;

alter table if exists team_entities
    add constraint FKjukml9fp2eipuhmugtiaf12gs
    foreign key (place_end_id)
    references places;

alter table if exists team_entities
    add constraint FKsm0040p8exgxema0d3j4osclb
    foreign key (route_id)
    references team_entities;

alter table if exists team_entities
    add constraint FKm7w1a9lbh6795ida5u4n95vdv
    foreign key (place_start_id)
    references places;

alter table if exists team_entities
    add constraint FKsv6ybr332iwjj0ya7947jv2kx
    foreign key (trip_id)
    references team_entities;

alter table if exists teams
    add constraint FKcq9jk9qh4ox827y0d161rabce
    foreign key (created_by_id)
    references users;

alter table if exists teams
    add constraint FKlorb7wivvrwpknrj7pcc6pqny
    foreign key (description_id)
    references team_entities;

alter table if exists trip_participations
    add constraint FK53e4u32gp8syx3tks1hc5wlt6
    foreign key (created_by_id)
    references users;

alter table if exists trip_participations
    add constraint FKp7t4suoehqwc2ro5magoclp9y
    foreign key (trip_id)
    references team_entities;

alter table if exists trip_participations
    add constraint FKt533ggb78hp0camm0gfkhqg1w
    foreign key (user_id)
    references users;

alter table if exists user_teams
    add constraint FKr2g0ggpshfw7qtfqj311b9xu0
    foreign key (created_by_id)
    references users;

alter table if exists user_teams
    add constraint FK2ndqpo9mm1g72f7hvb9daimrd
    foreign key (team_id)
    references teams;

alter table if exists user_teams
    add constraint FK5aymw95okwem1l7tmd2owesdh
    foreign key (user_id)
    references users;

alter table if exists users
    add constraint FK8nakkftyppd62ke6tv7oo5a92
    foreign key (created_by_id)
    references users;
