-- Display preferences the profile screen owns: theme and language, beside the unit system that
-- users.unit_system has carried since V2.
--
-- Server-side rather than client-side because the web keeps them in localStorage and the mobile app
-- keeps nothing at all: a member who sets imperial units on their phone gets kilometres on the web,
-- and a reinstall loses everything. One row, three columns, and every client agrees.
--
-- Both nullable, and deliberately so: null means "not chosen", which is not the same as SYSTEM or
-- as the domain's default language. A client reads null as "follow the device", and only a value
-- the user actually picked overrides that. A NOT NULL DEFAULT would have made every pre-existing
-- account look like it had chosen.

alter table users
    add column theme varchar(10);
alter table users
    add column language varchar(10);

alter table users
    add constraint users_theme_check check (theme in ('SYSTEM', 'LIGHT', 'DARK'));
