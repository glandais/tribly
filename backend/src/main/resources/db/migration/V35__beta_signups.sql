-- Sign-ups for apps not released yet (mobile, Garmin), to be added by hand to the relevant beta
-- program (TestFlight, Play Console, Connect IQ) once it opens.
--
-- No link to `users` or `teams`: anyone can express interest before ever creating an account, and
-- the apps being advertised aren't scoped to a team. `domain_id` is denormalized purely for
-- reporting on a white-labelled deployment, the same convention `team_invitations` uses.

create table beta_signups
(
    id         bigint      not null primary key,
    email      text        not null,
    domain_id  bigint,
    created_at timestamptz not null
);

-- Resubmitting the same email is a no-op, not an error, so the constraint that enforces it is
-- case-insensitive.
create unique index idx_beta_signups_email on beta_signups (lower(email));
