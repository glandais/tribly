#!/usr/bin/env bash
# Restore a deployed environment from a snapshot produced by scripts/backup.sh.
#
#   scripts/restore.sh --list                     list the snapshots available on the backup host
#   scripts/restore.sh --secrets-only             fetch .env + data/keys only (bootstrap a new host)
#   scripts/restore.sh [--snapshot latest|<STAMP>] [--force] [--keep-fetch]
#
# DESTRUCTIVE: the full restore runs `docker stack rm` + `docker volume rm`, which drops this
# environment's postgres and minio volumes before repopulating them. It asks for confirmation
# unless --force.
#
# Only rsync and docker are used, so this works both from the production host (root, restricted
# backup key) and from any machine that can read the backup store over plain SSH — which is how a
# restore drill is run without touching production.
#
# Not in the backup, and therefore not restored: the docker images (rebuild with ./build.sh at the
# commit recorded in MANIFEST), data/cache (regenerable), and the shared valhalla/tileserver data.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=scripts/_backup_common.sh
. "$SCRIPT_DIR/_backup_common.sh"

SNAPSHOT="latest"
SECRETS_ONLY=false
LIST_ONLY=false
FORCE=false
KEEP_FETCH=false

while (($# > 0)); do
  case "$1" in
    --snapshot) SNAPSHOT="${2:?--snapshot needs a value}"; shift 2 ;;
    --secrets-only) SECRETS_ONLY=true; shift ;;
    --list) LIST_ONLY=true; shift ;;
    --force) FORCE=true; shift ;;
    --keep-fetch) KEEP_FETCH=true; shift ;;
    -h | --help) sed -n '2,18p' "$0"; exit 0 ;;
    *) die "unknown argument: $1 (try --help)" ;;
  esac
done

cd "$REPO_ROOT"

# On a brand-new host there is no .env yet — that is exactly what --secrets-only is for, so it takes
# its coordinates from the environment instead.
if [[ -f "$REPO_ROOT/.env" ]]; then
  load_env
else
  load_backup_env
  $SECRETS_ONLY || $LIST_ONLY \
    || die "no .env — start with: BACKUP_REMOTE=... BACKUP_REMOTE_PATH=... $0 --secrets-only"
fi
require_vars BACKUP_REMOTE BACKUP_REMOTE_PATH

if $LIST_ONLY; then
  complete="$(list_complete_snapshots || true)"
  log "snapshots on $BACKUP_REMOTE:${BACKUP_REMOTE_PATH}"
  while read -r name; do
    [[ -n "$name" ]] || continue
    if printf '%s\n' "$complete" | grep -qx "$name"; then
      printf '    %s\n' "$name"
    else
      printf '    %s  (INCOMPLETE — failed run, not restorable)\n' "$name"
    fi
  done < <(list_snapshots)
  exit 0
fi

if [[ "$SNAPSHOT" == "latest" ]]; then
  SNAPSHOT="$(latest_complete_snapshot || true)"
  [[ -n "$SNAPSHOT" ]] || die "no complete snapshot on $BACKUP_REMOTE:$BACKUP_REMOTE_PATH (try --list)"
fi

# Everything is pulled to a local directory first: the checksums are verified before a single volume
# is touched, so a corrupt or truncated snapshot is found *before* the current data is destroyed.
FETCH="${BACKUP_FETCH_DIR:-/var/tmp/pedalons-restore}/$SNAPSHOT"
mkdir -p "$FETCH"

log "fetching $(remote_url "$SNAPSHOT") into $FETCH"
rsync_remote -a --info=progress2 --exclude="/minio/**" \
  "$(remote_url "$SNAPSHOT")/" "$FETCH/"

[[ -f "$FETCH/MANIFEST" ]] || die "no MANIFEST in the snapshot — is $SNAPSHOT a backup directory?"
[[ -f "$FETCH/COMPLETE" ]] || die "$SNAPSHOT has no COMPLETE marker: it is a failed run, not a backup"

MANIFEST="$(cat "$FETCH/MANIFEST")"
printf '%s\n' "$MANIFEST" | sed 's/^/    /'
manifest_get() { printf '%s\n' "$MANIFEST" | sed -n "s/^$1=//p"; }

SECRETS_FILE="$(manifest_get secrets_file)"
[[ -n "$SECRETS_FILE" ]] || SECRETS_FILE="secrets.tar.gz"

