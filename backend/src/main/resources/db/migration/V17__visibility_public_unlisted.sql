alter table ride_templates
    drop constraint if exists ride_templates_visibility_check;
alter table ride_templates
    add constraint ride_templates_visibility_check check (visibility in ('TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'));

alter table team_entities
    drop constraint if exists team_entities_visibility_check;
alter table team_entities
    add constraint team_entities_visibility_check check (visibility in ('TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'));

alter table teams
    drop constraint if exists teams_visibility_check;
alter table teams
    add constraint teams_visibility_check check (visibility in ('TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'));
