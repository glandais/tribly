-- The timezone preference, beside theme/language (V28): same rationale — nullable, "not chosen"
-- meaning "follow the client / the browser". No check constraint: unlike theme's fixed enum, IANA
-- timezone IDs (e.g. "Europe/Paris") aren't a short, stable list a SQL check can meaningfully pin
-- down, so validation happens server-side instead (java.time.ZoneId.of(...)).

alter table users
    add column timezone varchar(40);