log "verifying checksums"
(cd "$FETCH" && sha256sum -c SHA256SUMS) || die "checksum mismatch — this snapshot is corrupt, try an older one"

# --- secrets ----------------------------------------------------------------

restore_secrets() {
  log "restoring $SECRETS_FILE (.env, data/keys, data/storage)"
  if [[ "$SECRETS_FILE" == *.gpg ]]; then
    [[ -n "${BACKUP_SECRETS_PASSPHRASE_FILE:-}" && -r "$BACKUP_SECRETS_PASSPHRASE_FILE" ]] \
      || die "snapshot secrets are encrypted — set BACKUP_SECRETS_PASSPHRASE_FILE"
    gpg --batch --decrypt --passphrase-file "$BACKUP_SECRETS_PASSPHRASE_FILE" "$FETCH/$SECRETS_FILE" \
      | tar -xzf - -C "$REPO_ROOT"
  else
    tar -xzf "$FETCH/$SECRETS_FILE" -C "$REPO_ROOT"
  fi
  chmod 600 "$REPO_ROOT/.env" "$REPO_ROOT"/data/keys/privateKey.pem 2>/dev/null || true
  log "secrets restored — next: ./build.sh, then $0 --snapshot $SNAPSHOT"
}

if $SECRETS_ONLY; then
  if [[ -f "$REPO_ROOT/.env" ]] && ! $FORCE; then
    die ".env already exists — pass --force to overwrite it"
  fi
  restore_secrets
  exit 0
fi

require_vars ENV_NAME POSTGRES_DB

# A snapshot from another environment is usually a mistake (wrong checkout, wrong .env). It is also
# exactly what a restore drill does on purpose, so --force turns the refusal into a warning.
SNAP_ENV="$(manifest_get env_name)"
if [[ "$SNAP_ENV" != "$ENV_NAME" ]]; then
  $FORCE || die "snapshot is from '$SNAP_ENV', this checkout is '$ENV_NAME' — pass --force for a cross-environment drill"
  log "WARNING: restoring a '$SNAP_ENV' snapshot into '$ENV_NAME'"
fi

# --- full restore -----------------------------------------------------------

if ! $FORCE; then
  cat <<EOF

  This DESTROYS the current state of '$ENV_NAME':
    - postgres volume  (every account, ride, route, post)
    - minio volume     (every photo, GPX and preview)
  and replaces it with snapshot $SNAPSHOT.

EOF
  read -r -p "  Type the environment name to confirm: " answer
  [[ "$answer" == "$ENV_NAME" ]] || die "aborted"
fi

log "fetching minio objects"
mkdir -p "$FETCH/minio"
rsync_remote -a --delete --info=progress2 "$(remote_url "$SNAPSHOT")/minio/" "$FETCH/minio/"

# The stack declares `pedalons-shared` as external: `docker stack deploy` refuses to start until it
# exists.
docker network inspect pedalons-shared >/dev/null 2>&1 \
  || die "network pedalons-shared missing — start the shared stack first (docker stack deploy -c docker-compose.shared.yml pedalons-shared)"

for image in "pedalons-backend:$ENV_NAME" "pedalons-frontend:$ENV_NAME"; do
  docker image inspect "$image" >/dev/null 2>&1 \
    || die "image $image missing — run ./build.sh (MANIFEST commit: $(manifest_get git_commit))"
done

log "removing the stack and dropping its volumes"
docker stack rm "$ENV_NAME"
# `stack rm` returns as soon as removal is requested, not once it's done — volumes stay busy until
# every task actually exits. Poll rather than guess a fixed sleep.
for _ in $(seq 1 60); do
  [[ -z "$(docker stack ps "$ENV_NAME" -q --filter "desired-state=running" 2>/dev/null)" ]] && break
  sleep 2
done
docker volume rm "${ENV_NAME}_postgres_data" "${ENV_NAME}_minio_data" 2>/dev/null || true

log "redeploying the stack on empty volumes, backend and frontend held at 0 replicas"
docker stack deploy -c docker-compose.yml "$ENV_NAME"
docker service scale "${ENV_NAME}_backend=0" "${ENV_NAME}_frontend=0"

log "waiting for postgres to become healthy"
POSTGRES_CID=""
for _ in $(seq 1 60); do
  POSTGRES_CID="$(swarm_container_id postgres)"
  [[ -n "$POSTGRES_CID" ]] \
    && [[ "$(docker inspect -f '{{.State.Health.Status}}' "$POSTGRES_CID" 2>/dev/null)" == "healthy" ]] \
    && break
  sleep 2
