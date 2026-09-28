#!/usr/bin/env bash
# Patrol MCP server (registered in ../.mcp.json as `patrol`): lets an agent run a test of
# patrol_test/ in a `patrol develop` session, take screenshots and read the native UI tree.
# Same stack, same simulator and same dart-defines as e2e.sh — never production.
set -euo pipefail
cd "$(dirname "$0")/.."
source tool/e2e_env.sh

export PROJECT_ROOT="$PWD"
export PATROL_FLAGS="--device $E2E_DEVICE $E2E_DART_DEFINES"
export SHOW_TERMINAL="${SHOW_TERMINAL:-false}"
exec dart run patrol_mcp
