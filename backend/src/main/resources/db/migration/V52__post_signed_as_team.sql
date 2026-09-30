-- docs/LEDGER_*.md API-6: a post is signed either by its author or by its team.
--
-- signed_as_team lives on team_entities (single-table inheritance): only posts read it. Existing
-- posts are signed by the team — they were written when no author was ever shown, and none should
-- start naming its writer on the day this ships. The default is true for the same reason, and so
-- that the previous release, which does not know the column, still inserts during a rolling
-- deploy: its posts stay unsigned, as they are to it. The current release always writes the column.
alter table team_entities add column signed_as_team boolean not null default true;

-- The value a new post's « Au nom de l'équipe » box starts from. On for every team, existing and
-- new: showing names is something a team chooses.
alter table teams add column posts_as_team_by_default boolean not null default true;
