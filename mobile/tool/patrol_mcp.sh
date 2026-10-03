#!/usr/bin/env bash
# Patrol MCP server (registered in ../.mcp.json as `patrol`): lets an agent run a test of
# patrol_test/ in a `patrol develop` session, take screenshots and read the native UI tree.
# Same stack, same device (E2E_PLATFORM) and same dart-defines as e2e.sh — never production.
set -euo pipefail
cd "$(dirname "$0")/.."
source tool/e2e_env.sh
if [[ "$E2E_PLATFORM" == android ]]; then e2e_prepare_device; fi

export PROJECT_ROOT="$PWD"
export PATROL_FLAGS="--device $E2E_DEVICE $E2E_DART_DEFINES"
export SHOW_TERMINAL="${SHOW_TERMINAL:-false}"
if [[ "$E2E_PLATFORM" != android ]]; then
  exec dart run patrol_mcp
fi
# On Android, `patrol develop` builds the app through Gradle: once the server is gone, stop the
# daemon it spawned. No exec, so this shell outlives the server; a signal waits for the server
# to exit, then goes through the EXIT trap.
trap '(cd android && ./gradlew --quiet --stop)' EXIT
trap 'exit 143' TERM INT HUP
dart run patrol_mcp
