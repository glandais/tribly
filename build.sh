#!/usr/bin/env bash
set -euo pipefail

# What to build: all (default) | frontend | backend
# ENV_FILE picks another env file than ./.env — its ENV_NAME becomes the image tag (scripts/e2e.sh
# builds the tribly-e2e images this way).
TARGET="${1:-all}"
case "$TARGET" in
  all | frontend | backend) ;;
  *)
    echo "Usage: $0 [all|frontend|backend]" >&2
    exit 1
    ;;
esac

# `export $(... | xargs)` splits values on whitespace, quoted or not, so a display name like
# "Gaby Landais" would be exported as two broken words. Let the shell parse the file instead.
set -a
. "${ENV_FILE:-./.env}"
set +a

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

build_backend() {
  cd "$ROOT/backend"
  export QUARKUS_CONTAINER_IMAGE_GROUP=""
  export QUARKUS_CONTAINER_IMAGE_NAME="pedalons-backend"
  export QUARKUS_CONTAINER_IMAGE_TAG="$ENV_NAME"
  mvn clean package -DskipTests -Dquarkus.container-image.build=true
}

build_frontend() {
  cd "$ROOT/frontend"
  mkdir -p src/assets/legal
  cp "$ROOT"/privacy/*.md src/assets/legal/
  docker build --progress=plain \
    --build-arg VITE_BUILD_SOURCEMAP="${FRONTEND_SOURCEMAP:-false}" \
    --build-arg FRONTEND_PREFETCH_AUDIT="${FRONTEND_PREFETCH_AUDIT:-false}" \
    -t "pedalons-frontend:$ENV_NAME" .
  rm -rf src/assets/legal
}

if [ "$TARGET" = "all" ] || [ "$TARGET" = "backend" ]; then
  build_backend
fi
if [ "$TARGET" = "all" ] || [ "$TARGET" = "frontend" ]; then
  build_frontend
fi
