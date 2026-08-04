#!/usr/bin/env bash
# Nightly backup of one deployed environment, pushed to the backup host over the WireGuard tunnel.
#
#   scripts/backup.sh
#
# Produces $BACKUP_REMOTE_PATH/<UTC timestamp>/ on the backup host:
#   postgres.dump      pg_dump -Fc of $POSTGRES_DB
#   minio/             object storage, hard-linked against the previous snapshot
#   secrets.tar.gz     .env, data/keys/*.pem, data/storage
#   MANIFEST           what this snapshot is, and what to rebuild to restore it
#   SHA256SUMS         checksums of the two archives
#   COMPLETE           written last: a snapshot without it is a failed run, not a backup
#
# rsync is the ONLY channel. The receiving key is restricted to `command="rrsync <root>"`, so this
# script can neither run a command on the backup host nor touch anything outside its own root — a
# compromise of this production host stops at its own backup tree. Two consequences shape the code:
# the dumps are staged locally before being pushed (no remote `cat >`), and retention is not done
# here but by a prune job on the backup host itself.
#
# Reading the minio volume needs root, so this runs from root's crontab.
#
# Configuration lives in $BACKUP_ENV_FILE (default /root/pedalons-backup.env), NOT in .env: compose
# hands the whole .env to the backend container.
#
# Restore with scripts/restore.sh.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=scripts/_backup_common.sh
. "$SCRIPT_DIR/_backup_common.sh"

cd "$REPO_ROOT"
load_env
require_vars ENV_NAME POSTGRES_DB BACKUP_REMOTE BACKUP_REMOTE_PATH

# Serialize runs: a backup still going when the next one fires would push into a half-written
# snapshot and hard-link against it.
LOCK_FILE="${BACKUP_LOCK_FILE:-/var/lock/pedalons-backup-${ENV_NAME}.lock}"
exec 9>"$LOCK_FILE"
flock -n 9 || die "another backup is already running ($LOCK_FILE)"

# The whole run is captured: it becomes the body of the Healthchecks ping, so a failure arrives with
# its own log rather than a bare "did not run". tee keeps a manual run readable at the same time.
RUN_LOG="$(mktemp "/tmp/pedalons-backup-${ENV_NAME}.XXXXXX")"
exec 3>&1 4>&2
exec > >(tee -a "$RUN_LOG") 2>&1

# Restoring the original descriptors makes tee see EOF; `wait` then guarantees it has flushed before
# the log is read as a ping body. Without this the alert can carry a truncated tail.
flush_log() {
  exec 1>&3 2>&4
  wait 2>/dev/null || true
}

STAMP="$(date -u +%Y-%m-%dT%H%M%SZ)"
STAGE="${BACKUP_STAGE_DIR:-/var/backups/pedalons}/$ENV_NAME"

finish_failed() {
  local rc=$?
  ((rc == 0)) && return 0
  log "backup FAILED (exit $rc)"
  flush_log
  hc_ping "/fail" "$RUN_LOG"
  rm -f "$RUN_LOG"
}
trap finish_failed EXIT

hc_ping "/start"
log "backing up $ENV_NAME to $(remote_url "$STAMP")"

POSTGRES_CID="$(require_container postgres)"
MINIO_CID="$(require_container minio)"

rm -rf "$STAGE"
mkdir -p "$STAGE"
chmod 700 "$STAGE"

# --- 1. PostgreSQL ----------------------------------------------------------
#
# Postgres BEFORE minio, on purpose: AssetService uploads to S3 and only then persists the row, so a
# file landing mid-backup leaves an orphan object (harmless) rather than a row pointing at a missing
# object. pg_dump runs in a single transaction, so the dump is consistent without stopping anything.
# Credentials are read from the container's own environment — never typed into this shell.
log "dumping postgres ($POSTGRES_DB)"
docker exec -i "$POSTGRES_CID" \
  sh -c 'pg_dump -Fc -U "$POSTGRES_USER" -d "$POSTGRES_DB"' > "$STAGE/postgres.dump"
DUMP_SIZE="$(stat -c %s "$STAGE/postgres.dump")"
((DUMP_SIZE > 0)) || die "postgres.dump is empty"
log "postgres dump: $DUMP_SIZE bytes"

# --- 2. Secrets -------------------------------------------------------------
#
# Small but decisive: without data/keys every session and passkey is void, and without
# ENCRYPTION_KEY the stored Karoo/Garmin tokens can no longer be decrypted.
# data/storage is included even though nothing writes there today — it is a few kilobytes.
log "archiving secrets"
SECRET_PATHS=(.env data/keys)
[[ -d data/storage ]] && SECRET_PATHS+=(data/storage)

if [[ -n "${BACKUP_SECRETS_PASSPHRASE_FILE:-}" ]]; then
  [[ -r "$BACKUP_SECRETS_PASSPHRASE_FILE" ]] \
    || die "BACKUP_SECRETS_PASSPHRASE_FILE not readable: $BACKUP_SECRETS_PASSPHRASE_FILE"
  SECRETS_FILE="secrets.tar.gz.gpg"
  tar -czf - "${SECRET_PATHS[@]}" \
    | gpg --batch --symmetric --cipher-algo AES256 \
        --passphrase-file "$BACKUP_SECRETS_PASSPHRASE_FILE" > "$STAGE/$SECRETS_FILE"
