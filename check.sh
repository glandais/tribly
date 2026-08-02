#!/usr/bin/env bash
# Full local gate, mirroring .github/workflows/ci.yml: clean mobile, regenerate the API contract
# and its generated clients, format every module, then lint/analyze/build each of them. Backend
# tests are the slowest step and are skipped by default — pass --backend-tests to run them too.
# Each module also has its own check.sh for running that module's gate standalone.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

RUN_BACKEND_TESTS=false
for arg in "$@"; do
  case "$arg" in
    --backend-tests) RUN_BACKEND_TESTS=true ;;
    *)
      echo "check.sh: unknown option '$arg'" >&2
      echo "Usage: $0 [--backend-tests]" >&2
      exit 1
      ;;
  esac
done

step() { echo "==> $1"; }

step "mobile: clean"
(cd "$ROOT/mobile" && bash clean.sh)

step "backend: build + lint (also regenerates contracts/openapi.yaml)"
(cd "$ROOT/backend" && bash check.sh)

step "frontend: install + generate + format + typecheck + lint + build + test"
(cd "$ROOT/frontend" && bash check.sh)

step "mobile: regenerate + format + analyze + test"
(cd "$ROOT/mobile" && bash check.sh)

step "karoo: format + build"
(cd "$ROOT/karoo" && bash check.sh)

if [ "$RUN_BACKEND_TESTS" = true ]; then
  step "backend: tests (optional, --backend-tests)"
  mkdir -p "$ROOT/backend/keys"
  [ -f "$ROOT/backend/keys/privateKey.pem" ] ||
    openssl genrsa -out "$ROOT/backend/keys/privateKey.pem" 2048
  [ -f "$ROOT/backend/keys/publicKey.pem" ] ||
    openssl rsa -in "$ROOT/backend/keys/privateKey.pem" -pubout -out "$ROOT/backend/keys/publicKey.pem"
  (cd "$ROOT/backend" && mvn test)
else
  step "backend: tests skipped (pass --backend-tests to run)"
fi
