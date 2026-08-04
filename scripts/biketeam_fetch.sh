#!/usr/bin/env bash
# Prepare a restore from biketeam: pull the production data directory, dump the biketeam database
# on the remote host, pull the dump, and load it into `biketeam_import` via biketeam_restore.sh.
#
# This is the "Backup data" section of MIGRATE_BIKETEAM.md, unattended. Once it finishes, run the
# migration itself:  docker compose --profile restore run --rm backend-restore
#
# Usage:
#   scripts/biketeam_fetch.sh [options]
#
# Options:
#   --dest <dir>      where the export lands (default: ../biketeam-backup, next to the checkout)
#   --remote <user@host>   default: biketeam@main.tomacla.info
#   --remote-dir <path>    default: /home/biketeam/production
#   --skip-files      don't rsync the data directory, only redo the database dump
#   --skip-db         only rsync the data directory
#   --no-restore      stop after fetching; don't load the dump into biketeam_import
#
# The remote postgres password is read from the .env that comes with the production directory,
# so --skip-files only works once that directory has been fetched at least once.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

log() { printf '\n>> %s\n' "$*"; }
die() {
  printf 'ERROR: %s\n' "$*" >&2
  exit 1
}

DEST="$REPO_ROOT/../biketeam-backup"
REMOTE="biketeam@main.tomacla.info"
REMOTE_DIR="/home/biketeam/production"
REMOTE_DUMP="/tmp/biketeam_export.dump"
REMOTE_DB="biketeam_production"
REMOTE_DB_USER="biketeam"
DO_FILES=1
DO_DB=1
DO_RESTORE=1

while [[ $# -gt 0 ]]; do
  case "$1" in
    --dest) DEST="${2:?--dest needs a directory}"; shift 2 ;;
    --remote) REMOTE="${2:?--remote needs user@host}"; shift 2 ;;
    --remote-dir) REMOTE_DIR="${2:?--remote-dir needs a path}"; shift 2 ;;
    --skip-files) DO_FILES=0; shift ;;
    --skip-db) DO_DB=0; shift ;;
    --no-restore) DO_RESTORE=0; shift ;;
    -h|--help) sed -n '2,20p' "$0"; exit 0 ;;
    *) die "unknown option: $1" ;;
  esac
done

mkdir -p "$DEST"
DEST="$(cd "$DEST" && pwd)"

# --- 1. production data directory (GPX, images, and the .env holding the db password) -------------

if ((DO_FILES)); then
  log "rsync $REMOTE:$REMOTE_DIR -> $DEST/"
  rsync -avz "$REMOTE:$REMOTE_DIR" "$DEST/"
else
  log "skipping the data directory (--skip-files)"
fi

# --- 2. dump the database on the remote host ------------------------------------------------------

if ((DO_DB)); then
  REMOTE_ENV="$DEST/$(basename "$REMOTE_DIR")/.env"
  [[ -f "$REMOTE_ENV" ]] || die "no $REMOTE_ENV — run once without --skip-files to fetch it"

  # grep rather than sourcing: that file is the *remote* stack's environment, and sourcing it here
  # would silently shadow POSTGRES_USER/POSTGRES_PASSWORD for the local restore below.
  REMOTE_DB_PASSWORD="$(sed -n 's/^POSTGRES_PASSWORD=//p' "$REMOTE_ENV" | tail -1)"
  REMOTE_DB_PASSWORD="${REMOTE_DB_PASSWORD%\"}"
  REMOTE_DB_PASSWORD="${REMOTE_DB_PASSWORD#\"}"
  [[ -n "$REMOTE_DB_PASSWORD" ]] || die "no POSTGRES_PASSWORD in $REMOTE_ENV"

  log "pg_dump $REMOTE_DB on $REMOTE -> $REMOTE_DUMP"
  # The password goes over the SSH channel on stdin, never on the remote command line where it
  # would show up in that host's process list.
  printf '%s' "$REMOTE_DB_PASSWORD" | ssh "$REMOTE" \
    "PGPASSWORD=\$(cat) pg_dump -Fc -U '$REMOTE_DB_USER' -d '$REMOTE_DB' -h localhost -f '$REMOTE_DUMP'"

  log "rsync $REMOTE:$REMOTE_DUMP -> $DEST/"
  rsync -avz --progress "$REMOTE:$REMOTE_DUMP" "$DEST/"

  # Our own temp file, and it carries the whole member table — don't leave it on a shared host.
  ssh "$REMOTE" "rm -f '$REMOTE_DUMP'"
else
  log "skipping the database dump (--skip-db)"
fi

# --- 3. restore into biketeam_import --------------------------------------------------------------

DUMP="$DEST/$(basename "$REMOTE_DUMP")"

if ((DO_RESTORE)); then
  [[ -f "$DUMP" ]] || die "no dump at $DUMP"
  "$REPO_ROOT/scripts/biketeam_restore.sh" "$DUMP"
else
  log "fetched; skipping the restore (--no-restore)"
  echo "   scripts/biketeam_restore.sh $DUMP"
fi

cat <<EOF

Ready. Data directory: $DEST/$(basename "$REMOTE_DIR")/data
Next:  ./build.sh && docker compose --profile restore run --rm backend-restore
EOF
