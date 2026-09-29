# Operations

How Pedalons is deployed, backed up and restored on a host. Workstation setup stays in the
[README](../README.md); this is the server side.

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

Each environment's `.env` differs from a workstation's in five keys only — see
[The `.env`, on a workstation and on a server](../README.md#the-env-on-a-workstation-and-on-a-server).
The same `docker-compose.yml` also runs on a workstation to test a build:
[Running the full stack locally](../README.md#running-the-full-stack-locally).

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
- **the gpx2web cache** (`DATA_CACHE_PATH`) — since gpx2web 1.5.1 map tiles are written then renamed,
  and only on a 2xx, but the elevation tiles next to them were not reviewed and the downloads are
  guarded only by an in-JVM lock: don't share the directory between backends (tracked as
  ledger `OPS-10`). Keep it at
  `/mnt/cache`: pointed at `/tmp` it lives inside the container and is re-downloaded in full on every
  restart.

### Access logs

The host's Caddy writes the **only** access log: Traefik's is off (`--accesslog=false` in
`docker-compose.yml`), since it cannot filter a query parameter and its file, with no volume, died
with the container anyway. The privacy policy (§1, §6) makes two promises about that log, and both
live in the host's Caddy configuration, outside this repository:

- **Nothing sensitive from the query string.** Three kinds of parameters are written away before
  the line is:
  - credentials, because their client fetches them outside the authenticated HTTP stack and
    cannot set a header — `?t=` on `/api/…/tiles/{z}/{x}/{y}.mvt` (the tile token, ~15 min, see
    `TileTokenService`; MapLibre fetches tiles itself, so dozens of lines per map session),
    `?token=` on the ICS calendar feed (it does **not** expire, `SEC-17`), and the OAuth
    `?code=`/`?state=` of the GPS-service callbacks;
  - the device's position: `?lat=`/`?lon=` from the Garmin app (`DeviceRoutesResource`),
    `?nearLat=`/`?nearLon=` from "around me" and the ad proximity probe.
- **14 days**, then deleted.

```caddyfile
# {args[0]}: the log file's name, so each stack gets its own file under the logrotate glob.
(pedalons_access_log) {
	log {
		output file /var/log/caddy/{args[0]}.log {
			roll_disabled          # logrotate owns the rotation, below
		}
		format filter {
			request>uri query {
				replace t REDACTED
				replace token REDACTED
				replace code REDACTED
				replace state REDACTED
				delete lat
				delete lon
				delete nearLat
				delete nearLon
			}
			# A redirect's Location repeats the raw query string, filter or not.
			resp_headers>Location delete
		}
	}
}

# The bare domain only redirects, but {uri} carries the query string: it logs through the snippet too.
pedalons.fr {
	import pedalons_access_log pedalons-access
	redir https://www.pedalons.fr{uri}
}

www.pedalons.fr {
	import pedalons_access_log pedalons-access
	reverse_proxy 127.0.0.1:8090
}

staging.pedalons.fr, staging.np.pedalons.fr {
	import pedalons_access_log pedalons-staging-access
	reverse_proxy 127.0.0.1:8089
}
```

Three traps, each met on the first install (29 September 2026):

- **`www.pedalons.fr` is the application**, `pedalons.fr` a redirect. Both must import the snippet:
  the redirect sees the same query string.
- **`resp_headers>Location`**: without its `delete`, the redirect's log line carries
  `?lat=…&t=…` in clear in the `Location` header, next to a correctly filtered `uri`.
- **`caddy validate` run as root creates the log files as `root:root 0600`**, which Caddy (user
  `caddy`) then cannot open on reload. After any `validate`, `chown caddy:caddy` the **files**, not
  just `/var/log/caddy`.

Credentials are replaced rather than deleted, so a line still shows that one was there; the
position leaves no trace. Caddy already leaves `Authorization` and `Cookie` headers out of its log.
Rotation, from the repository like the backup logs:

```bash
mkdir -p /var/log/caddy && chown caddy:caddy /var/log/caddy
install -m 644 scripts/caddy-access.logrotate /etc/logrotate.d/caddy-access
logrotate -d /etc/logrotate.d/caddy-access     # dry run: must list every *-access.log
```

Check it bites after `systemctl reload caddy`, on the application **and** on the redirect:

```bash
for h in www.pedalons.fr pedalons.fr staging.pedalons.fr; do
  curl -s -o /dev/null "https://$h/api/version?lat=1&t=x"
done
tail -n 3 /var/log/caddy/pedalons-access.log /var/log/caddy/pedalons-staging-access.log
```

Every line must show `t=REDACTED`, no `lat`, and no `Location` header. Keep
`/etc/caddy/Caddyfile.bak-<date>` until then; rolling back is restoring it and reloading. Installed
on the production host on 29 September 2026 (ledger `OPS-6`). The short TTL of the tile token is
what makes any line written before that inert, and the reason that TTL must never be raised to
hours.

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

Production is backed up nightly to the backup host over a WireGuard tunnel (`<prod-tunnel-ip>` → `<backup-tunnel-ip>`).

### rsync is the only channel

The receiving account's key is restricted to `command="rrsync <root>",restrict,from="<prod-tunnel-ip>"`:
it accepts an `rsync --server` invocation and refuses everything else, confined to one directory.
A compromise of the production host therefore stops at its own backup tree — it cannot read the
other backups on the host, and it cannot delete its own history. Three consequences run through the
scripts, and none of them are incidental:

- **the dumps are staged locally** (`/var/backups/pedalons/<env>`) before being pushed — there is no
  remote `cat >`; the staging directory is removed once the run succeeds; a failed run leaves it (dump and
  secrets included, mode 700) until the next run wipes it on start;
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
| `secrets.tar.gz` | `.env`, `data/keys` (JWT keys, FCM service account), `data/storage` | The JWT keys sign every session and passkey; the service account sends every push; `ENCRYPTION_KEY` decrypts the stored Karoo/Garmin/Wahoo tokens and the per-domain GPS client secrets |
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
BACKUP_REMOTE=<backup-user>@<backup-tunnel-ip>
BACKUP_REMOTE_PATH=/                     # relative to the rrsync root
BACKUP_SSH_KEY=/root/.ssh/id_pedalons_backup
BACKUP_PING_URL=https://hc-ping.com/<uuid>
```

Retention is not set here: it is the second argument of `backup-prune.sh` on the backup host (30 in
the crontab below).

Reading the minio volume needs root, so the backup runs from root's crontab:

```
15 3 * * * cd /home/pedalons/prod && ./scripts/backup.sh >> /var/log/backup/pedalons-backup.log 2>&1
```

and on the backup host, after it — the checkout is refreshed in the same line, so the repository
stays the single source of truth instead of a copy that quietly drifts:

```
30 4 * * * cd /opt/pedalons-scripts && { git fetch -q --depth 1 origin develop && git reset -q --hard FETCH_HEAD ; } ; ./scripts/backup-prune.sh <backup-root> 30 >> /var/log/backup/pedalons-backup-prune.log 2>&1
```

`;` rather than `&&` between the two: a failed fetch must not skip the night's pruning, it just runs
the last version that landed. The checkout is read-only, shallow and sparse (5 MB, `scripts/` only):

```bash
git clone --depth 1 --single-branch --branch develop --no-checkout \
  https://github.com/glandais/tribly.git /opt/pedalons-scripts
cd /opt/pedalons-scripts && git sparse-checkout set --no-cone scripts && git checkout develop
```

The obvious shortcut — having `backup.sh` push the prune script along with the snapshot, the way
another backup job on that host copies itself into its own backup — is a trap **here**: root's crontab on the
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

On the backup host, as root — modelled on the host's other backup accounts (placeholders in `<…>`):

```bash
adduser --system --group --home /home/<backup-user> --shell /bin/bash <backup-user>
mkdir -p /home/<backup-user>/.ssh <backup-root>
echo 'from="<prod-tunnel-ip>",restrict,command="rrsync <backup-root>" ssh-ed25519 AAAA... backup-pedalons-prod' \
  > /home/<backup-user>/.ssh/authorized_keys
chown -R <backup-user>:<backup-user> /home/<backup-user> <backup-root>
chmod 700 /home/<backup-user>/.ssh && chmod 600 /home/<backup-user>/.ssh/authorized_keys
chmod 750 <backup-root>
```

Check the restriction actually bites — this must fail:

```bash
ssh -i /root/.ssh/id_pedalons_backup <backup-user>@<backup-tunnel-ip> 'ls /'
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
BACKUP_REMOTE=<backup-user>@<backup-tunnel-ip> BACKUP_REMOTE_PATH=/ \
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
BACKUP_REMOTE=root@<backup-host-lan-ip>
BACKUP_REMOTE_PATH=<backup-root>
EOF
BACKUP_ENV_FILE=/tmp/drill.env scripts/restore.sh --force
```

### Cold backup of the shared stack

`~/shared/data/valhalla` is ~17 GB and takes hours to rebuild from the `.osm.pbf`. It carries no
application data, so it stays out of the nightly backup — but copying it **once** (and again
whenever the OSM extract changes) turns a multi-hour restore into an `rsync` (tracked as
ledger `OPS-9`):

```bash
rsync -a ~/shared/data/valhalla/{france-latest.osm.pbf,valhalla_tiles.tar,file_hashes.txt} \
      ~/shared/data/valhalla/elevation_data \
      backup-host:/path/to/pedalons-shared/
```

See [Seeding the shared Valhalla data](#seeding-the-shared-valhalla-data) for the permission trap
when moving those directories back.
