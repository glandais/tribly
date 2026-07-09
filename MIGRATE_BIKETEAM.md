# Reset

Wipes the stack and its data — postgres, minio, and the assets written to `./data/storage`.

docker compose --profile restore down -v --remove-orphans
rm -rf ./data/storage/*
docker compose up -d

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

It runs the same `pedalons-backend:${ENV_NAME}` image as the `backend` service, so build it
first — the migration code only exists in a locally built image:

./build.sh

docker compose --profile restore up -d backend-restore
docker compose logs -f backend-restore

The migration runs once on boot; the container then keeps running as an idle Quarkus
app. Stop it yourself once the logs show `Biketeam migration completed`:

docker compose --profile restore down backend-restore

Config lives in the `backend-restore` service in docker-compose.yml. Re-running it is
safe: already-migrated rows are matched through the biketeam→tribly id mapping table,
and a replay repairs rows that a previous run left missing.

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
