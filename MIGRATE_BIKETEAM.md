# Reset

Wipes the stack and its data — postgres, minio, and the assets written to `./data/storage`.

docker compose --profile restore down -v --remove-orphans
rm -rf ./data/storage/*
docker compose up -d

`./data/cache` is deliberately left alone: it holds gpx2web's map tiles and the elevation tiles
fetched from tiles.mapterhorn.com, keyed by coordinates, so it stays valid across a reset and saves
the bulk of a run's rendering. Delete it only to reclaim disk.

# Backup data

rsync -avz biketeam@main.tomacla.info:/home/biketeam/production ../biketeam-backup/

cat ../biketeam-backup/production/.env | grep POSTGRES_PASSWORD

ssh biketeam@main.tomacla.info
pg_dump -Fc -U biketeam -d biketeam_production -h localhost -f /tmp/biketeam_export.dump
(password is POSTGRES_PASSWORD from cat above)

rsync -avz biketeam@main.tomacla.info:/tmp/biketeam_export.dump ../biketeam-backup/

# Restore the dump into biketeam_import

The compose postgres publishes `POSTGRES_HOST_PORT` (default 5432) on 127.0.0.1, so the
script reaches it from the host. User and password default to POSTGRES_USER /
POSTGRES_PASSWORD, read from .env.

./scripts/biketeam_restore.sh ../biketeam-backup/biketeam_export.dump

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
| `Team.createdAt` (and its about page) | `team.created_at` (a date → midnight Paris) |
| `Route.createdAt` and `dateTime` | `map.posted_at` (a date; biketeam has no finer timestamp) |
| `Ride` / `Trip` / `Post` `.createdAt` | their `published_at` |
| `Ride.dateTime` | `ride.date` + earliest group meeting time |
| `Trip.dateTime` | `trip.start_date` + `meeting_time` |
| `Post.dateTime` | `publication.published_at` |
| `Comment.createdAt` | `message.published_at` |

`Comment` matters most: it has no business date, `CommentDto` exposes `createdAt` and
`CommentRepository` sorts on it, so without this the whole 2021→2026 discussion history would
collapse onto the migration timestamp.

Places, users, memberships and participations carry no date in biketeam, so theirs is the
migration time.

## Team logos

Biketeam kept the team logo at `misc/<teamId>/logo.png|jpg`. Tribly has no logo column: `TeamAvatar`
reads `team.about.assets.logo`, so the file is imported as a `LOGO` asset on the team's about page.
No `::asset{}` directive is added — a logo is addressed through `assets.logo`, not from the markdown.

Biketeam handed every new team a copy of its `default-images/empty.png` placeholder, so the file
being present means nothing: **70 of the 187 exported teams never replaced it**, leaving 117 real
logos. Those 70 are skipped by comparing the file digest against the placeholder, which leaves
tribly's initials avatar in place. `heatmap.png`, which sits in the same directory, is never picked
up, and neither is `misc/logo.png` — that one is biketeam's own platform logo, not a team's.

## Members without an email

Biketeam let people sign in through Strava, Facebook or Google without ever giving an
email; tribly requires one. Those accounts are migrated with a placeholder address —
`strava_<stravaId>@pedalons.fr`, `facebook_<id>@…`, `google_<id>@…` — under
`placeholder-email-domain`. The address is unique and stable across replays but is not
deliverable, so the account is left **unverified** and cannot log in until its owner
claims it. This keeps their memberships, ride participations and comments; skipping them
would have dropped roughly 60% of n-peloton's participation history.

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
