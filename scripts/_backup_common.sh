#!/usr/bin/env bash
# Shared helpers for scripts/backup.sh and scripts/restore.sh.
# Sourced, never executed on its own.

# Repository root, whatever directory the caller ran the script from.
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

log() { printf '%s  %s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*"; }
die() {
  printf '%s  ERROR: %s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*" >&2
  exit 1
}

# Load .env, then the backup configuration. The two are deliberately separate files: docker-compose
# passes the whole .env to the backend container (`env_file`), so the destination, the SSH key and
# the Healthchecks URL would end up in the application's environment — and in the backup of it.
# BACKUP_ENV_FILE defaults to /root/pedalons-backup.env, root-readable only and not versioned.
load_backup_env() {
  local backup_env="${BACKUP_ENV_FILE:-/root/pedalons-backup.env}"
  [[ -f "$backup_env" ]] || return 0
  set -a
  # shellcheck disable=SC1090
  . "$backup_env"
  set +a
}

# Kept separate from load_backup_env: restoring onto a fresh host starts with no .env at all — that
# is what --secrets-only is for — and the backup configuration still has to be readable there.
load_env() {
  local env_file="${1:-$REPO_ROOT/.env}"
  [[ -f "$env_file" ]] || die "no .env at $env_file"
  set -a
  # shellcheck disable=SC1090
  . "$env_file"
  set +a
  load_backup_env
}

require_vars() {
  local missing=()
  local name
  for name in "$@"; do
    [[ -n "${!name:-}" ]] || missing+=("$name")
  done
  ((${#missing[@]} == 0)) || die "missing required variable(s): ${missing[*]} (see .env.example)"
}

# --- remote access ----------------------------------------------------------
#
# Everything goes through rsync and nothing else. The receiving account's key is restricted to
# `command="rrsync <root>"`, which accepts an `rsync --server` invocation and refuses every other
# command: no `ssh host 'mkdir'`, no remote `cat >`, no remote `rm`. That is the point — a
# compromised production host cannot read or wreck anything outside its own backup root.

rsh() {
  local opts=(-o BatchMode=yes -o ConnectTimeout=15)
  [[ -n "${BACKUP_SSH_KEY:-}" ]] && opts+=(-i "$BACKUP_SSH_KEY")
  [[ -n "${BACKUP_SSH_PORT:-}" ]] && opts+=(-p "$BACKUP_SSH_PORT")
  printf 'ssh %s' "${opts[*]}"
}

rsync_remote() { rsync -e "$(rsh)" "$@"; }

# Snapshot root as rsync addresses it. With a restricted key BACKUP_REMOTE_PATH is "/" — rrsync
# anchors every absolute path at its own root — and with a normal account it is the real path.
# The same expression works for both, which is what lets a restore read the same store over a
# plain SSH account.
remote_path() { printf '%s' "${BACKUP_REMOTE_PATH%/}/$1"; }
remote_url() { printf '%s:%s' "$BACKUP_REMOTE" "$(remote_path "$1")"; }

# Dated snapshot directories, oldest first. rsync's own listing is the only directory read the
# restricted key allows.
list_snapshots() {
  rsync_remote --list-only "$(remote_url '')/" 2>/dev/null \
    | awk '$1 ~ /^d/ {print $NF}' | grep -E '^20[0-9]{2}-' | sort
}

# A snapshot is only usable once its COMPLETE marker landed — it is the last thing a run writes.
# Without it, an interrupted run would leave a dated directory that looks like a backup: the
# restricted key cannot delete a partial snapshot, so it has to be recognisable instead.
# -R is what makes this work: without it rsync lists a globbed file under its basename alone, so
# every marker comes back as plain "COMPLETE" — same name for every snapshot, and rsync only shows
# one of them. With it, the listing carries "<snapshot>/COMPLETE".
# The listed path is relative to the rrsync root with the restricted key ("<snapshot>/COMPLETE") but
# absolute over a plain SSH account ("/home/backup-pedalons/<snapshot>/COMPLETE") — a restore drill
# reads the same store through the second. Hence taking the last component rather than anchoring.
list_complete_snapshots() {
  rsync_remote --list-only -R "$(remote_url '*')/COMPLETE" 2>/dev/null \
    | awk '{print $NF}' \
    | sed -n 's#/COMPLETE$##p' \
    | awk -F/ '{print $NF}' \
    | grep -E '^20[0-9]{2}-' | sort
}

latest_complete_snapshot() { list_complete_snapshots | tail -1; }

# --- docker -----------------------------------------------------------------

require_container() {
  docker inspect "$1" >/dev/null 2>&1 || die "container $1 not found — is the stack up?"
}

# Image a container runs, or "absent" when the container is not there. `docker inspect -f` still
# emits a newline when it fails, so a bare `|| echo absent` yields an empty line *and* the fallback.
image_of() {
  local image
  image="$(docker inspect -f '{{.Config.Image}}' "$1" 2>/dev/null || true)"
  printf '%s' "${image:-absent}"
}

# Host path backing /data in the minio container. Asked of the running container rather than derived
# from the compose project name, which is the checkout directory's basename and so differs between
# hosts (~/prod, ~/staging, /home/pedalons/prod...). Using .Source also means a bind mount works as
# well as the named volume. Reading it needs root — the backup runs from root's crontab.
minio_mountpoint() {
  local path
  path="$(docker inspect "${ENV_NAME}-minio" \
    --format '{{range .Mounts}}{{if eq .Destination "/data"}}{{.Source}}{{end}}{{end}}')"
  [[ -n "$path" ]] || die "could not resolve what backs /data in ${ENV_NAME}-minio"
  [[ -d "$path" ]] || die "minio data directory not readable: $path (run as root?)"
  printf '%s' "$path"
}

# --- healthchecks -----------------------------------------------------------
#
# $1 = "/start", "/fail" or empty; $2 = optional log file sent as the body.
hc_ping() {
  [[ -n "${BACKUP_PING_URL:-}" ]] || return 0
  if [[ -n "${2:-}" && -f "${2:-}" ]]; then
    tail -c 10000 "$2" | curl -fsS -m 15 --retry 3 --data-binary @- "${BACKUP_PING_URL}$1" >/dev/null 2>&1 || true
  else
    curl -fsS -m 15 --retry 3 "${BACKUP_PING_URL}$1" >/dev/null 2>&1 || true
  fi
}
