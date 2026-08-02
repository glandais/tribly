#!/usr/bin/env bash
# Build + lint. Tests are separate (--tests) since they're the slowest step and need
# TestContainers (PostgreSQL + MinIO) running.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

RUN_TESTS=false
for arg in "$@"; do
  case "$arg" in
    --tests) RUN_TESTS=true ;;
    *)
      echo "check.sh: unknown option '$arg'" >&2
      echo "Usage: $0 [--tests]" >&2
      exit 1
      ;;
  esac
done

../format.sh backend

# Also regenerates contracts/openapi.yaml, consumed by frontend/mobile generators.
mvn -q clean package -DskipTests

mvn -q checkstyle:check

if [ "$RUN_TESTS" = true ]; then
  mkdir -p keys
  [ -f keys/privateKey.pem ] || openssl genrsa -out keys/privateKey.pem 2048
  [ -f keys/publicKey.pem ] || openssl rsa -in keys/privateKey.pem -pubout -out keys/publicKey.pem
  mvn test
fi
