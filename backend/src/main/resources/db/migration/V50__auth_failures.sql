-- docs/LEDGER_*.md SEC-4 and SEC-7 (audit H5 and M4): the recent failed attempts at a secret an
-- attacker could guess online — a password, a device pairing code. Counting them is what lets the
-- server refuse the next guess before paying for it (a bcrypt, a code lookup).
--
-- In the database rather than in memory: during a rolling deploy two backends answer side by side,
-- and a counter held by one of them would halve the limit. What is stored is not the identifier
-- itself: `subject` is a SHA-256 of the lower-cased e-mail for a password, the user id for a pairing
-- code, `-` for an anonymous lookup. Rows older than a day are purged every night
-- (AuthCleanupScheduler), which the privacy policy announces.
--
-- A new table the previous release never reads: safe for a rolling deploy.
create table auth_failures
(
    id         bigint      not null primary key,
    domain_id  bigint      not null references domains (id) on delete cascade,
    kind       varchar(20) not null,
    subject    varchar(64) not null,
    created_at timestamptz not null
);

-- "How many failures for this subject since T", and "how many on this domain since T".
create index idx_auth_failures_subject on auth_failures (domain_id, kind, subject, created_at);
create index idx_auth_failures_created on auth_failures (created_at);
