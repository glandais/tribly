# Operations

How Pedalons is deployed, backed up and restored on a host. Workstation setup stays in the
[README](../README.md); this is the server side.

## Deployment

A host is a single-node **Docker Swarm**, and runs **one shared stack** plus **one stack per
environment**, each from its own checkout (`~/shared`, `~/prod`, `~/staging`) with its own `.env`.
Caddy terminates TLS on the host and reverse-proxies each hostname to that environment's traefik
(`HTTP_PORT`: 8090 for prod, 8089 for staging). Swarm rather than plain compose for what it
reconciles: a `deploy` converges the running services onto the file, and a task that dies is
rescheduled rather than left for a `restart:` policy.

`docker-compose.shared.yml` holds the services that carry no application data and are read-only:
`valhalla` (17 GB of OSM routing tiles) and `tileserver` (server-side raster rendering, ~1.8 GB
resident). One instance serves every environment. It owns the `pedalons-shared` overlay network.

`docker-compose.yml` holds everything that must stay isolated per environment: traefik, backend,
frontend, postgres, minio, imgproxy and varnish. Its `backend` also joins `pedalons-shared` to reach
the two shared services — under the same hostnames as before, since `valhalla` and `tileserver` are
their service names.

Each environment's `.env` differs from a workstation's in five keys only — see
[The `.env`, on a workstation and on a server](../README.md#the-env-on-a-workstation-and-on-a-server).
The same `docker-compose.yml` also runs on a workstation, under plain compose, to test a build:
[Running the full stack locally](../README.md#running-the-full-stack-locally). It therefore stays
valid for both tools — its header lists what each one ignores.

**Always deploy through `scripts/deploy.sh`**, never `docker stack deploy` by hand:

- `docker stack deploy` reads **no `.env`**. Every `${VAR}` of the file would expand to nothing —
  including, for the shared stack, a `VALHALLA_TILE_URLS` whose silent fallback to the default costs
  a rebuild of several hours;
- it picks the **build of the checked-out commit**, `pedalons-backend:${ENV_NAME}-<sha12>` (see
  [Rolling updates](#rolling-updates-and-rollback)), and refuses a checkout with uncommitted changes;
- the stack must be named after `ENV_NAME`: the backup scripts find its containers and volumes
  (`${ENV_NAME}_postgres_data`) by that name.

```bash
# once per host — the firewall unit (see "Only Caddy may reach traefik") before the swarm
docker swarm init
cd ~/shared && scripts/deploy.sh --shared

# then each environment, on every deploy
cd ~/prod && ./build.sh && scripts/deploy.sh
docker stack ps "$ENV_NAME"        # what is running, and why a task was rejected

# teardown — keeps the volumes
docker stack rm "$ENV_NAME"
```

On a stack's **first** deploy, `docker stack ps` usually shows one backend task `Failed` with
`task: non-zero exit (1)`: Swarm ignores `depends_on`, the backend started before postgres accepted
connections, and its replacement is the one running. Nothing to do — the next deploys roll the
backend beside a postgres already up.

### Rolling updates and rollback

`./build.sh` tags each image twice: `pedalons-backend:${ENV_NAME}-<sha12>`, never moved once
written, and `pedalons-backend:${ENV_NAME}`, the latest build (what a workstation runs). From a
checkout with uncommitted changes it writes the second only. No registry: on a single-node swarm the
images built on the host are all Swarm needs, and it merely warns that it cannot record a digest.

A deploy of a new commit changes the image in the backend and frontend specs, and Swarm rolls them
**start-first**: the new task boots beside the old one, which keeps serving until the new one's
healthcheck passes (`/q/health/ready`, i.e. after Flyway). traefik health-checks both too, and the
old task reports itself not ready for 10 s after SIGTERM (`quarkus.shutdown.delay`; `/health` in
the frontend's `server.js`) before it drains, so no request lands on a stopping task. A new task
that never turns healthy is rolled back on its own (`failure_action: rollback`), and
`scripts/deploy.sh` waits for that outcome: it returns once backend and frontend both run the new
build, healthy, and exits non-zero on a rollback or after `DEPLOY_TIMEOUT` seconds (600 by default)
— a deploy that printed no error is one that went through. postgres, minio,
imgproxy and varnish keep the default stop-first: each owns a volume, one instance at a time.

```bash
docker service ps "${ENV_NAME}_backend"           # the rollout, and why a task failed
docker service rollback "${ENV_NAME}_backend"     # back to the previous spec, i.e. the previous build
scripts/deploy.sh --rev HEAD~1                    # or any earlier commit still built
```

`deploy.sh` keeps the last five builds per image (and whatever a service runs or would roll back
to), and drops the older ones; `--rev` of a dropped commit means `git checkout` + `./build.sh`.

**For about a minute, two backends run against the same database.** What that asks of the code:

- **Flyway migrations stay backward compatible** — the previous release must run on the new
  schema. Renaming or dropping a column takes two deploys: add and write both, then drop.
- **Scheduled jobs may run on both.** Those that claim their work in the database
  (`for update skip locked`: notifications, webhooks, user exports) are safe; any other job must be
  idempotent, since `concurrentExecution = SKIP` only guards within one JVM.
- **`data/cache` is shared** by the two for that while — the gpx2web race described below.

A `.env` value changed on the host takes effect on the next `scripts/deploy.sh`: `env_file` is read
at deploy time and a new value rolls the backend.

### Only Caddy may reach traefik

**Swarm cannot publish a port on the loopback.** Given `127.0.0.1:8090:80`, it drops the address
with a mere warning and listens on every interface — which is why `docker-compose.yml` no longer
pretends to. And `ufw` cannot close it either: Docker inserts its rules ahead of ufw's. The rule has
to go into the `DOCKER-USER` chain, which Docker consults first and never rewrites — one per
environment port, on the public interface.

`docker swarm init` also opens the swarm's own ports on every interface: 2377/tcp (cluster
management), 7946/tcp+udp (gossip) and 4789/udp — the VXLAN that carries the overlay networks, which
is **unauthenticated**: anyone who reaches it can inject packets into `pedalons-shared` or an
environment's network. A single-node swarm needs none of them from outside. They go through the
host's `INPUT` chain, not `DOCKER-USER`, and a host with no firewall (`-P INPUT ACCEPT`) leaves them
open.

Both sets of rules come from `scripts/pedalons-firewall.sh`, run by a systemd unit **before**
`docker.service`: Docker keeps an existing `DOCKER-USER` chain as it is, so no stack is ever up
without the rules — not even at boot. **Install it before `docker swarm init`**, IPv4 and IPv6
alike. Not `iptables-persistent`: it would save fail2ban's chains and Docker's too, which both
rebuild their own at start.

```bash
install -m 755 scripts/pedalons-firewall.sh /usr/local/sbin/pedalons-firewall.sh
install -m 644 scripts/pedalons-firewall.service /etc/systemd/system/pedalons-firewall.service
# The public interface: `eth0` on some hosts, `ens2`, `enp5s0` on others. A rule naming an interface
# the host does not have is accepted without a word, and drops nothing. TRAEFIK_PORTS (default
# "8089 8090") lists the HTTP_PORT of every environment.
echo "PUBLIC_IF=$(ip -o route get 1.1.1.1 | sed -n 's/.* dev \([^ ]*\).*/\1/p')" > /etc/default/pedalons-firewall
systemctl daemon-reload && systemctl enable --now pedalons-firewall.service
iptables -S DOCKER-USER                                   # one DROP per environment port
iptables -S FORWARD | head -3                             # -A FORWARD -j DOCKER-USER, first
iptables -S INPUT | grep -E '2377|7946|4789'              # four DROP; ip6tables alike
```

`--ctorigdstport` matches the port as the client asked for it, before the routing mesh rewrites the
destination; Caddy, which comes in over the loopback, is untouched.

**Then check from another machine, once the first stack is up — it is part of the procedure, not an
option**: `curl -m 5 http://<host>:8090` must time out, while the site answers through Caddy, and
`nc -zv -w 3 <host> 2377` must fail. Nothing
on the host itself can tell: the loopback is not filtered, so a local `curl` succeeds either way.

Postgres, for the same reason, publishes no port at all on a host — a raw Postgres guarded by a
password alone has no business on a public interface. Reach it through `docker exec` (see
[Running SQL](../README.md#running-sql)); a workstation still gets `127.0.0.1:5432` from the overlay.

### Moving a host from compose to Swarm

Done once per host. The data needs moving, not just the services: compose named the volumes after the
project, i.e. the checkout directory (`prod_postgres_data`), a stack names them after itself
(`pedalons-prod_postgres_data`). A stack deployed without the move would boot on two new empty
volumes — Flyway on an empty schema and a freshly bootstrapped Domain — with the real data left
where nothing reads it. Take a backup first (`scripts/backup.sh`), then, after `git pull` in every
checkout:

```bash
# 1. each environment: stop its compose containers (volumes kept) and copy its data across
cd ~/prod && scripts/migrate-to-swarm.sh
cd ~/staging && scripts/migrate-to-swarm.sh

# 2. the firewall unit of "Only Caddy may reach traefik" above, then the swarm: from the next step
#    on, a published port listens on every interface
#    ... the install commands above, then `iptables -S DOCKER-USER`
docker swarm init --advertise-addr <public IPv4>   # the flag only if the host has several addresses

# 3. the shared stack. Its old project name came from the `name:` the file no longer carries, and
#    its bridge network must be gone before Swarm creates the overlay of the same name
cd ~/shared && docker compose -p pedalons-shared -f docker-compose.shared.yml down
scripts/deploy.sh --shared

# 4. each environment, as a stack
cd ~/prod && scripts/deploy.sh
cd ~/staging && scripts/deploy.sh
```

5. From **another machine**: `curl -m 5 http://<host>:8090` (and `:8089`) must time out, and
   `nc -zv -w 3 <host> 2377` fail. If one answers, the rules are missing or name the wrong interface
   — fix that before anything else.

The old volumes stay behind as the way back; drop them (`docker volume rm prod_postgres_data …`)
once the site has been checked, photos included.

#### One environment at a time

The environments may move separately — staging first, prod once staging has proved itself. Only the
environment that moves runs step 1 and step 4; steps 2 and 3 are the host's and happen once. The
catch is step 3: the environment still under compose has its backend on the `pedalons-shared`
bridge, which must be gone before the overlay replaces it, and must then join the overlay — which a
compose container can only do because the shared stack declares it `attachable`. So step 3 becomes,
with prod still under compose:

```bash
docker network disconnect pedalons-shared pedalons-prod-backend
cd ~/shared && docker compose -p pedalons-shared -f docker-compose.shared.yml down
docker network inspect pedalons-shared >/dev/null 2>&1 && echo "still there"   # must print nothing
scripts/deploy.sh --shared
docker network connect pedalons-shared pedalons-prod-backend
docker exec pedalons-prod-backend getent hosts valhalla tileserver             # two addresses
```

Valhalla and the tile server are down for prod meanwhile, the time for valhalla to load its tiles. A
later `docker compose up` of prod rejoins the overlay by itself: its file declares the network
`external`.

### Services that stay per-environment

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

**On a host**, `docker stack deploy` never alters a network that already exists: change its driver,
its options or its labels and the deploy succeeds, reporting nothing, on the old network. For any
such change, remove the stack and wait for its network to be gone before deploying again:

```bash
docker stack rm "$ENV_NAME"    # never followed by `docker volume rm` — that is the data
while docker network inspect "${ENV_NAME}-net" >/dev/null 2>&1; do sleep 2; done
scripts/deploy.sh
```

**On a workstation**, renaming or re-declaring a network makes compose (v5.3.1) drop the old one and
create the new one *during* the run, then fail on `network X was found but has incorrect label
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
other backups on the host. Within that tree it can still delete or overwrite its own snapshots:
`rrsync` runs without `-no-del` and honours `rsync --delete` (ledger `SEC-16`). What it cannot do is
read them back in clear: the dump and the secrets are encrypted to a key it does not hold
([Encryption](#encryption)). Three consequences run through the scripts, and none of them are
incidental:

- **the dumps are staged locally** (`/var/backups/pedalons/<env>`) before being pushed — there is no
  remote `cat >`; they are encrypted as they are written, so nothing lands there in clear; the staging
  directory is removed once the run succeeds; a failed run leaves it (mode 700) until the next run
  wipes it on start;
- **the previous snapshot is named explicitly** in `--link-dest`, because `rrsync` rejects any path
  containing `..`;
- **retention lives on the backup host** (`backup-prune.sh`, root's crontab there), not in the
  backup script.

### What a snapshot holds

`<root>/<UTC timestamp>/`:

| File | Contents | Why it matters |
|------|----------|----------------|
| `postgres.dump.age` | `pg_dump -Fc` of `$POSTGRES_DB`, encrypted with age | Accounts, teams, rides, routes, posts |
| `minio/` | The object store, verbatim (not encrypted: see [Encryption](#encryption)) | Photos, GPX files, avatars, previews |
| `secrets.tar.gz.age` | `.env`, `data/keys` (JWT keys, FCM service account), `data/storage`, encrypted with age | The JWT keys sign every session and passkey; the service account sends every push; `ENCRYPTION_KEY` decrypts the stored Karoo/Garmin/Wahoo tokens and the per-domain GPS client secrets |
| `MANIFEST` | Timestamp, env, git commit, image tags, file names | Says which commit to rebuild before restoring |
| `SHA256SUMS` | Checksums of the two encrypted archives | Verified by `restore.sh` before it decrypts or destroys anything |
| `COMPLETE` | Written last, after everything else landed | A dated directory without it is a failed run, not a backup — the scripts never delete on the backup host, so it has to be recognisable |

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

### Encryption

The backup host sits at the controller's home, so every snapshot is encrypted at rest, in two
layers:

- **the dump and the secrets are encrypted on production**, with [age](https://age-encryption.org)
  to a public key (`BACKUP_AGE_RECIPIENT`). `pg_dump` and `tar` are piped straight into `age`: neither
  touches the staging disk in clear. The private key is kept **offline**, at
  `<offline-key-location>`: production can write a backup but cannot read one back, and neither can
  the backup host;
- **the whole backup root sits on a LUKS volume** on the backup host
  ([below](#luks-volume-on-the-backup-host)). That is what protects `minio/`: encrypting it on
  production would turn the object store into one opaque archive a night and lose the
  `--link-dest` incrementals.

`backup.sh` refuses to run without `age` or without a valid `BACKUP_AGE_RECIPIENT`: a run that cannot
encrypt fails (and Healthchecks says so) rather than pushing clear text. `restore.sh` restores only
this format — snapshots from before it (clear, or `secrets.tar.gz.gpg`) are not supported.

The key pair is generated **once, on a machine that is not production** (ideally offline):

```bash
age-keygen -o pedalons-backup.key      # prints "Public key: age1..."
```

- the private key (the whole `pedalons-backup.key`, `AGE-SECRET-KEY-1...`) goes to
  `<offline-key-location>`, then the file is deleted; lose it and no snapshot's database or secrets
  can be read again;
- the public key goes into `/root/pedalons-backup.env` on production as `BACKUP_AGE_RECIPIENT`
  (see [Configuration](#configuration)).

On production, `apt install age` (the Debian/Ubuntu package ships `age` and `age-keygen`). Check the
recipient before the first night — this must print nothing and exit 0:

```bash
age -r "$(sed -n 's/^BACKUP_AGE_RECIPIENT=//p' /root/pedalons-backup.env)" </dev/null >/dev/null
```

A new key pair takes effect from the next run; older snapshots stay readable only with the old
private key, so keep it until they have all been pruned (30 nights).

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
BACKUP_AGE_RECIPIENT=age1...             # public key, required (see Encryption)
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

### LUKS volume on the backup host

`<backup-root>` is the mount point of a LUKS container: a file on the backup host's disk, never
unlocked at boot. As root on the backup host, once — `<backup-root>` must be empty and unmounted:

```bash
touch <backup-root>.luks && chattr +C <backup-root>.luks    # btrfs: no copy-on-write, before any data
fallocate -l 40G <backup-root>.luks
cryptsetup luksFormat --type luks2 <backup-root>.luks        # passphrase -> <offline-key-location>
cryptsetup open --allow-discards <backup-root>.luks pedalons-backup
mkfs.ext4 /dev/mapper/pedalons-backup
chattr +i <backup-root>                                      # the bare mount point: unwritable
mount /dev/mapper/pedalons-backup <backup-root>
chown <backup-user>:<backup-user> <backup-root> && chmod 750 <backup-root>
cryptsetup luksHeaderBackup <backup-root>.luks --header-backup-file /root/pedalons-backup.luks-header
```

- **`chattr +i` on the bare mount point** is what keeps a locked volume from filling up in clear:
  while nothing is mounted there, the night's `rsync` fails instead of writing to the host disk;
- **`--allow-discards`** lets TRIM from ext4 reach the SSD, so deleted snapshots do not linger on
  it;
- **the header backup** goes to `<offline-key-location>` and is then deleted from the host: a damaged
  LUKS header makes the whole volume unreadable, passphrase or not.

There is deliberately **no `crypttab` or `fstab` entry**: the passphrase is never stored on the
host. After every reboot, unlock it by hand over SSH:

```bash
cryptsetup open --allow-discards <backup-root>.luks pedalons-backup && mount /dev/mapper/pedalons-backup <backup-root>
```

Until then each night's backup fails, and Healthchecks says so; `backup-prune.sh` finds an empty
directory and removes nothing.

### Restoring

Everything but `--list` needs the private key. Bring it from `<offline-key-location>` into a file on
a RAM-backed filesystem, for the duration of the restore only, and point `BACKUP_AGE_IDENTITY` at
it:

```bash
install -m 600 /dev/null /dev/shm/pedalons-backup.key
$EDITOR /dev/shm/pedalons-backup.key         # paste the AGE-SECRET-KEY-1... line
export BACKUP_AGE_IDENTITY=/dev/shm/pedalons-backup.key

scripts/restore.sh --list                    # complete snapshots, and failed runs marked as such
scripts/restore.sh                           # restore the newest complete one (asks to confirm)
scripts/restore.sh --snapshot 2026-07-24T031500Z

rm /dev/shm/pedalons-backup.key              # as soon as the restore is done
```

The restore pulls the snapshot to a local directory, verifies its checksums, then decrypts the
secrets and the whole dump once to prove the key matches — all **before** touching anything. Only
then does it take the stack down, drop the current postgres and minio volumes, and repopulate them;
the dump is decrypted in flight into `pg_restore` and never written in clear. It refuses a snapshot
with no `COMPLETE` marker, and refuses one from another `ENV_NAME` unless `--force`.

It follows the stack wherever it runs. On a host (a swarm) it removes the stack and redeploys it
through `scripts/deploy.sh --hold-app` — Swarm cannot start part of a stack, so backend and frontend
come back at zero replicas, or the backend would bootstrap a fresh Domain on the empty database
before the dump lands. On a workstation it is `docker compose down -v`, then `up`.

It needs only `rsync`, `age` and `docker`, no root. The objects go back as `root:root` — the user
MinIO runs as — through `docker cp` on a workstation, and on a host, where a service scaled to zero
leaves no container to copy into, through a throwaway container mounting the volume. Writing into
`/var/lib/docker/volumes` directly would stamp them with the restoring account's uid.

On a **new host**, the order matters — you need `.env` before anything else can read its own config:

```bash
git clone <repo> ~/prod && cd ~/prod

# 0. age, and the private key on a RAM-backed file (see above)
apt install age
export BACKUP_AGE_IDENTITY=/dev/shm/pedalons-backup.key

# 1. secrets first: no .env yet, so pass the coordinates in the environment
BACKUP_REMOTE=<backup-user>@<backup-tunnel-ip> BACKUP_REMOTE_PATH=/ \
BACKUP_SSH_KEY=/root/.ssh/id_pedalons_backup \
  scripts/restore.sh --secrets-only

# 2. the firewall unit (see "Only Caddy may reach traefik"), the swarm, the shared stack, then
#    the images
docker swarm init
cd ~/shared && scripts/deploy.sh --shared
cd ~/prod && ./build.sh            # at the commit recorded in MANIFEST

# 3. the data, then drop the key
scripts/restore.sh
rm /dev/shm/pedalons-backup.key
```

`/root/pedalons-backup.env` is not in the snapshot: recreate it (with `BACKUP_AGE_RECIPIENT`) before
the next night's backup.

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
(`./build.sh`) and `scripts/deploy.sh`; nothing needs to be restored again.

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
BACKUP_ENV_FILE=/tmp/drill.env BACKUP_AGE_IDENTITY=/dev/shm/pedalons-backup.key scripts/restore.sh --force
```

The backup host's LUKS volume must be open, and the private key is needed here too — delete it
from `/dev/shm` afterwards.

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
