# Pedalons - Cycling Team Management Platform

Multi-tenant web platform for cycling teams to organize rides, trips, manage GPX routes with interactive maps, and communicate.

## Tech Stack

- **Backend**: Java 21, Quarkus 3.38.x, PostgreSQL 17 with PostGIS
- **Frontend**: TypeScript 7 (tsgo), React 19, Vite 8, Mantine UI 9
- **Mobile**: Flutter, Dart, Riverpod 3
- **Karoo**: Kotlin, Jetpack Compose, ktor-client-karoo
- **Garmin**: Monkey C, Connect IQ SDK
- **API**: OpenAPI 3.1 contract-driven development
- **Testing**: JUnit 5, REST Assured, Vitest

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 22+ (Vite 8 requires ^20.19 or >=22.12)
- pnpm — the version is pinned by `packageManager` in `frontend/package.json`; `corepack enable` honours it
- Docker 24+
- Docker Compose 2.20+

### 1. Clone and Setup

```bash
git clone git@github.com:glandais/tribly.git
cd tribly
cp .env.example .env
```

### The `.env`, on a workstation and on a server

There is **one** `.env`, never committed, and one template for both uses. Compose reads it, and
`docker-compose.yml` hands the whole file to the backend container through `env_file` — which is why
anything that has no business inside the application has no business in it either (the `BACKUP_*`
settings live in `/root/pedalons-backup.env` instead, see [Backup and restore](#backup-and-restore)).

A workstation and a deployment differ in six keys, and only those:

| Key | Deployment | Workstation |
|---|---|---|
| `ENV_NAME` | `pedalons-prod`, `pedalons-staging` | `tribly-local` |
| `COMPOSE_FILE` | *unset* — `docker-compose.yml` alone | `docker-compose.yml:docker-compose.local.yml` |
| `PEDALONS_EMAIL_BREVO_ENABLED` | *unset*, so `true`: sends through the Brevo API | `false` |
| `QUARKUS_MAILER_*` | the Brevo SMTP relay | `mailhog` / `1025`, TLS and login `DISABLED` |
| `PEDALONS_BOOTSTRAP_DOMAIN` / `_BASE_URL` | the public hostname, `https://…` | `localhost` / `http://localhost:8090` |
| `HTTP_PORT` | 8090 prod, 8089 staging, behind Caddy | anything free |

Two of those are not a matter of taste. **`ENV_NAME` names the stack** — containers, network, image
tags, and the `${ENV_NAME}-minio` the backup scripts inspect; a local stack called `…-prod` is
indistinguishable from the real one in `docker ps` and to `scripts/restore.sh`. And **a local stack
must not be able to send mail**, for reasons worth reading before the first `up`:
[Running the full stack locally](#running-the-full-stack-locally).

`COMPOSE_FILE` is what turns the checkout into a workstation. The overlay it adds carries mailhog,
the valhalla and tileserver a server instead gets from the shared stack, the loopback ports
`mvn quarkus:dev` and `pnpm dev` talk to, and an `app` profile on `backend`/`frontend`/`traefik` so a
plain `docker compose up -d` starts the backing services alone. `source scripts/dev-env.sh` then
feeds this same file's `POSTGRES_*` / `MINIO_*` to the dev backend — no second set of credentials to
keep in sync.

**Fill in before the first `up`:**

| Key | |
|---|---|
| `POSTGRES_PASSWORD` | any value; the database is created with it on first start |
| `MINIO_ROOT_PASSWORD` and `MINIO_SECRET_KEY` | must be **equal** — the second is how the backend authenticates against the first. Same for `MINIO_ROOT_USER` / `MINIO_ACCESS_KEY` |
| `ENCRYPTION_KEY` | `openssl rand -base64 32`. Encrypts the stored GPS-service tokens: change it later and they stop decrypting |
| `BREVO_API_KEY` | required even locally, even with Brevo disabled — the `%prod` profile expands it at startup, so a placeholder does |
| `PEDALONS_BOOTSTRAP_ADMIN_EMAIL` | your address. The account is created without a password; first login is by OTP or passkey |

**Everything else in `.env.example` has a working default**, so a workstation `.env` can be shorter
than the template: `PUID`/`PGID` (1000:1000), `POSTGRES_HOST_PORT` (5432), `FRONTEND_SOURCEMAP`
(false), `STRAVA_*` (blank hides the "Continue with Strava" button),
`SOCIAL_PLACEHOLDER_EMAIL_DOMAIN`, `QUARKUS_MAILER_USERNAME`/`_PASSWORD` (unread when the login is
`DISABLED`), and `VALHALLA_TILE_URLS` — whose default builds France, and whose every change costs a
rebuild of several hours.

### 2. Install and configure mkcert

```bash
# Windows (chocolatey)
choco install mkcert

# Windows (scoop)
scoop install mkcert

# macOS
brew install mkcert
```

Install local CA (one time):

```bash
mkcert -install
```

Generate certificates in the frontend folder:

```bash
cd frontend
mkcert localhost 127.0.0.1 192.168.50.20
# Creates localhost+2.pem and localhost+2-key.pem
```


### 3. Start Infrastructure

```bash
# PostgreSQL, MinIO, imgproxy, varnish, valhalla, tileserver, mailhog
docker compose up -d

# Wait for PostgreSQL to be ready
docker compose exec postgres pg_isready -U "$POSTGRES_USER"
```

One stack serves both workflows. `docker-compose.yml` is the deployment file; the workstation
overlay `docker-compose.local.yml` adds mailhog, folds in the valhalla and tileserver of
`docker-compose.shared.yml`, and publishes on loopback the ports `mvn quarkus:dev` and `pnpm dev`
talk to — imgproxy on 38080, valhalla on 8002, tileserver on 18080, MinIO on 9000, SMTP on 1025.

The command above starts the backing services **only**: the overlay puts `backend`, `frontend` and
`traefik` behind an `app` profile, since in dev those three are what you are replacing. Add
`--profile app` (or `COMPOSE_PROFILES=app` in `.env`) to run the whole application from the built
images — see [Running the full stack locally](#running-the-full-stack-locally). A deployment reads
`docker-compose.yml` alone, where the three carry no profile and always start.

### 4. Start Backend

```bash
cd backend
source ../scripts/dev-env.sh   # postgres + MinIO credentials, from the same .env the stack reads
./mvnw quarkus:dev
```

`dev-env.sh` exports those five values and nothing else — on purpose. Quarkus reads environment
variables at a higher ordinal than `application.properties`, so sourcing the whole `.env` would
override the `%dev` bootstrap domain (`localhost`, which is the WebAuthn origin of dev passkeys)
with the stack's own. If the `.env` still holds the `.env.example` credentials, the `%dev` defaults
already match and the `source` is a no-op.

Backend available at:
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health

### Default domain and platform admin

#### Bootstrapping


On startup the backend creates a default `Domain` and a `PLATFORM_ADMIN` `User` if they don't yet
exist (idempotent — see `BootstrapService`). Defaults match the localhost dev setup; override via
environment variables for other environments:

| Variable | Default |
|----------|---------|
| `PEDALONS_BOOTSTRAP_ENABLED` | `true` |
| `PEDALONS_BOOTSTRAP_DOMAIN` | `localhost` |
| `PEDALONS_BOOTSTRAP_DOMAIN_NAME` | `Pedalons Dev` |
| `PEDALONS_BOOTSTRAP_BASE_URL` | `https://localhost:5173` |
| `PEDALONS_BOOTSTRAP_ADMIN_EMAIL` | `gabriel.landais@gmail.com` |
| `PEDALONS_BOOTSTRAP_ADMIN_DISPLAY_NAME` | `Gaby Landais` |

The admin is created with `password_hash = NULL` — first login is via OTP or passkey.

#### SQL

Bootstrapping covers the domain you configured; nothing else resolves. Every request finds its
tenant from the `Host` header, so browsing through a *second* hostname — a LAN IP, say — needs its
own `domains` row. Open a `psql` prompt on the dev database:

```bash
docker exec -it "${ENV_NAME}-postgres" sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
```

Then insert a domain matching the host you browse the frontend with:

```sql
INSERT INTO domains (id, domain, name, base_url, single_team, active, deleted, created_at, updated_at, version)
VALUES (
    (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT,
    '192.168.50.20',
    'Pedalons',
    'https://192.168.50.20:5173',
    false,
    true,
    false,
    NOW(),
    NOW(),
    0
);
```

See [Running SQL](#running-sql) for the deployed stack, and [Bootstrapping a new deployment](#bootstrapping-a-new-deployment) for what to do next.

### 5. Start Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Frontend available at https://localhost:5173

The dev server proxies `/api` to **staging** (`https://staging.pedalons.fr`) unless
`VITE_API_TARGET` says otherwise, so front-end work needs no local backend at all. Point it at the
one you just started to work against local data:

```bash
echo 'VITE_API_TARGET=http://localhost:8080' >> frontend/.env
```

## Project Structure

```
tribly/
├── backend/          # Quarkus backend (Java 21) — also holds the dev-services compose
├── frontend/         # React 19 SPA (Mantine UI)
├── mobile/           # Flutter mobile app (iOS/Android)
├── karoo/            # Hammerhead Karoo extension (Kotlin/Compose)
├── garmin-app/       # Garmin Connect IQ app (Monkey C)
├── contracts/        # OpenAPI specifications
├── services/         # Docker service configs (valhalla, Varnish)
├── scripts/          # Utility scripts
├── data/             # Runtime data (segments, tileserver, keys)
├── docker-compose.yml         # One deployed environment (prod, staging, ...)
├── docker-compose.local.yml   # Workstation overlay: mailhog, the shared services, the
│                              #   loopback ports dev mode needs. Never deployed
└── docker-compose.shared.yml  # Services shared by every environment on the host
```

## Deployment

A host runs **one shared stack** plus **one stack per environment**, each from its own checkout
(`~/shared`, `~/prod`, `~/staging`) with its own `.env`. Caddy terminates TLS on the host and
reverse-proxies each hostname to that environment's traefik, published on loopback only
(`HTTP_PORT`: 8090 for prod, 8089 for staging).

`docker-compose.shared.yml` holds the services that carry no application data and are read-only:
`valhalla` (17 GB of OSM routing tiles) and `tileserver` (server-side raster rendering, ~1.8 GB
resident). One instance serves every environment. It owns the `pedalons-shared` Docker network.

`docker-compose.yml` holds everything that must stay isolated per environment: traefik, backend,
frontend, postgres, minio, imgproxy and varnish. Its `backend` also joins `pedalons-shared` to reach
the two shared services — under the same hostnames as before, since `valhalla` and `tileserver` are
their compose service names.

Start the shared stack **first**: `pedalons-shared` is declared `external` in `docker-compose.yml`, so
an environment fails to come up until it exists.

```bash
# once per host
cd ~/shared && docker compose -f docker-compose.shared.yml up -d

# then each environment
cd ~/prod && ./build.sh && docker compose up -d --remove-orphans
```

Two services stay per-environment on purpose, even though they look shareable:

- **imgproxy/varnish** — imgproxy only takes a single global `IMGPROXY_S3_ENDPOINT`, so one instance
  cannot serve two MinIO backends. They become shareable if and when MinIO is shared.
- **the vcyclist cache** (`DATA_CACHE_PATH`) — `DemTileFetcher` writes each downloaded tile to a temp
  file in the target directory, then moves it into place with `ATOMIC_MOVE` + `REPLACE_EXISTING`,
  and only after the bytes have decoded successfully. Two backends racing on the same tile just
  overwrite each other with identical bytes; a crash or a bad response never leaves a truncated or
  garbage file cached permanently. Keep it at `/mnt/cache`: pointed at `/tmp` it lives inside the
  container and is re-downloaded in full on every restart.

### Running the full stack locally

The same `docker-compose.yml`, on a workstation — for testing a build, or for running the biketeam
migration (see [MIGRATE_BIKETEAM.md](MIGRATE_BIKETEAM.md)). Two things must differ from a deployment,
and both live in the local `.env`:

```bash
ENV_NAME=tribly-local
COMPOSE_FILE=docker-compose.yml:docker-compose.local.yml
PEDALONS_EMAIL_BREVO_ENABLED=false
QUARKUS_MAILER_HOST=mailhog       # + the rest of the block in .env.example
```

**`ENV_NAME` names the stack** — the containers, the network, the image tags `build.sh` produces,
and the `${ENV_NAME}-minio` that `backup.sh` inspects. A local stack called `…-prod` is
indistinguishable from the real one in `docker ps` and to the backup scripts.

**A local stack must not be able to send mail.** The containers run the `%prod` Quarkus profile
wherever they run, and there `pedalons.email.brevo.enabled=true` sends through the Brevo *API* —
`QUARKUS_MAILER_*` is not even read. Disabling it falls back to SMTP, pointed at the mailhog of
`docker-compose.local.yml` (UI on http://127.0.0.1:8025). This is not hygiene: after a biketeam
migration the local database holds thousands of real member addresses, and one OTP or team
invitation is enough to reach them.

`COMPOSE_FILE` makes a plain `docker compose` command pick up the overlay here and nowhere else. A
deployed `.env` has no `COMPOSE_FILE` and reads `docker-compose.yml` alone, which is why the overlay
is a separate file rather than a profile. What it adds: mailhog (http://127.0.0.1:8025); valhalla
and tileserver, `extends`-ed from `docker-compose.shared.yml`; and the loopback ports
[dev mode](#3-start-infrastructure) talks to.

**No shared stack on a workstation.** A host runs one for several environments; a laptop has one, so
the overlay pulls those two services into it and redeclares the `shared` network as an ordinary
project network (`${ENV_NAME}-shared`) instead of the `external` `pedalons-shared`. Nothing to start
beforehand:

```bash
cd ~/code/tribly && ./build.sh && docker compose --profile app up -d
```

`--profile app` is what pulls in `backend`, `frontend` and `traefik`; without it the overlay starts
the backing services alone, which is what [dev mode](#3-start-infrastructure) wants. Put
`COMPOSE_PROFILES=app` in the `.env` if this machine mostly runs the full stack.

### Redacting credentials from access logs

Two endpoints carry a credential in the query string, because their client fetches them outside the
authenticated HTTP stack and cannot set a header:

- `?t=` on `/api/…/tiles/{z}/{x}/{y}.mvt` — the tile token, ~15 min (see `TileTokenService`). MapLibre
  fetches tiles itself, so this repeats on every tile: dozens of log lines per map session.
- `?token=` on the ICS calendar feed — this one does **not** expire, so it matters more.

Traefik and Caddy both log the full URI. Configure the host's Caddy access log to redact those two
parameters. The short TTL is what makes historical tile-token lines inert, and it is the reason the
TTL must never be raised to hours; the calendar token has no such protection.

### Seeding the shared Valhalla data

`~/shared/data/valhalla` is ~17 GB and takes hours to build from the `.osm.pbf`. On first start the
container hashes the directory and skips the build if it matches — the log then says *"Jumping
directly to the tile loading!"*, and anything mentioning a rebuild means the check failed. When an
environment already holds a built copy, move it rather than rebuild: keep `france-latest.osm.pbf`,
`valhalla_tiles.tar` and `file_hashes.txt` together, and note that `mv` cannot rename the
root-owned subdirectories (`elevation_data`, `valhalla_tiles`) out of a user-owned parent — do that
part from a root context:

```bash
docker run --rm -v /home/pedalons:/h alpine \
  mv /h/staging/data/valhalla/elevation_data /h/shared/data/valhalla/
```

### Changing an environment's network topology

Renaming or re-declaring a network makes compose (v5.3.1) drop the old one and create the new one
*during* the run, then fail on `network X was found but has incorrect label
com.docker.compose.network` — it labels the network with the map key but validates against the
resolved name. The run aborts halfway and can leave a container attached to **no network**, which
then fails with a misleading DNS error rather than an obvious one. So for any such change, do not
rely on a single `up -d`:

```bash
docker compose down --remove-orphans   # never -v: it drops the postgres and minio volumes
docker compose up -d
```

After an aborted run, check what a container is actually attached to before believing its logs:

```bash
docker inspect <container> --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}'
docker compose up -d --force-recreate <service>
```
## Backup and restore

`scripts/backup.sh` pushes one dated snapshot per run from a deployed environment to the backup
host; `scripts/restore.sh` brings an environment back from one, on the same machine or a new one;
`scripts/backup-prune.sh` expires old snapshots and runs **on the backup host**.

Production is backed up nightly to `optiplex` over the WireGuard tunnel (`10.10.0.1` → `10.10.0.2`).

### rsync is the only channel

The receiving account's key is restricted to `command="rrsync <root>",restrict,from="10.10.0.1"`:
it accepts an `rsync --server` invocation and refuses everything else, confined to one directory.
A compromise of the production host therefore stops at its own backup tree — it cannot read the
other backups on the host, and it cannot delete its own history. Three consequences run through the
scripts, and none of them are incidental:

- **the dumps are staged locally** (`/var/backups/pedalons/<env>`) before being pushed — there is no
  remote `cat >`; the staging directory is removed at the end of the run;
- **the previous snapshot is named explicitly** in `--link-dest`, because `rrsync` rejects any path
  containing `..`;
- **retention lives on the backup host** (`backup-prune.sh`, root's crontab there), not in the
  backup script.

### What a snapshot holds

`<root>/<UTC timestamp>/`:

| File | Contents | Why it matters |
|------|----------|----------------|
| `postgres.dump` | `pg_dump -Fc` of `$POSTGRES_DB` | Accounts, teams, rides, routes, posts |
| `minio/` | The object store, verbatim | Photos, GPX files, avatars, previews |
| `secrets.tar.gz` | `.env`, `data/keys/*.pem`, `data/storage` | The JWT keys sign every session and passkey; `ENCRYPTION_KEY` decrypts the stored Karoo/Garmin tokens |
| `MANIFEST` | Timestamp, env, git commit, image tags | Says which commit to rebuild before restoring |
| `SHA256SUMS` | Checksums of the two archives | Verified by `restore.sh` before it destroys anything |
| `COMPLETE` | Written last, after everything else landed | A dated directory without it is a failed run, not a backup — and the restricted key cannot delete it, so it has to be recognisable |

Postgres is dumped **before** MinIO on purpose: `AssetService` uploads to S3 and only then persists
the row, so a file landing mid-backup leaves an orphan object rather than a row pointing at a
missing one. `pg_dump -Fc` is transactional, and MinIO renames objects into place, so neither needs
the stack stopped.

Unchanged MinIO objects are hard-linked to the previous snapshot (`rsync --link-dest`): each dated
directory reads as a full copy but only costs its delta. Measured on production: a first snapshot
takes ~60 s and 1.7 GB; the next takes ~18 s and near-zero disk.

`.minio.sys/tmp/` is excluded: MinIO stages every write there, so rsync catches files mid-flight and
fails the run with exit 23. The rest of `.minio.sys` is *not* excluded — without `format.json` MinIO
does not recognise its own data. A run that still trips 23/24 (an upload landing during the backup)
gets one more rsync pass before it is called failed.

**Not** in a snapshot, and to be rebuilt by hand: the Docker images (`./build.sh` at the MANIFEST's
commit), `data/cache` (regenerable, just slow), and the shared valhalla/tileserver data (below).

### Configuration

`BACKUP_*` lives in **`/root/pedalons-backup.env`**, not in `.env`: compose hands the whole `.env`
to the backend container (`env_file`), so the destination, the key path and the Healthchecks URL
would end up inside the application — and inside the backup of it. Override the location with
`BACKUP_ENV_FILE`.

```bash
BACKUP_REMOTE=pedalonsbackup@10.10.0.2
BACKUP_REMOTE_PATH=/                     # relative to the rrsync root
BACKUP_SSH_KEY=/root/.ssh/id_pedalons_backup
BACKUP_KEEP=30
BACKUP_PING_URL=https://hc-ping.com/<uuid>
```

Reading the minio volume needs root, so the backup runs from root's crontab:

```
15 3 * * * cd /home/pedalons/prod && ./scripts/backup.sh >> /var/log/backup/pedalons-backup.log 2>&1
```

and on the backup host, after it — the checkout is refreshed in the same line, so the repository
stays the single source of truth instead of a copy that quietly drifts:

```
30 4 * * * cd /opt/pedalons-scripts && { git fetch -q --depth 1 origin develop && git reset -q --hard FETCH_HEAD ; } ; ./scripts/backup-prune.sh /home/backup-pedalons 30 >> /var/log/backup/pedalons-backup-prune.log 2>&1
```

`;` rather than `&&` between the two: a failed fetch must not skip the night's pruning, it just runs
the last version that landed. The checkout is read-only, shallow and sparse (5 MB, `scripts/` only):

```bash
git clone --depth 1 --single-branch --branch develop --no-checkout \
  https://github.com/glandais/tribly.git /opt/pedalons-scripts
cd /opt/pedalons-scripts && git sparse-checkout set --no-cone scripts && git checkout develop
```

The obvious shortcut — having `backup.sh` push the prune script along with the snapshot, the way
`backup.ns3085825` copies itself into its own backup — is a trap **here**: root's crontab on the
backup host would then execute a file the production host can overwrite, so a compromised production
host would get root execution on the backup host at 04:30. That is exactly what the `rrsync`
confinement exists to prevent. Pulling from the repository keeps the trust in git, where the
deployment already places it.

Both write into `/var/log/backup/`, which `scripts/pedalons-backup.logrotate` rotates daily and keeps
for 30 days — the same directory, glob and settings the backup host already uses for its other
backup logs, so one rule covers every machine:

```bash
mkdir -p /var/log/backup
install -m 644 scripts/pedalons-backup.logrotate /etc/logrotate.d/backup
logrotate -d /etc/logrotate.d/backup     # dry run
```

The scripts run with `BatchMode=yes` and never prompt, so an untrusted host key fails the run with a
bare `Host key verification failed`. Trust it once, as the user cron runs as.

`BACKUP_PING_URL` gets `/start` before the run and `/fail` (with the run log as the body) on error:
a backup nobody watches stops existing the day it starts failing.

### Setting up a new receiving account

On the backup host, as root — the model is `nsbackup` in `backup.ns3085825`:

```bash
adduser --system --group --home /home/pedalonsbackup --shell /bin/bash pedalonsbackup
mkdir -p /home/pedalonsbackup/.ssh /home/backup-pedalons
echo 'from="10.10.0.1",restrict,command="rrsync /home/backup-pedalons" ssh-ed25519 AAAA... backup-pedalons-prod' \
  > /home/pedalonsbackup/.ssh/authorized_keys
chown -R pedalonsbackup:pedalonsbackup /home/pedalonsbackup /home/backup-pedalons
chmod 700 /home/pedalonsbackup/.ssh && chmod 600 /home/pedalonsbackup/.ssh/authorized_keys
chmod 750 /home/backup-pedalons
```

Check the restriction actually bites — this must fail:

```bash
ssh -i /root/.ssh/id_pedalons_backup pedalonsbackup@10.10.0.2 'ls /'
# /usr/bin/rrsync error: SSH_ORIGINAL_COMMAND does not run rsync
```

### Restoring

```bash
scripts/restore.sh --list                    # complete snapshots, and failed runs marked as such
scripts/restore.sh                           # restore the newest complete one (asks to confirm)
scripts/restore.sh --snapshot 2026-07-24T031500Z
```

The restore pulls the snapshot to a local directory and verifies its checksums **before** touching
anything, then runs `docker compose down -v` — it drops the current postgres and minio volumes and
repopulates them. It refuses a snapshot with no `COMPLETE` marker, and refuses one from another
`ENV_NAME` unless `--force`.

It needs only `rsync` and `docker`, no root: objects go back through `docker cp`, which also hands
them to the container as `root:root` — the user MinIO runs as. Writing into
`/var/lib/docker/volumes` directly would stamp them with the restoring account's uid.

On a **new host**, the order matters — you need `.env` before anything else can read its own config:

```bash
git clone <repo> ~/prod && cd ~/prod

# 1. secrets first: no .env yet, so pass the coordinates in the environment
BACKUP_REMOTE=pedalonsbackup@10.10.0.2 BACKUP_REMOTE_PATH=/ \
BACKUP_SSH_KEY=/root/.ssh/id_pedalons_backup \
  scripts/restore.sh --secrets-only

# 2. the shared stack (see Deployment), then the images
cd ~/shared && docker compose -f docker-compose.shared.yml up -d
cd ~/prod && ./build.sh            # at the commit recorded in MANIFEST

# 3. the data
scripts/restore.sh
```

The script ends by printing row counts, the object count and the status of `GET /api/config`. The
last check is manual and the one that matters: open the site and confirm an existing photo renders —
that path goes MinIO → imgproxy → varnish, so it proves the objects came back, not just the rows.

Flyway replays any migration newer than the dump on the next boot, so restoring an old snapshot
under a recent image works; the reverse does not, which is why the MANIFEST records the commit. An
image older than the snapshot fails at boot, in a crash loop, with:

```
FlywayValidateException: Detected applied migration not resolved locally: 26.
```

The restore itself succeeded in that case — the data is in place and the script correctly refuses to
report success, since `/api/config` never answers 200. Rebuild at the MANIFEST's commit
(`./build.sh`) and `docker compose up -d`; nothing needs to be restored again.

#### Restore drill from another machine

The restricted key only allows the production host in (`from=`), so a drill elsewhere reads the same
store through an ordinary SSH account on the backup host — `BACKUP_REMOTE_PATH` is then the real
path instead of `/`. `--force` is what lets a `pedalons-prod` snapshot land in a differently-named
local environment:

```bash
cat > /tmp/drill.env <<'EOF'
BACKUP_REMOTE=root@192.168.50.95
BACKUP_REMOTE_PATH=/home/backup-pedalons
EOF
BACKUP_ENV_FILE=/tmp/drill.env scripts/restore.sh --force
```

### Cold backup of the shared stack

`~/shared/data/valhalla` is ~17 GB and takes hours to rebuild from the `.osm.pbf`. It carries no
application data, so it stays out of the nightly backup — but copying it **once** (and again
whenever the OSM extract changes) turns a multi-hour restore into an `rsync`:

```bash
rsync -a ~/shared/data/valhalla/{france-latest.osm.pbf,valhalla_tiles.tar,file_hashes.txt} \
      ~/shared/data/valhalla/elevation_data \
      backup-host:/path/to/pedalons-shared/
```

See [Seeding the shared Valhalla data](#seeding-the-shared-valhalla-data) for the permission trap
when moving those directories back.

## Features

### GPS Device Integration

Users can connect GPS devices from their profile to upload routes directly to their devices.

**Supported devices:**
- Hammerhead Karoo
- Garmin Edge devices (via Garmin Connect)
- Wahoo ELEMNT (via the Wahoo Cloud account — no companion app, the head unit syncs from the cloud)

**Setup:**

- Set `ENCRYPTION_KEY` for secure token storage (required in production)

**Usage:**
1. Navigate to Profile > GPS Devices
2. Click "Connect" next to your device
3. Authorize the application via Device Code Flow (QR code or URL)
4. On any route detail page, use "Send to Device" to upload routes

## Development

### Generate API Client

```bash
cd frontend
pnpm generate-api
```

### Generate UI Route Paths

UI routes (per-locale URL templates, deeplinks, path builders) are declared in `contracts/routes.yaml`. Regenerate `paths.generated.ts` / `paths.generated.dart`, AASA and the AndroidManifest deeplink section with:

```bash
cd frontend
pnpm generate-routes
```

See [APP_LINKS.md](APP_LINKS.md) for the full workflow.

### Run Tests

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && pnpm test
```

### Code Quality

```bash
# Format every module (Spotless, Prettier, dart format, ktfmt, prettier-plugin-monkeyc)
./format.sh
./format.sh mobile          # or one module: backend|frontend|mobile|karoo|garmin-app

# Backend linting
cd backend && ./mvnw checkstyle:check

# Frontend linting
cd frontend && pnpm lint
```

`format.sh` fails loudly when a toolchain is missing rather than skipping the module. Run it before
committing, and include its output in the commit.

## Running SQL

One PostgreSQL container, whichever way you run Pedalons: `${ENV_NAME}-postgres` —
`tribly-prod-postgres`, `tribly-local-postgres`, … Its credentials come from `.env`, which is not
versioned. The container name follows `ENV_NAME`, so read it from `docker ps` rather than assuming,
and read the credentials from the container's own environment, which keeps secrets out of your shell
history:

```bash
# Interactive session
docker exec -it "${ENV_NAME}-postgres" sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'

# One-off statement
docker exec "${ENV_NAME}-postgres" sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=1 -c "SELECT domain, name, active FROM domains;"'
```

It also publishes `127.0.0.1:${POSTGRES_HOST_PORT:-5432}` — that is how `scripts/biketeam_restore.sh`
and `mvn quarkus:dev` reach it from the host, and how any client of yours can. **The stack ships no
SQL browser**: pick your own — psql, pgAdmin, DBeaver, the database panel of your IDE — and point it
at `localhost:5432` with the `.env` credentials. Nothing to declare in compose for that.

Use `-v ON_ERROR_STOP=1` for anything that writes: without it `psql` reports the error and carries on to the next statement, so a failed migration script looks like it succeeded.

### Writing to entity tables by hand

Tables backing a JPA entity carry two columns Hibernate manages for you, and hand-written SQL has to maintain them:

- `updated_at` — set it to `NOW()` on every UPDATE.
- `version` — optimistic locking. **Increment it on every UPDATE.** If you don't, an entity already loaded in memory can silently overwrite your change the next time it is persisted.

```sql
UPDATE users
   SET platform_role = 'PLATFORM_ADMIN',
       updated_at    = NOW(),
       version       = COALESCE(version, 0) + 1
 WHERE email = 'your-email@example.com'
   AND deleted = false;
```

IDs are TSIDs (`bigint`), not sequences. Generate one inline when inserting:

```sql
(EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT
```

## Multi-Tenancy

Pedalons is multi-tenant: each domain (hostname) has isolated teams and users. The domain is resolved from the `Host` or `X-Forwarded-Host` HTTP header.

### Bootstrapping a new deployment

Domains and platform admins are managed from the admin UI (`/admin`), but a brand-new database can't reach it: you need a domain before you can register a user, and a user before anyone can be an admin. Break the cycle with SQL, once, then use the UI for everything after.

**1. Create the first domain** (see [Running SQL](#running-sql) for how to get a `psql` prompt):

```sql
INSERT INTO domains (id, domain, name, base_url, single_team, active, deleted, created_at, updated_at, version)
VALUES (
    (EXTRACT(EPOCH FROM NOW()) * 1000)::BIGINT * 1000000 + (RANDOM() * 999999)::INT,
    'monclub.fr',                    -- domain: hostname used to access the site
    'Mon Club Cycliste',             -- name: displayed in emails, UI, WebAuthn prompts
    'https://monclub.fr',            -- base_url: full URL for email/calendar links
    false,                           -- single_team: if true, domain has only one team
    true,                            -- active
    false,                           -- deleted
    NOW(),
    NOW(),
    0
);
```

| Field | Description |
|-------|-------------|
| `domain` | HTTP hostname (matched against `Host`/`X-Forwarded-Host` header) |
| `name` | App name shown in emails, WebAuthn prompts, and UI |
| `base_url` | Full URL with protocol, used in email links and calendar feeds |

Verify the backend resolves it — a known host returns `200`, an unknown one `404 DOMAIN_NOT_FOUND`:

```bash
curl -s -H 'Host: monclub.fr' http://localhost:8080/api/config
```

**2. Register a user** through the normal signup flow. This sends a verification email, so the mailer must work: in `prod` the backend sends via Brevo, which rejects calls from IPs missing from its [authorised IPs](https://app.brevo.com/security/authorised_ips) allowlist. A rejected call surfaces as a misleading `401 UNKNOWN` on `/api/auth/register`, because `GlobalExceptionMapper` replays the upstream status verbatim.

**3. Grant the platform admin role** via SQL:

```sql
UPDATE users
   SET platform_role = 'PLATFORM_ADMIN',
       updated_at    = NOW(),
       version       = COALESCE(version, 0) + 1
 WHERE email = 'your-email@example.com'
   AND deleted = false;
```

`platform_role` is constrained to `PLATFORM_ADMIN` or `NULL` — it is the only role in the `PlatformRole` enum. No re-login is needed: the role is not carried in the JWT, `AdminInterceptor` reads it from the database on every request.

The role lives on `users`, a table scoped by `domain_id`. You are therefore an admin *of that domain*, despite the "platform admin" name — add a second domain and you'll need a fresh account and a fresh `UPDATE` there.

### Using the admin interface

Once you are a platform admin, the "Admin" link appears in the header menu, providing access to:

- **Dashboard**: Platform statistics
- **Domains**: Manage domains (create, edit, activate/deactivate)
- **Teams**: View all teams, archive/restore
- **Users**: View all users, grant/revoke platform admin role

## Team Governance

Platform admins control the following per-team attributes (configurable via the team admin page):

| Attribute | Description |
|-----------|-------------|
| `visibilityEditable` | If `true`, team admins can change visibility. If `false`, only platform admins can. |
| `joinable` | If `true` and the team is public, any domain user can self-join. |
| `addMemberAllowed` | If `true`, team admins can add members directly. If `false`, only platform admins can. |

When a user creates a team, defaults are:
- `visibility`: `TEAM` (enforced by the backend)
- `visibilityEditable`: `false`
- `joinable`: `false`
- `addMemberAllowed`: `false`

A non-platform-admin user can create at most one team per domain.

## Garmin Connect IQ App Development

The Garmin app (`garmin-app/`) allows users to browse and download routes directly to their Garmin devices. See README inside `garmin-app/` folder
