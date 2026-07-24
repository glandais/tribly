# Pedalons - Cycling Team Management Platform

Multi-tenant web platform for cycling teams to organize rides, trips, manage GPX routes with interactive maps, and communicate.

## Tech Stack

- **Backend**: Java 21, Quarkus 3.31.x, PostgreSQL 17 with PostGIS
- **Frontend**: TypeScript 5.9, React 19, Vite 7, Mantine UI 8
- **Mobile**: Flutter, Dart, Riverpod 3
- **Karoo**: Kotlin, Jetpack Compose, ktor-client-karoo
- **Garmin**: Monkey C, Connect IQ SDK
- **API**: OpenAPI 3.1 contract-driven development
- **Testing**: JUnit 5, REST Assured, Vitest

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 20+
- pnpm 10+
- Docker 24+
- Docker Compose 2.20+

### Clone and Setup

```bash
git clone https://github.com/glandais/pedalons.git
cd pedalons

# Copy environment template
cp .env.example .env
```

### Environment Variables

Required environment variables for production:

| Variable | Description |
|----------|-------------|
| `ENCRYPTION_KEY` | Base64-encoded 32-byte key for token encryption (generate with `openssl rand -base64 32`) |


### Install and configure mkcert

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


### 2. Start Infrastructure

```bash
# Start dev services (PostgreSQL, MinIO, imgproxy, valhalla, tileserver, mailhog)
cd backend
docker compose up -d

# Wait for PostgreSQL to be ready
docker compose exec postgres pg_isready -U pedalons
```

### 3. Start Backend

```bash
cd backend
./mvnw quarkus:dev
```

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

Nothing works until at least one domain exists — every request resolves its tenant from the `Host` header. Open a `psql` prompt on the dev database:

```bash
docker exec -it pedalons-dev-postgres psql -U pedalons -d pedalons
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

### Start Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Frontend available at https://localhost:5173

## Project Structure

```
pedalons/
├── backend/          # Quarkus backend (Java 21)
├── frontend/         # React 19 SPA (Mantine UI)
├── mobile/           # Flutter mobile app (iOS/Android)
├── karoo/            # Hammerhead Karoo extension (Kotlin/Compose)
├── garmin-app/       # Garmin Connect IQ app (Monkey C)
├── contracts/        # OpenAPI specifications
├── services/         # Docker service configs (valhalla, Varnish)
├── scripts/          # Utility scripts
├── data/             # Runtime data (segments, tileserver, keys)
├── docker-compose.yml         # One deployed environment (prod, staging, ...)
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
- **the gpx2web cache** (`DATA_CACHE_PATH`) — gpx2web downloads tiles straight into their final path
  with no write-then-rename, guarded only by an in-JVM lock. Two backends sharing the directory can
  read a truncated file and cache it permanently. Keep it at `/mnt/cache`: pointed at `/tmp` it lives
  inside the container and is re-downloaded in full on every restart.

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

and on the backup host, after it:

```
30 4 * * * /root/pedalons-backup-prune.sh /home/backup-pedalons 30 >> /var/log/backup/pedalons-backup-prune.log 2>&1
```

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
# Backend linting
cd backend && ./mvnw checkstyle:check

# Frontend linting
cd frontend && pnpm lint
```

## Running SQL

Two different PostgreSQL containers exist depending on how you run Pedalons. Check which one you have with `docker ps` before running anything.

| Setup | Compose file | Container | Credentials |
|-------|--------------|-----------|-------------|
| Local dev | `backend/docker-compose.yml` | `pedalons-dev-postgres` | Hardcoded (`pedalons` / `pedalons`) |
| Deployed stack | `docker-compose.yml` (root) | `pedalons-postgres` | From `.env` (not versioned) |

**Local dev** — the port is published on `127.0.0.1:5432`, so any client works:

```bash
docker exec -it pedalons-dev-postgres psql -U pedalons -d pedalons
```

**Deployed stack** — no port is published, so go through the container. Read the credentials from the container's own environment rather than typing them, which keeps secrets out of your shell history:

```bash
# Interactive session
docker exec -it pedalons-postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'

# One-off statement
docker exec pedalons-postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v ON_ERROR_STOP=1 -c "SELECT domain, name, active FROM domains;"'
```

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