else
  SECRETS_FILE="secrets.tar.gz"
  tar -czf - "${SECRET_PATHS[@]}" > "$STAGE/$SECRETS_FILE"
fi
chmod 600 "$STAGE/$SECRETS_FILE"

# --- 3. MANIFEST ------------------------------------------------------------
#
# Images are not backed up. This is what tells a restore which commit to rebuild.
log "writing MANIFEST"
{
  echo "snapshot=$STAMP"
  echo "env_name=$ENV_NAME"
  echo "host=$(hostname)"
  echo "repo=$REPO_ROOT"
  # -c safe.directory: the checkout belongs to the deploy user, this script runs as root, and git
  # refuses "dubious ownership" by default — which would silently reduce the commit to "unknown".
  echo "git_commit=$(git -c safe.directory="$REPO_ROOT" -C "$REPO_ROOT" rev-parse HEAD 2>/dev/null || echo unknown)"
  echo "git_branch=$(git -c safe.directory="$REPO_ROOT" -C "$REPO_ROOT" rev-parse --abbrev-ref HEAD 2>/dev/null || echo unknown)"
  echo "postgres_db=$POSTGRES_DB"
  echo "postgres_image=$(image_of "$POSTGRES_CID")"
  echo "backend_image=$(image_of "$(swarm_container_id backend)")"
  echo "frontend_image=$(image_of "$(swarm_container_id frontend)")"
  echo "postgres_dump_bytes=$DUMP_SIZE"
  echo "secrets_file=$SECRETS_FILE"
} > "$STAGE/MANIFEST"
(cd "$STAGE" && sha256sum postgres.dump "$SECRETS_FILE" > SHA256SUMS)

# --- 4. Push ----------------------------------------------------------------

PREV="$(latest_complete_snapshot || true)"
if [[ -n "$PREV" ]]; then
  log "previous snapshot: $PREV (used as --link-dest)"
else
  log "no previous snapshot — this run copies everything"
fi

log "pushing dump, secrets and manifest"
rsync_remote -a --mkpath "$STAGE"/ "$(remote_url "$STAMP")/"

# --- 5. MinIO ---------------------------------------------------------------
#
# rsync of the raw volume: only changed objects cross the wire, and --link-dest hard-links every
# unchanged one into the previous snapshot, so each dated directory reads as a full copy while
# costing only its delta on the backup disk.
#
# rrsync refuses any path containing "..", so the link target is named explicitly rather than
# reached with a relative path — hence looking up the previous snapshot above.
#
# MinIO stages writes in .minio.sys/tmp/ and renames them into place, so copying a live volume never
# catches a half-written object — which is what makes backing it up without stopping MinIO sound.
MINIO_PATH="$(minio_mountpoint)"

sync_minio() {
  local link=()
  [[ -n "$PREV" ]] && link=(--link-dest="$(remote_path "$PREV")/minio")
  # .minio.sys/tmp is MinIO's staging area: files appear and disappear there continuously, and
  # rsync rightly refuses them ("failed verification -- update discarded", exit 23). It holds
  # nothing a restore needs — MinIO recreates it — so it is excluded, while the rest of
  # .minio.sys (format.json, bucket metadata) is not: without it MinIO does not recognise its data.
  rsync_remote -a --numeric-ids --delete --mkpath \
    --exclude="/.minio.sys/tmp/**" --exclude="/.minio.sys/tmp.old/**" \
    "${link[@]}" \
    "$MINIO_PATH/" "$(remote_url "$STAMP")/minio/"
}

log "syncing minio from $MINIO_PATH"
set +e
sync_minio
RSYNC_RC=$?
# 23/24 mean files changed or vanished mid-run — an upload landing during the backup. A second pass
# picks up what the first one missed instead of failing the night's backup over it.
if ((RSYNC_RC == 23 || RSYNC_RC == 24)); then
  log "rsync exit $RSYNC_RC (files changed during the transfer) — second pass"
  sync_minio
  RSYNC_RC=$?
fi
set -e
case "$RSYNC_RC" in
  0) ;;
  24) log "rsync: some files vanished during the transfer (exit 24) — tolerated" ;;
  *) die "rsync of minio failed (exit $RSYNC_RC)" ;;
esac

# --- 6. Seal ----------------------------------------------------------------
#
# Last write of the run. Only a snapshot carrying this marker is offered by restore.sh, and only one
# is used as the next run's --link-dest.
date -u +%Y-%m-%dT%H:%M:%SZ > "$STAGE/COMPLETE"
rsync_remote -a "$STAGE/COMPLETE" "$(remote_url "$STAMP")/COMPLETE"

# The staged dump is the only local copy of the secrets and the database; it has done its job.
rm -rf "$STAGE"

log "backup complete: $(remote_url "$STAMP")"
trap - EXIT
flush_log
hc_ping "" "$RUN_LOG"
rm -f "$RUN_LOG"
