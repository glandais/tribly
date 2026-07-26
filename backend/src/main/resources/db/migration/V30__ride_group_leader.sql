-- Who actually leads a ride group.
--
-- The mockups draw an "Organisateur" pill on the participants sheet, and nothing in the model
-- could fill it: ride_groups only had created_by from BaseEntity, which holds whoever created the
-- *ride* — the same person on every one of its groups. Rendering that under "group leader" would
-- have been wrong on almost every group, and wrong in the confident way that nobody reports.
--
-- Nullable, and it stays nullable: most groups have no designated leader, and a NOT NULL DEFAULT
-- would have to invent one. A null leader means "not designated", which the clients render as no
-- pill at all rather than as a fallback.
--
-- ON DELETE SET NULL rather than CASCADE: a leader account going away must not take the group, its
-- route and its participants with it.

alter table ride_groups
    add column leader_id bigint references users (id) on delete set null;

-- Answers "which groups does this member lead?", the read behind a future "my groups" screen and
-- behind any cleanup when someone leaves a team. Partial, because the column is null far more
-- often than not.
create index idx_ride_groups_leader on ride_groups (leader_id) where leader_id is not null;
