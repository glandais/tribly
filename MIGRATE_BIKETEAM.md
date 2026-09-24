# Biketeam → Pédalons migration

> **Two procedures live here.** The [live migration](#live-migration) is the current one: a biketeam
> team admin moves *their* team from biketeam's admin, server to server over HTTPS, without people. Everything from
> [Reset](#reset) down to the end describes the **legacy dump import** (a restored `biketeam_import`
> database + a copy of the data directory + the `backend-restore` service), which is **deprecated**
> and marked `REMOVE-WITH-LEGACY-BIKETEAM-IMPORT` for removal — except the mapping rules
> ([Replaying](#replaying), [Known failures](#known-failures), [Ordering](#ordering-of-groups-and-stages),
> [Visibility](#visibility), [Dates](#dates), [Team pages](#team-pages), [Team logos](#team-logos)),
> which the live migration applies unchanged. Design and contract with biketeam:
> [docs/plans/2026-09-22-biketeam-live-migration.md](docs/plans/2026-09-22-biketeam-live-migration.md).

# Live migration

A biketeam team admin opens *Migrer vers Pédalons* in their team's biketeam admin, chooses a **trial**
(`dryRun`, ticked by default) or the real thing, and optionally a **reset**. Biketeam sends the browser
to `/migration-biketeam?request=<signed token>` here; the admin signs in (or signs up) on Pédalons,
reads what will be imported and confirms. Pédalons mints a single-use grant and sends the browser
back to biketeam, which redeems it over HTTPS (`POST /api/internal/biketeam-migration/jobs`) and
polls the job. The worker fetches the team's snapshot and files from biketeam's export API
(`/internal/pedalons/…`), maps them with the rules below, and hands biketeam a URL table. A
successful non-trial run, once the admin clicks *Basculer vers Pédalons*, makes biketeam redirect
every URL of the team here — with a 302 by default, a 301 once `PEDALONS_REDIRECT_STATUS=301` is set
on biketeam after the switches have settled — and turn the team read-only; nothing is deleted on
either side.

What comes over: the team (name, visibility, `joinable`), its about page (presentation + contact
details), its FAQ page, its logo (not the placeholder), places, routes with their GPX, ride
templates, publications (as posts), rides with their groups, trips with their stages, and their
images. What does **not**: users, memberships, participations, comments, registrations, route
ratings and favourites. The Pédalons account that confirmed becomes the team's **ADMIN** and the
author (`createdBy`) of everything; members join afterwards through the usual invitation link or,
for a `joinable` team, on their own. Ride groups have no leader (`RideGroupDto.leader` is null).

The team lands in the domain the admin confirmed on (its parent domain, if they came through an
alias), at slug = biketeam team id.

| Situation at the slug | Trial / real run | With `reset` |
|---|---|---|
| free | team created | same |
| a team migrated earlier from this biketeam team | updated in place (replay) — only by its Pédalons ADMIN or a PLATFORM_ADMIN | trashed (`deleted`, slug renamed `<slug>-reset-<jobId>`) then created anew — refused while a domain alias is pinned on it |
| that team, already in the trash | trashed team set aside, created anew | same |
| any other team (a native Pédalons team) | **refused** (`BIKETEAM_SLUG_CONFLICT`), never touched | same |
| this biketeam team was migrated into another domain of this database | **refused** (`BIKETEAM_MIGRATED_IN_OTHER_DOMAIN`) | same |

A **trial** is a real migration — the team really exists afterwards, visible according to its
biketeam visibility — that biketeam simply does not switch over to. Replays are idempotent (same
`biketeam_migration_map` as the legacy import, now tagged with `biketeam_team_id`), so the real run
after a trial re-creates nothing: it resynchronises what changed on biketeam meanwhile, and skips
the whole GPX pipeline — without even downloading — for every route whose file has the same
`size:md5` fingerprint, including routes the legacy import built.

## Turning it on

Off until **all five** variables are set in the backend's `.env` (documented, empty, in
`.env.example`); only some of them stops the backend at startup, naming the missing ones.

| Variable | Value |
|---|---|
| `PEDALONS_BIKETEAM_REQUEST_KEY` | `openssl rand -base64 32` — same value as biketeam's `PEDALONS_REQUEST_KEY` |
| `PEDALONS_BIKETEAM_TRIGGER_SECRET` | `openssl rand -base64 32` — same as biketeam's `PEDALONS_TRIGGER_SECRET` |
| `PEDALONS_BIKETEAM_EXPORT_URL` | biketeam's public URL, **HTTPS mandatory**, e.g. `https://www.prendslaroue.fr` |
| `PEDALONS_BIKETEAM_EXPORT_SECRET` | `openssl rand -base64 32` — same as biketeam's `PEDALONS_EXPORT_SECRET` |
| `PEDALONS_BIKETEAM_PUBLIC_URL` | biketeam's public `site.url`, e.g. `https://www.prendslaroue.fr` |

Order, on a new deployment:

1. Deploy Pédalons, then biketeam, both without any of these variables: nothing changes.
2. Secrets on both sides, restart both. There is no VPN: each side calls the other through its
   normal public HTTPS entry point — Pédalons' `/api/internal/` and biketeam's `/internal/pedalons/`
   are reachable from the Internet, guarded by the shared secrets, the single-use grant, and (for the
   export) a migration in flight for that team. Both URLs must be `https://`.
3. The backend log says
   `Biketeam live migration enabled (export …, public site …)`.
4. A trial of a small team (`gaby`) against **staging**, then against production, then the real run.

`pedalons.biketeam.grant-ttl` (10 min), `.max-attempts` (3), `.stuck-after` (20 min),
`.export-idle-timeout` (60 s without a byte of a snapshot or file) and `.export-transfer-timeout`
(10 min for one snapshot or file) have defaults in `application.properties`.

## Following a job

Everything is in `biketeam_migrations` — one row per confirmed request, first a grant (`GRANTED`,
`EXPIRED`), then a job (`QUEUED`, `RUNNING`, `SUCCEEDED`, `FAILED`). Biketeam shows the same
status on its admin page; the backend logs one line per transition.

```sql
select id, biketeam_team_id, dry_run, reset, status, attempts, progress, error_code, error_message,
       queued_at, started_at, finished_at
from biketeam_migrations order by created_at desc limit 20;

-- counts and warnings of the latest job of a team
select result -> 'counts', result -> 'warnings'
from biketeam_migrations where biketeam_team_id = 'n-peloton' order by created_at desc limit 1;
```

One job at a time per instance (the worker ticks every 10 s), one active job per biketeam team. A
network or 5xx failure of the export is retried after 2, then 4 minutes; a job silent for
`stuck-after` (a killed backend) is started over — the replay is cheap. Grants never redeemed become
`EXPIRED` within the hour. No row is ever deleted.

| `error_code` | Meaning |
|---|---|
| `BIKETEAM_SLUG_CONFLICT` | A native Pédalons team holds the slug: rename it on Pédalons, then retry from biketeam. |
| `BIKETEAM_MIGRATED_IN_OTHER_DOMAIN` | This biketeam team already lives in another domain of this database. |
| `BIKETEAM_NOT_TEAM_ADMIN` | The confirming account does not administer the existing team (any more). |
| `BIKETEAM_RESET_BLOCKED` | A domain alias is pinned on the team a reset would trash. |
| `EXPORT_REFUSED` | Biketeam answered 4xx — wrong `PEDALONS_BIKETEAM_EXPORT_SECRET`, or no migration in flight on its side. |
| `EXPORT_UNAVAILABLE` | Biketeam unreachable or 5xx after every attempt — check `PEDALONS_BIKETEAM_EXPORT_URL` and that biketeam answers on it. |
| `EXPORT_INVALID` | Unreadable snapshot, unexpected `schemaVersion`, or the snapshot of another team. |
| `WORKER_LOST` | No heartbeat, and no attempt left. |
| `INTERNAL_ERROR` | Anything else — the backend log has the stack trace. |

A job that `SUCCEEDED` may still count per-element failures (`counts.*.failed`, with a warning each:
`GPX_MISSING`, `GPX_EMPTY`, `GPX_FAILURE`, `FILE_DOWNLOAD_FAILED`, `IMAGE_FAILED`, `ITEM_FAILED`) —
the same element-level error boundary as the legacy import, which lost 25 routes of the 2026-07
dump to missing or broken GPX files. Read them after the trial; whether to go on is a human call.
One warning is not a failure: `TRIP_STAGES_OUTSIDE_DATES` flags a trip whose stages fall outside its
biketeam start/end dates — migrated as is, but Pédalons ends a trip with its last stage, so its
biketeam end date is lost. Fix the dates on either side.

## Both applications on a workstation

Pédalons as usual (`mvn quarkus:dev` on 8080, `pnpm dev` on 5173), biketeam on **8081**
(`SERVER_PORT=8081`). In dev, plain `http://localhost` URLs are fine.

```bash
# Pédalons backend, on top of `source scripts/dev-env.sh`
export PEDALONS_BIKETEAM_REQUEST_KEY=…   PEDALONS_BIKETEAM_TRIGGER_SECRET=…   PEDALONS_BIKETEAM_EXPORT_SECRET=…
export PEDALONS_BIKETEAM_EXPORT_URL=http://localhost:8081
export PEDALONS_BIKETEAM_PUBLIC_URL=http://localhost:8081   # = biketeam's SITE_URL
# biketeam: PEDALONS_URL=http://localhost:5173, PEDALONS_INTERNAL_URL=http://localhost:8080, same three secrets
```

## Tests

Not run by the assistant (see CLAUDE.md): run them yourself.

```bash
cd backend
mvn test -Dtest='BiketeamRequestTokenVerifierTest,BiketeamMigrationResourceTest,BiketeamMigrationDisabledTest,BiketeamMigrationInternalResourceTest,BiketeamLiveMigrationTest'
```

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Reset": legacy dump import only — delete it down to the next heading. -->
# Reset

Wipes the stack and its data — postgres, minio, and the assets written to `./data/storage`.

docker compose --profile restore down -v --remove-orphans
rm -rf ./data/storage/*
docker compose up -d

`./data/cache` is deliberately left alone: it holds gpx2web's map tiles and the elevation tiles
fetched from tiles.mapterhorn.com, keyed by coordinates, so it stays valid across a reset and saves
the bulk of a run's rendering. Delete it only to reclaim disk.

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Backup data": legacy dump import only — delete it down to the next heading. -->
# Backup data

`scripts/biketeam_fetch.sh` does the whole fetch-and-restore: rsync of the production directory,
`pg_dump` on the remote host, rsync of the dump, then `biketeam_restore.sh`.

./scripts/biketeam_fetch.sh

The remote postgres password is read from the `.env` that comes with the production directory, so
nothing has to be typed. `--skip-files` redoes only the dump (the data directory is the slow part),
`--skip-db` only the files, `--no-restore` stops after fetching. `--dest` moves the export
elsewhere; it defaults to `../biketeam-backup`, next to the checkout, which is what the
`backend-restore` service mounts.

That is the same sequence as, by hand:

rsync -avz biketeam@main.tomacla.info:/home/biketeam/production ../biketeam-backup/

cat ../biketeam-backup/production/.env | grep POSTGRES_PASSWORD

ssh biketeam@main.tomacla.info
pg_dump -Fc -U biketeam -d biketeam_production -h localhost -f /tmp/biketeam_export.dump
(password is POSTGRES_PASSWORD from cat above)

rsync -avz biketeam@main.tomacla.info:/tmp/biketeam_export.dump ../biketeam-backup/

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Restore the dump into biketeam_import": legacy dump import only — delete it down to the next heading. -->
# Restore the dump into biketeam_import

The compose postgres publishes `POSTGRES_HOST_PORT` (default 5432) on 127.0.0.1, so the
script reaches it from the host. User and password default to POSTGRES_USER /
POSTGRES_PASSWORD, read from .env.

./scripts/biketeam_restore.sh ../biketeam-backup/biketeam_export.dump

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Run the migration": legacy dump import only — delete it down to the next heading. -->
# Run the migration

The `restore` profile starts a second backend that migrates `biketeam_import` into the
main database at startup. It reads the GPX/images from `../biketeam-backup`, mounted
read-only at `/mnt/biketeam`. It is not routed through traefik.

Like `backend`, it joins the `pedalons-shared` network to render thumbnails and elevation
profiles, so the shared stack must already be running (see Deployment in README.md) —
otherwise compose refuses to start it, `pedalons-shared` being declared external.

It runs the same `pedalons-backend:${ENV_NAME}` image as the `backend` service, so build it
first — the migration code only exists in a locally built image:

./build.sh

The migration runs once on boot and then stops the application, so the container exits on its own.
Run it in the foreground to get the logs and the exit code — 0 when every team made it through,
1 when any of them failed:

docker compose --profile restore run --rm backend-restore

Or detached, if you would rather not hold the terminal for an hour:

docker compose --profile restore up -d backend-restore
docker compose logs -f backend-restore

Config lives in the `backend-restore` service in docker-compose.yml. Set
`PEDALONS_MIGRATION_BIKETEAM_EXIT_WHEN_DONE=false` to keep the application up afterwards; `%dev`
already does, since there the migration is a step of a server you asked to keep running.

## Replaying

Re-running is safe and cheap. Already-migrated rows are matched through the biketeam→tribly id
mapping table, so a replay repairs what a previous run left missing rather than duplicating it.
Verified on `louise` (165 routes, 5 rides, 19 trips, 124 stages): seven consecutive runs, every row
count identical from the second onwards — only the Hibernate `version` column moves.

Most of a run is the GPX pipeline: parse, SRTM elevation, Douglas-Peucker, FIT, two thumbnails, five
S3 uploads — 92% of `louise`'s replay before this was addressed. `biketeam_migration_map` therefore
records the size and MD5 of the `.gpx` each route was built from, and a replay whose file still
digests the same skips the pipeline entirely, refreshing only the name, surface and visibility.
The fingerprint is written *after* the pipeline succeeds, so a run killed mid-upload leaves none and
the next one redoes the work.

| `louise` | duration |
|---|---|
| first import | 476s |
| replay, cold cache and no fingerprints | 161s |
| replay | **11s** |

What remains is the ride and trip thumbnails, which `updateRide`/`updateTrip` regenerate
unconditionally. A route whose `.gpx` changed between two dumps is reprocessed, as it should be.

## Known failures

A full local run of the 2026-07 dump (187 teams, ~70 min) loses 25 routes, for reasons that predate
the migration and cannot be fixed here. Everything else reconciles exactly against the source.

| What | Count | Cause |
|---|---|---|
| Routes | 22 | The `.gpx` file is simply missing from the export — the `map` row points at nothing (`GPX_EMPTY`). |
| Routes | 3 | Emoji in the track/waypoint name, written by biketeam as two separate UTF-16 surrogate character references (`&#55357;&#56629;`), which is not valid XML (`GPX_FAILURE`). |

No team failed, and the 25 lost routes were referenced by no ride and no trip stage.

That same run also lost 3 trips and their 22 stages to a `uk_team_entity_slug` collision, since
fixed: `TripStage`'s constructor minted the slug as `"stage-" + System.currentTimeMillis()` and no
one ever replaced it, so two stages created in the same millisecond collided — and every surviving
stage carried a timestamp for a slug. `TripService` now derives it from the stage name, like every
other entity.

`FileTypeDetector` also logs one WARN per generated FIT file (~5400 of them): Magika has no
signature for FIT, so it falls back to the extension and **accepts** the upload. Harmless noise.

## Ordering of groups and stages

Biketeam stores no order: it sorts in Java, at render time. The reader reproduces those comparators
so tribly's `sortOrder` — the index in the request list — matches what biketeam displayed.

| Read by the migration | Biketeam's comparator | Shown by |
|---|---|---|
| `ride_group` | `Ride.getSortedGroups()` — meeting time, then name | `ride.ftlh` |
| `trip_stage` | `Trip.getSortedStages()` — date, then name | `trip.ftlh` |
| `ride_group_template` | `RideTemplate.getSortedGroups()` — **name alone**, no time | admin form |

The name is compared with `COLLATE "C"`, not the dump's `en_US.utf8`: biketeam uses
`String::compareTo`, which is code point order, and `"C"` is the only collation that reproduces it
whatever locale the database was created with. Verified against the 2026-07 dump — the two orders
agree on all 849 rides, 152 trips and 26 templates. `id` breaks exact ties, where biketeam sorts a
`HashSet` and has no defined order of its own.

Nothing sorts in `RideService`/`TripService`: there, `sortOrder` is the order a human dragged them
into.

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Which teams get migrated": legacy dump import only — delete it down to the next heading. -->
## Which teams get migrated

`team-id` names one biketeam team, and — since biketeam ids are already slugs — the tribly
team it becomes. Leave it unset and every live biketeam team is migrated, each in its own
error boundary: a team that blows up is logged and the run moves on to the next one.

The migration admin (`admin-email`) is only made PLATFORM_ADMIN of the target domain —
enough for `SecurityVerifier` to let it write through the normal services, without joining
any team. Team membership comes from biketeam's own `user_role` rows. Four biketeam teams
have no admin of their own and end up with none; the platform admin can still manage them.

## Visibility

Tribly gates content on two fields at once (`TeamEntityRepository.getPublicEntity`): listing needs
`team.visibility = 'PUBLIC'` **and** `te.visibility = 'PUBLIC'`; a direct link needs both to be
anything other than `TEAM`.

Biketeam's only per-item flag is `ride.listed_in_feed` / `trip.listed_in_feed`, which hides an item
from the team feed while a direct link still opens it — exactly `PUBLIC_UNLISTED`. Routes, posts
and ride templates have no such flag and are always listed, so they map to `PUBLIC`.

Item visibility comes from the item's own flag, never from the team's unlisted-ness. Pushing the
team's `PUBLIC_UNLISTED` down onto its content would change nothing today, but it would stick:
promoting that team to `PUBLIC` later would leave its whole feed hidden. A `TEAM` team clamps
everything under it to `TEAM`, which `validateVisibility` requires anyway.

| biketeam `team.visibility` | tribly | teams |
|---|---|---|
| `PUBLIC` | `PUBLIC` | 63 |
| `PUBLIC_UNLISTED` | `PUBLIC_UNLISTED` | 7 |
| `USER` (personal space) | `PUBLIC_UNLISTED` | 105 |
| `PRIVATE` | `TEAM` | 4 |
| `PRIVATE_UNLISTED` | `TEAM` | 8 |

Both PRIVATE flavours mean "members only", which is `TEAM`; tribly has no unlisted-and-private,
so that distinction is dropped. `USER` marks a personal training space that biketeam never lists,
yet `Team.isPublic()` returns true for it, so anyone with the link can read it — `PUBLIC_UNLISTED`
is the faithful translation. An unknown value maps to `TEAM`, the most restrictive.

`Team.joinable` follows: biketeam puts `/join` behind `authorizePublicAccess`, so a `TEAM` team
cannot be self-joined.

The migration reads and writes private teams as PLATFORM_ADMIN without joining them:
`TeamEntityRepository` skips the whole visibility filter when `query.platformAdmin()` is set.

## Dates

`BaseEntity.createdAt` is a `@CreationTimestamp` mapped `updatable = false`: Hibernate stamps it on
insert and never writes it again, so the migration restores the biketeam date with a plain SQL
update right after each insert.

| tribly | biketeam source |
|---|---|
| `Team.createdAt` (and its about page, and its FAQ page) | `team.created_at` (a date → midnight Paris) |
| `Route.createdAt` and `dateTime` | `map.posted_at` (a date; biketeam has no finer timestamp) |
| `Ride` / `Trip` / `Post` `.createdAt` | their `published_at` |
| `Ride.dateTime` | `ride.date` + earliest group meeting time |
| `Trip.dateTime` | `trip.start_date` + `meeting_time` |
| `TripStage.dateTime` | `trip_stage.date` + **an invented time** — see below |
| `Post.dateTime` | `publication.published_at` |
| `Comment.createdAt` | `message.published_at` |

`Comment` matters most: it has no business date, `CommentDto` exposes `createdAt` and
`CommentRepository` sorts on it, so without this the whole 2021→2026 discussion history would
collapse onto the migration timestamp.

Places, users, memberships and participations carry no date in biketeam, so theirs is the
migration time.

### Trip stage departures

`trip_stage` holds a bare `date` and no time at all — a biketeam trip's only time is
`trip.meeting_time`, the rendezvous of the whole trip. Tribly's `TripStage.dateTime` is an `Instant`
that `TripStageCard` and `StageDetailPage` both render down to the minute, so leaving it at the
day's start would print "à 00:00" on every migrated stage.

The **first** stage therefore takes `trip.meeting_time`, which is precisely what that column meant,
and the **later** ones get **8:00** — a convention for a departure on the road, not a claim about
the source, which says nothing on the subject. Stages arrive sorted by biketeam's own comparator
(date, then name), so index 0 really is the first day.

### Timezones

Biketeam stored time-of-day as `time without time zone` (`ride_group.meeting_time`,
`trip.meeting_time`) and business dates as bare `date`, resolving both against
`team_configuration.timezone` at render time (`Team.getZoneId()`). The migration hardcodes
`Europe/Paris` instead, and that is exact for the 2026-07 dump: 186 of the 187 teams are on
`Europe/Paris`, and the one that isn't — `allonsrouler974`, on `Indian/Reunion` — has zero rides,
zero trips, zero routes and zero posts, so no date is read through it. Revisit the constant if a
later dump has a populated team outside Paris.

The live migration no longer assumes Paris: the export carries `team_configuration.timezone`, and
every bare date and time of the team is read in it (falling back to `Europe/Paris` if it is missing
or unreadable).

Only the four `published_at` columns (`ride`, `trip`, `publication`, `message`) are true instants,
stored `timestamp with time zone`; those need no zone and are copied straight across.

## Team pages

Biketeam had exactly two pieces of free-form team prose, and tribly's `TeamPage` takes both:

| biketeam | tribly |
|---|---|
| `team_description` (presentation + contact details) | the team's **about** page, rendered to markdown |
| `team_configuration.markdown_page` | an **additional** page titled *FAQ* |

`markdown_page` is what biketeam's `FAQController` served at `/{teamId}/faq`, under that fixed
title — the schema has no other page table and no per-page title, so *FAQ* is the whole of it. Six
teams of the 2026-07 dump have one (`n-peloton`, `audax-lavallois`, `mollet_qui_pique`,
`la_petite_amicale_du`, `tomacla`, `malika`); the other 181 get no additional page.

The column already holds Markdown, so it is copied across as-is — only CRLF is normalised, with
none of the hard-break rewriting `team_description` needs. It lands at
`/equipes/{team}/pages/faq`, published, at the team's own visibility, dated to the team's creation
like the about page.

Written through `TeamPageRepository` rather than `TeamPageService.createPage`: that path enforces a
three-additional-pages cap. Biketeam can never supply more than one, but the cap counts pages an
earlier run already created. Replays match on the `TEAM_PAGE` mapping keyed `<teamId>:faq` — the
bare `<teamId>` is the about page — and refresh the markdown in place.

**Internal links are not rewritten.** `n-peloton`'s page links to
`https://www.prendslaroue.fr/n-peloton/faq#equipement` and to its old home page; those stay pointing
at biketeam and have to be fixed by hand, or by the team, once the old site goes away.

### Trip notes

A trip had its own free-form page too — `trip.markdown_page`, served at `/{team}/trips/{id}/notes`.
Tribly has no per-trip page, so it becomes the tail of the trip's description, under a `## Notes`
heading. Like the FAQ it is already Markdown: only CRLF is normalised. The description is rebuilt
from the source on every run, so a replay never stacks a second section.

## Team logos

Biketeam kept the team logo at `misc/<teamId>/logo.png|jpg`. Tribly has no logo column: `TeamAvatar`
reads `team.about.assets.logo`, so the file is imported as a `LOGO` asset on the team's about page.
No `::asset{}` directive is added — a logo is addressed through `assets.logo`, not from the markdown.

Biketeam handed every new team a copy of its `default-images/empty.png` placeholder, so the file
being present means nothing: **70 of the 187 exported teams never replaced it**, leaving 117 real
logos. Those 70 are skipped by comparing the file digest against the placeholder, which leaves
tribly's initials avatar in place. `heatmap.png`, which sits in the same directory, is never picked
up, and neither is `misc/logo.png` — that one is biketeam's own platform logo, not a team's.

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Members without an email": legacy dump import only — delete it down to the next heading. -->
## Members without an email

Biketeam let people sign in through Strava, Facebook or Google without ever giving an
email; tribly requires one. Those accounts are migrated with a placeholder address —
`strava_<stravaId>@pedalons.fr`, `facebook_<id>@…`, `google_<id>@…` — under
`placeholder-email-domain`. The address is unique and stable across replays but is not
deliverable, so the account is left **unverified** and cannot log in until its owner
claims it. This keeps their memberships, ride participations and comments; skipping them
would have dropped roughly 60% of n-peloton's participation history.

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Verified emails and passwords": legacy dump import only — delete it down to the next heading. -->
## Verified emails and passwords

Biketeam's old profile form accepted any address, so when it added email/password login it
reset `email_verified` to false on every account. A verified email opens OTP login and
password reset in tribly, so the migration only marks it verified on proof: biketeam's own
`email_verified`, or a Google/Facebook identity on the account — tribly has neither login,
and without this those members would have no way in. A real address without either proof
is migrated **unverified**.

The BCrypt `password_hash` is copied as is (Spring's `$2a$` is readable by `BcryptUtil`),
but only on a verified email: tribly's password login does not check verification, so a
password set on someone else's address would open that account.

Biketeam's case-insensitive email deduplication cleared the address of every "losing"
account and logged it in `user_email_conflict`. A loser with an external id stays a separate
(placeholder) account, as in biketeam; one without is folded into the account that kept the
address, so its history is not dropped — only its data follows, never a login method.

A dump taken before those biketeam changes has neither the columns nor the table; the reader
detects that, and every address is then only verified through a Google/Facebook identity.

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Running the migration from dev mode instead": legacy dump import only — delete it down to the next heading. -->
## Running the migration from dev mode instead

PEDALONS_MIGRATION_BIKETEAM_ENABLED=true \
PEDALONS_MIGRATION_BIKETEAM_TEAM_ID=n-peloton \
PEDALONS_MIGRATION_BIKETEAM_DATA_DIR=/home/glandais/code/perso/biketeam-backup/production/data \
PEDALONS_BOOTSTRAP_DOMAIN=localhost \
PEDALONS_BOOTSTRAP_DOMAIN_NAME=Pédalons \
PEDALONS_BOOTSTRAP_BASE_URL=https://localhost:5173 \
PEDALONS_BOOTSTRAP_ADMIN_EMAIL=gabriel.landais@gmail.com \
BIKETEAM_DB_URL=jdbc:postgresql://localhost:5432/biketeam_import \
BIKETEAM_DB_USER=pedalons \
BIKETEAM_DB_PASSWORD=pedalons_dev_password \
mvn quarkus:dev -Dquarkus.console.disable-input=true

Note the credentials differ from the compose stack: `%dev` talks to a `pedalons` database
owned by `pedalons`, whereas docker-compose.yml runs `tribly` / `${POSTGRES_USER}`. Restore
the dump into whichever postgres the dev profile points at.

<!-- REMOVE-WITH-LEGACY-BIKETEAM-IMPORT — section "Configuration": legacy dump import only — delete it down to the next heading. -->
## Configuration

The target domain and the admin account are **not** migration settings. `pedalons.bootstrap.*`
owns them, and the migration calls `BootstrapService` to get them — so `bootstrap.domain` is
where the data lands, and `bootstrap.admin-email` is the PLATFORM_ADMIN it writes as. Both are
required; the migration aborts if either is blank.

Nothing is defaulted in `application.properties`: `%dev` carries the dev values, and every
deployment passes `PEDALONS_BOOTSTRAP_*` through `.env`. docker-compose.yml restates them with
`${VAR:?}`, so a missing one stops `docker compose up` rather than quietly creating a Domain
under the wrong hostname. Note that `.env` is *sourced* by `build.sh` and
`scripts/biketeam_restore.sh` — quote any value containing a space.

`bootstrap.base-url` is not the tenant key; `bootstrap.domain` is, matched against
`X-Forwarded-Host`/`Host`. base-url only builds absolute URLs: the links in emails and the
WebAuthn origin of passkeys. Both, along with `domain-name`, are read **only when the Domain row
is created** — changing the env var afterwards has no effect.

Of the migration's own settings, `data-dir` is optional (unset skips GPX tracks and images),
and dropping `team-id` migrates every team instead of a single one.
