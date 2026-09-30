-- docs/LEDGER_*.md SEC-17: a calendar feed token dies after a long silence instead of never. A
-- calendar app polls its subscription for ever, so a fixed lifetime would cut live subscriptions
-- without a word; inactivity only reaps the tokens nobody polls any more.
--
-- Nullable, so the previous release, which does not know the column, can still create tokens during
-- a rolling deploy: a null reads as the creation date. Every existing token starts a fresh period
-- today, so no subscription alive at the deploy is cut by it.
alter table calendar_tokens add column last_used_at timestamptz;

update calendar_tokens set last_used_at = now();
