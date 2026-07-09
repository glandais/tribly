#!/usr/bin/env bash
# Restore a biketeam pg_dump (custom format, -Fc) into a fresh `biketeam_import` database
# alongside the main `pedalons` database, ready for the in-app migration runner.
#
# Usage:
#   scripts/biketeam_restore.sh <dump_file> [pg_user] [pg_host] [pg_port]
#
# Defaults are read from .env (POSTGRES_USER / POSTGRES_HOST_PORT) so they match the
# postgres service of docker-compose.yml, which publishes 5432 on the loopback only.
set -euo pipefail

ENV_FILE="$(dirname "$0")/../.env"
if [[ -f "$ENV_FILE" ]]; then
  # shellcheck disable=SC1090
  set -a && source "$ENV_FILE" && set +a
fi

DUMP="${1:?Usage: $0 <dump_file> [pg_user] [pg_host] [pg_port]}"
PG_USER="${2:-${POSTGRES_USER:-tribly}}"
PG_HOST="${3:-localhost}"
PG_PORT="${4:-${POSTGRES_HOST_PORT:-5432}}"
TARGET_DB="biketeam_import"

# Avoid an interactive prompt when .env carries the password.
if [[ -z "${PGPASSWORD:-}" && -n "${POSTGRES_PASSWORD:-}" ]]; then
  export PGPASSWORD="$POSTGRES_PASSWORD"
fi

if [[ ! -f "$DUMP" ]]; then
  echo "ERROR: dump file not found: $DUMP" >&2
  exit 1
fi

echo ">> Dropping + recreating $TARGET_DB on $PG_HOST:$PG_PORT as $PG_USER"
psql -U "$PG_USER" -h "$PG_HOST" -p "$PG_PORT" -d postgres -v ON_ERROR_STOP=1 <<SQL
DROP DATABASE IF EXISTS ${TARGET_DB};
CREATE DATABASE ${TARGET_DB} OWNER ${PG_USER};
SQL

echo ">> Restoring $DUMP into $TARGET_DB"
# Restore everything; Spring Session / oauth2_authorized_client tables are restored but
# never queried by the migration service. pg_restore doesn't support --exclude-table.
pg_restore --no-owner --no-acl --no-privileges \
  -U "$PG_USER" -h "$PG_HOST" -p "$PG_PORT" -d "$TARGET_DB" \
  "$DUMP"

echo
echo "Restore complete."
