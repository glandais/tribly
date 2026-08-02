#!/usr/bin/env bash
# Bootstrap the Docker secrets `docker stack deploy` needs before the first deploy of an
# environment. Unlike a plain `.env` value, a `secrets:` entry in docker-compose.yml with
# `external: true` is NOT created on demand -- `docker stack deploy` fails outright if it's missing.
#
#   scripts/create-secrets.sh
#
# Run once per environment, before the first `docker stack deploy -c docker-compose.yml
# "$ENV_NAME"`, and again any time a new secret-bearing var is added to the list below.
#
# Docker secrets are IMMUTABLE: `docker secret create` on a name that already exists fails, and
# there is no in-place update. This script is therefore idempotent by skipping names that already
# exist, never by overwriting them. To rotate a secret's value:
#   1. edit .env with the new value
#   2. pick a new secret name (bump the version suffix, e.g. postgres_password_v2)
#   3. run this script again
#   4. update the `secrets:` external name for that entry in docker-compose.yml
#   5. docker stack deploy -c docker-compose.yml "$ENV_NAME"
#
# Also writes the same plaintext values under ./data/secrets/, which docker-compose.restore.yml
# reads as file-backed local secrets (backend-restore runs via plain `docker compose run`, outside
# the Swarm secret store, so both entry points read from one place here instead of two).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=scripts/_backup_common.sh
. "$SCRIPT_DIR/_backup_common.sh"

cd "$REPO_ROOT"
load_env
require_vars ENV_NAME

# env var name -> secret key (matches the `secrets:` block in docker-compose.yml /
# docker-compose.restore.yml).
declare -A SECRET_VARS=(
  [POSTGRES_PASSWORD]=postgres_password
  [MINIO_SECRET_KEY]=minio_secret_key
  [BREVO_API_KEY]=brevo_api_key
  [ENCRYPTION_KEY]=encryption_key
  [QUARKUS_MAILER_PASSWORD]=quarkus_mailer_password
)

SECRETS_DIR="$REPO_ROOT/data/secrets"
mkdir -p "$SECRETS_DIR"
chmod 700 "$SECRETS_DIR"

for var in "${!SECRET_VARS[@]}"; do
  key="${SECRET_VARS[$var]}"
  value="${!var:-}"
  [[ -n "$value" ]] || { log "skipping $var: empty in .env"; continue; }

  secret_name="${ENV_NAME}_${key}"
  if docker secret inspect "$secret_name" >/dev/null 2>&1; then
    log "docker secret $secret_name already exists — leaving it as is (secrets are immutable)"
  else
    printf '%s' "$value" | docker secret create "$secret_name" -
    log "created docker secret $secret_name"
  fi

  printf '%s' "$value" > "$SECRETS_DIR/$key"
  chmod 600 "$SECRETS_DIR/$key"
done

log "done. Secret files for docker-compose.restore.yml are in $SECRETS_DIR (not versioned -- see .gitignore)."
