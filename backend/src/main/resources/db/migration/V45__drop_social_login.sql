-- Strava login is gone, and it was the only social provider: V25's tables have no reader left.
-- Accounts migrated from biketeam without an email keep their strava_<athleteId>@… placeholder
-- address, from which the athlete id can still be read back if it is ever needed again.

drop table social_login_codes;
drop table social_oauth_states;
drop table user_social_identities;
