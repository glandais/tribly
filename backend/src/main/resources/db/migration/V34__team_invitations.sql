-- An offer to join a team, addressed to an e-mail rather than to an account. It replaces adding a
-- member by picking them from a user search: nobody joins a team without a click of their own,
-- whether or not they already have an account.
--
-- token_hash, not the token: the plaintext is a bearer credential sitting in one inbox — the same
-- treatment as auth_tokens.token_hash, social_login_codes.code_hash and
-- user_exports.download_token_hash.
--
-- Its own table rather than a fifth auth_tokens type. AuthCleanupScheduler deletes auth tokens once
-- they are used or expired, which would erase every accepted and every revoked invitation within a
-- day — and who let whom into a team is exactly what one wants to still be able to look up.
--
-- domain_id is denormalised even though team_id implies it: it answers "which invitations are
-- waiting for this address?", the question asked right after someone signs up, without joining
-- teams. Same reason auth_tokens carries one.
create table team_invitations
(
    id             bigint                      not null,
    team_id        bigint                      not null,
    domain_id      bigint                      not null,
    email          varchar(250)                not null,
    role           varchar(20)                 not null,
    token_hash     varchar(100)                not null,
    status         varchar(20)                 not null,
    expires_at     timestamp(6) with time zone not null,
    accepted_at    timestamp(6) with time zone,
    accepted_by_id bigint,
    revoked_at     timestamp(6) with time zone,
    revoked_by_id  bigint,
    created_by_id  bigint                      not null,
    created_at     timestamp(6) with time zone not null,
    updated_at     timestamp(6) with time zone not null,
    version        bigint,
    primary key (id)
);

alter table team_invitations
    add constraint uk_team_invitations_token_hash unique (token_hash);
alter table team_invitations
    add constraint fk_team_invitations_team foreign key (team_id) references teams on delete cascade;
alter table team_invitations
    add constraint fk_team_invitations_domain foreign key (domain_id) references domains;
-- The inviter is never cleared: an accepted invitation becomes a user_teams row whose created_by is
-- this person, and a roster that cannot say who brought a member in is worth less than one that can.
alter table team_invitations
    add constraint fk_team_invitations_creator foreign key (created_by_id) references users;
-- Purely informative, hence the softer treatment.
alter table team_invitations
    add constraint fk_team_invitations_accepted_by foreign key (accepted_by_id) references users on delete set null;
alter table team_invitations
    add constraint fk_team_invitations_revoked_by foreign key (revoked_by_id) references users on delete set null;

-- "Which invitations are waiting for this address?", asked after every sign-up.
create index idx_team_invitations_email on team_invitations (domain_id, email, status);
-- The admin list, most recent first.
create index idx_team_invitations_team on team_invitations (team_id, created_at desc);
-- The rate-limit counter: how many has this inviter sent since T.
create index idx_team_invitations_creator on team_invitations (created_by_id, created_at);
-- The nightly sweep that turns lapsed PENDING rows into EXPIRED.
create index idx_team_invitations_expires on team_invitations (expires_at) where status = 'PENDING';

-- At most one live invitation per (team, address). Re-inviting is not an error — the previous one is
-- revoked and a fresh one issued, which is what the "resend" button does — so this index has to key
-- on the status, and a lapsed invitation has to be marked EXPIRED before the slot frees up.
-- TeamInvitationService checks this in Java too: tests build the schema from the JPA mappings, which
-- cannot express a partial index, so this is the backstop for two concurrent POSTs slipping through
-- the read-then-insert window.
create unique index uk_team_invitations_pending on team_invitations (team_id, email)
    where status = 'PENDING';
