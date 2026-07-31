-- Opens the member directory beyond a team's administrators. FALSE by default, unlike the other
-- enable_* flags: switching it on discloses the whole roster to every member, and that has to be an
-- act of the team's admin, not a side effect of applying this migration.
ALTER TABLE teams ADD COLUMN enable_member_directory BOOLEAN NOT NULL DEFAULT FALSE;
