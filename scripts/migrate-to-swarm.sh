#!/usr/bin/env bash
# One-off: move an environment that runs under `docker compose` onto its Swarm stack, data included.
#
#   scripts/migrate-to-swarm.sh
#
# Compose named the volumes after the project — the checkout directory: prod_postgres_data. A stack
# names them after itself: ${ENV_NAME}_postgres_data. Deployed without this, the stack would boot on
# two new empty volumes: Flyway on an empty schema, a freshly bootstrapped Domain, and the real data
# left aside where nothing reads it.
#
# So this stops the compose containers (never `-v`), and copies each volume into the one the stack
# will use. The old volumes are left in place — they are the way back — and are dropped by hand once
# the stack has proved itself. The whole host procedure, shared stack included, is in README.md
# ("Moving a host from compose to Swarm"). Run it after `docker swarm init`, before scripts/deploy.sh.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=scripts/_backup_common.sh
. "$SCRIPT_DIR/_backup_common.sh"

cd "$REPO_ROOT"
load_env
require_vars ENV_NAME
[[ -z "${COMPOSE_FILE:-}" ]] || die "COMPOSE_FILE is set in .env: a workstation stays on compose"

# Where the data lives today: asked of the compose containers while they still exist, and otherwise
# derived from the project name the way compose does (lowercased, [a-z0-9_-] only).
volume_of() { # $1 = container, $2 = mount point
  docker inspect "$1" --format "{{range .Mounts}}{{if eq .Destination \"$2\"}}{{.Name}}{{end}}{{end}}" 2>/dev/null || true
}
project="${COMPOSE_PROJECT_NAME:-$(basename "$REPO_ROOT" | tr '[:upper:]' '[:lower:]' | tr -cd 'a-z0-9_-')}"
OLD_PG="$(volume_of "${ENV_NAME}-postgres" /var/lib/postgresql/data)"
OLD_MINIO="$(volume_of "${ENV_NAME}-minio" /data)"
OLD_PG="${OLD_PG:-${project}_postgres_data}"
OLD_MINIO="${OLD_MINIO:-${project}_minio_data}"
NEW_PG="${ENV_NAME}_postgres_data"
NEW_MINIO="${ENV_NAME}_minio_data"

for volume in "$OLD_PG" "$OLD_MINIO"; do
  docker volume inspect "$volume" >/dev/null 2>&1 \
    || die "volume $volume not found — set COMPOSE_PROJECT_NAME to the project compose ran under"
done
[[ "$OLD_PG" != "$NEW_PG" ]] || die "the data already lives in $NEW_PG — nothing to move"

log "moving $ENV_NAME: $OLD_PG -> $NEW_PG, $OLD_MINIO -> $NEW_MINIO"
if [[ -z "${FORCE:-}" ]]; then
  read -r -p "  This stops the compose containers of $ENV_NAME. Type the environment name to go on: " answer
  [[ "$answer" == "$ENV_NAME" ]] || die "aborted"
fi

if [[ -n "$(docker ps -aq --filter "name=^${ENV_NAME}-")" ]]; then
  log "stopping the compose containers (volumes kept)"
  docker compose -p "$project" down --remove-orphans
fi
[[ -z "$(docker ps -q --filter "volume=$OLD_PG")$(docker ps -q --filter "volume=$OLD_MINIO")" ]] \
  || die "a container still uses $OLD_PG or $OLD_MINIO — stop it first"

# A target that already holds files is refused rather than merged: it is either a stack deployed too
# early — whose empty bootstrap must not be mixed with the real data — or a previous run of this.
for pair in "$OLD_PG:$NEW_PG" "$OLD_MINIO:$NEW_MINIO"; do
  from="${pair%%:*}"
  to="${pair#*:}"
  if docker volume inspect "$to" >/dev/null 2>&1; then
    [[ -z "$(docker run --rm -v "$to:/to:ro" alpine ls -A /to)" ]] \
      || die "$to already holds data — if a stack was deployed on it too early: docker stack rm $ENV_NAME, docker volume rm $to, then run this again"
  fi
  log "copying $from -> $to"
  # -a keeps owners and modes: postgres runs as uid 70 in its alpine image, MinIO as root.
  docker run --rm -v "$from:/from:ro" -v "$to:/to" alpine cp -a /from/. /to/
done

log "done. Next: scripts/deploy.sh — then, once the site is checked, drop $OLD_PG and $OLD_MINIO."
