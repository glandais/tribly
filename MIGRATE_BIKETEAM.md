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
PEDALONS_MIGRATION_BIKETEAM_TARGET_DOMAIN=localhost \
PEDALONS_MIGRATION_BIKETEAM_TARGET_DOMAIN_NAME=Pédalons \
PEDALONS_MIGRATION_BIKETEAM_TARGET_DOMAIN_BASE_URL=https://localhost:5173 \
PEDALONS_MIGRATION_BIKETEAM_DATA_DIR=/home/glandais/code/perso/biketeam-backup/production/data \
PEDALONS_MIGRATION_BIKETEAM_ADMIN_EMAIL=gabriel.landais@gmail.com \
BIKETEAM_DB_URL=jdbc:postgresql://localhost:5432/biketeam_import \
BIKETEAM_DB_USER=pedalons \
BIKETEAM_DB_PASSWORD=pedalons_dev_password \
mvn quarkus:dev -Dquarkus.console.disable-input=true

Note the credentials differ from the compose stack: `%dev` talks to a `pedalons` database
owned by `pedalons`, whereas docker-compose.yml runs `tribly` / `${POSTGRES_USER}`. Restore
the dump into whichever postgres the dev profile points at.

`admin-email` is required — the migration aborts if it is blank. `target-domain-name`
and `target-domain-base-url` are optional: they default to the hostname and to
`https://{target-domain}`. `data-dir` is optional too; leaving it blank skips GPX tracks
and images.