done
[[ -n "$POSTGRES_CID" ]] && [[ "$(docker inspect -f '{{.State.Health.Status}}' "$POSTGRES_CID")" == "healthy" ]] \
  || die "postgres never became healthy — check docker service logs ${ENV_NAME}_postgres"

# --- postgres ---------------------------------------------------------------
#
# --clean --if-exists so the script also works against a database that was not wiped; --no-owner
# --no-acl because the dump's role names need not exist here (same flags as biketeam_restore.sh).
# pg_restore is not run with --exit-on-error: a fresh database emits harmless "does not exist"
# notices for the DROPs, so the summary below is what matters.
log "restoring postgres into $POSTGRES_DB"
set +e
docker exec -i "$POSTGRES_CID" \
  sh -c 'pg_restore --clean --if-exists --no-owner --no-acl -U "$POSTGRES_USER" -d "$POSTGRES_DB"' \
  < "$FETCH/postgres.dump"
PG_RC=$?
set -e
((PG_RC == 0)) || log "WARNING: pg_restore exited $PG_RC — read the errors above before continuing"

# --- minio ------------------------------------------------------------------
#
# Restoring under a live MinIO would corrupt its on-disk index, so the service is scaled to 0 first.
# Swarm has no true stop/start, only scale — scaling back up schedules a fresh task rather than
# resuming the same container, which is fine here since the data lives on the volume, not the task.
# `docker cp` rather than a write into /var/lib/docker/volumes: it needs no root, and it hands the
# files to the container as root:root, which is the user MinIO runs as. Writing the volume path
# directly would stamp them with the restoring account's uid.
log "scaling minio to 0 to restore its data"
docker service scale "${ENV_NAME}_minio=0"
for _ in $(seq 1 30); do
  [[ -z "$(swarm_container_id minio)" ]] && break
  sleep 1
done
# A short-lived helper container mounts the same volume — nothing needs to run to receive the copy.
MINIO_VOLUME="${ENV_NAME}_minio_data"
log "copying objects into the $MINIO_VOLUME volume"
docker run --rm -v "$MINIO_VOLUME:/data" -v "$FETCH/minio:/restore:ro" alpine \
  sh -c 'cp -a /restore/. /data/'
docker service scale "${ENV_NAME}_minio=1"
for _ in $(seq 1 30); do
  [[ -n "$(swarm_container_id minio)" ]] && break
  sleep 1
done

log "scaling backend and frontend back up"
docker service scale "${ENV_NAME}_backend=1" "${ENV_NAME}_frontend=1"

# --- verification -----------------------------------------------------------

log "waiting for the backend to answer"
HTTP_PORT="${HTTP_PORT:-8090}"
HOST_HEADER="${PEDALONS_BOOTSTRAP_DOMAIN:-localhost}"
STATUS=000
for _ in $(seq 1 60); do
  STATUS="$(curl -s -o /dev/null -w '%{http_code}' -H "Host: $HOST_HEADER" \
    "http://127.0.0.1:${HTTP_PORT}/api/config" || true)"
  [[ "$STATUS" == "200" ]] && break
  sleep 5
done

echo
log "restore summary"
docker exec "$POSTGRES_CID" sh -c \
  'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -tAc "SELECT (SELECT count(*) FROM domains), (SELECT count(*) FROM users), (SELECT count(*) FROM teams), (SELECT count(*) FROM assets)"' \
  | awk -F'|' '{printf "    domains=%s users=%s teams=%s assets=%s\n", $1, $2, $3, $4}'
# Counted on the fetched copy, not inside the container: the minio image ships no `find`, and the
# failure is silent — `wc -l` of an empty error stream reports a confident 0 objects.
printf '    minio objects=%s\n' "$(find "$FETCH/minio" -type f -not -path '*/.minio.sys/*' | wc -l)"
printf '    GET /api/config (Host: %s) -> %s\n' "$HOST_HEADER" "$STATUS"
echo

$KEEP_FETCH || rm -rf "$FETCH"

[[ "$STATUS" == "200" ]] \
  || die "the backend does not answer 200 on /api/config — check docker service logs ${ENV_NAME}_backend"

log "restore complete. Open the site and check that an existing photo still renders: that path goes"
log "through minio -> imgproxy -> varnish, so it is what proves the objects came back, not just the rows."
