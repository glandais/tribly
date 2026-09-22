#!/usr/bin/env bash
# Deploy — or update — this checkout's environment as the Swarm stack ${ENV_NAME}.
#
#   ./build.sh && scripts/deploy.sh   the build of the checked-out commit
#   scripts/deploy.sh --rev <commit>  an earlier build, without rebuilding (e.g. --rev HEAD~1)
#   scripts/deploy.sh --hold-app      backend and frontend at 0 replicas (used by restore.sh)
#   scripts/deploy.sh --shared        the shared stack instead, from the ~/shared checkout
#
# Never run `docker stack deploy` by hand on this file, for two reasons this script exists for:
#
#   - `docker stack deploy` reads no .env. Every ${VAR} of docker-compose.yml would expand to an
#     empty string, and only the few spelled `${VAR:?}` would stop it. The shared stack is no
#     exception: a VALHALLA_TILE_URLS set in its .env and silently replaced by the default costs a
#     rebuild of several hours.
#   - backend and frontend run the image tagged with a commit (scripts/_image_tag.sh), which this
#     script picks. A new tag is a new service spec, which Swarm rolls out start-first: the old task
#     serves until the new one is healthy, and a new task that never gets there is rolled back.
#
# The stack is named after ENV_NAME, not after the checkout: scripts/backup.sh and restore.sh find
# its containers and volumes (${ENV_NAME}_postgres_data...) by that name.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

log() { printf '%s  %s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*"; }
die() {
  printf '%s  ERROR: %s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*" >&2
  exit 1
}

# Builds kept per image, besides the one running: how far back --rev can go without rebuilding.
KEEP_BUILDS=5

HOLD_APP=false
SHARED=false
REV=""
case "${1:-}" in
  "") ;;
  --hold-app) HOLD_APP=true ;;
  --shared) SHARED=true ;;
  --rev) REV="${2:?--rev needs a commit}" ;;
  -h | --help) sed -n '2,21p' "$0"; exit 0 ;;
  *) die "unknown argument: $1 (try --help)" ;;
esac

cd "$REPO_ROOT"
. "$SCRIPT_DIR/_image_tag.sh"
[[ "$(docker info --format '{{.Swarm.LocalNodeState}}' 2>/dev/null)" == "active" ]] \
  || die "this node is not in a swarm — run 'docker swarm init' once (see README.md, Deployment)"

if $SHARED; then
  # Its .env is optional: it only ever carries VALHALLA_TILE_URLS.
  if [[ -f .env ]]; then
    set -a
    . ./.env
    set +a
  fi
  log "deploying stack pedalons-shared"
  docker stack deploy -c docker-compose.shared.yml pedalons-shared
  exit 0
fi

[[ -f .env ]] || die "no .env in $REPO_ROOT — copy .env.example and fill it in"
# Same parsing as build.sh, which the values have to survive anyway.
set -a
. ./.env
set +a
[[ -n "${ENV_NAME:-}" ]] || die "ENV_NAME is not set in .env"
[[ -z "${COMPOSE_FILE:-}" ]] \
  || die "COMPOSE_FILE is set in .env: that is a workstation, which runs compose, not a stack"

docker network inspect pedalons-shared >/dev/null 2>&1 \
  || die "network pedalons-shared missing — deploy the shared stack first (scripts/deploy.sh --shared)"
if [[ -z "$REV" ]] && worktree_dirty; then
  die "uncommitted changes in $REPO_ROOT: deploy a commit — commit or stash them, or pass --rev"
fi
suffix="$(image_suffix "${REV:-HEAD}")" || die "unknown commit: $REV"
for image in "pedalons-backend:$ENV_NAME$suffix" "pedalons-frontend:$ENV_NAME$suffix"; do
  docker image inspect "$image" >/dev/null 2>&1 || die "image $image missing — run ./build.sh"
done
# Read by docker-compose.yml, one per service so that each could move alone.
export BACKEND_IMAGE_SUFFIX="$suffix" FRONTEND_IMAGE_SUFFIX="$suffix"
[[ -f data/keys/privateKey.pem && -f data/keys/publicKey.pem ]] \
  || die "no JWT keys in data/keys — run data/keys/generate-keys.sh"
# Compose creates a missing bind-mount source; Swarm refuses to start the task instead.
mkdir -p data/storage data/cache

files=(-c docker-compose.yml)
if $HOLD_APP; then
  hold="$(mktemp)"
  trap 'rm -f "$hold"' EXIT
  printf 'services:\n  backend:\n    deploy:\n      replicas: 0\n  frontend:\n    deploy:\n      replicas: 0\n' > "$hold"
  files+=(-c "$hold")
fi

log "deploying stack $ENV_NAME with the build of ${suffix#-}"
# Swarm warns that it cannot resolve these images on a registry: there is none, the images are on
# this node, which is all a single-node swarm needs.
docker stack deploy --prune "${files[@]}" "$ENV_NAME"

$HOLD_APP && exit 0

# Drop old builds, newest KEEP_BUILDS kept. The image in each service's spec is never dropped, nor
# the one of its previous spec — what `docker service rollback` goes back to.
for service in backend frontend; do
  keep=()
  for field in Spec PreviousSpec; do
    keep+=("$(docker service inspect -f "{{with .$field}}{{.TaskTemplate.ContainerSpec.Image}}{{end}}" \
      "${ENV_NAME}_$service" 2>/dev/null || true)")
  done
  docker images "pedalons-$service" --format '{{.Repository}}:{{.Tag}}' \
    | grep -E "^pedalons-$service:$ENV_NAME-[0-9a-f]{12}\$" \
    | tail -n +$((KEEP_BUILDS + 1)) \
    | while read -r image; do
      [[ " ${keep[*]} " == *" $image"[@\ ]* ]] && continue
      docker rmi "$image" >/dev/null && log "dropped old build $image"
    done
done

log "deploying. Follow the rollout with: docker service ps ${ENV_NAME}_backend"
log "back to the previous build: docker service rollback ${ENV_NAME}_backend (or --rev <commit>)"
