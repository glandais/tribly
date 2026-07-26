-- Contacting a classified ad's author without publishing their address.
--
-- The alternative was a free-text `contact` column on `ads`, holding an email or a phone number
-- rendered to every team member. On a 1999-member team that is an irrevocable disclosure: once
-- seen it cannot be unseen, and it lands in the GDPR export of whoever copied it. Relaying the
-- message through the server instead means the contract never carries a contact field at all.
--
-- What is stored here is metadata, not correspondence: who wrote to which ad, and when. That is
-- what rate limiting needs and what an abuse report needs; the message body itself only ever
-- exists in the email that carried it, so this table never becomes a message store we would have
-- to expose, export and delete.

create table ad_contacts
(
    id            bigint      not null primary key,
    ad_id         bigint      not null references team_entities (id) on delete cascade,
    created_by_id bigint      not null references users (id),
    created_at    timestamptz not null,
    updated_at    timestamptz not null,
    version       bigint      not null default 0
);

-- The rate-limit read is "how many messages has this sender sent since T", so sender first.
create index idx_ad_contacts_sender_created on ad_contacts (created_by_id, created_at);

-- The abuse read is "who has been writing to this ad".
create index idx_ad_contacts_ad_created on ad_contacts (ad_id, created_at);

-- Opting out. Null means "never chosen", which reads as contactable — the same convention V28 uses
-- for theme and language. Only an explicit false silences the relay, so no existing account has its
-- ads made unreachable by this migration.
alter table users
    add column contactable_by_members boolean;
