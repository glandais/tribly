#!/usr/bin/env bash
# Patrol end-to-end tests (patrol_test/) against the e2e stack — see patrol_test/README.md.
#
#   bash e2e.sh                                      # every test in patrol_test/
#   bash e2e.sh -t patrol_test/logout_test.dart      # one test; any `patrol test` argument passes through
#
# The stack comes first: `scripts/e2e.sh up` from the repository root. Ports are read from the
# committed ../.env.e2e, so they are written down once for the web and the mobile suites.
#
# The device is the iOS simulator named by PATROL_DEVICE (a UDID), or else the one called
# "iPhone 17 Pro Max". Never `booted`: that hits whichever simulator happens to be up.
set -euo pipefail
cd "$(dirname "$0")"

env_value() { grep -E "^$1=" ../.env.e2e | cut -d= -f2-; }
api_url="http://localhost:$(env_value HTTP_PORT)"
mailhog_url="http://localhost:$(env_value E2E_MAILHOG_PORT)"

if ! curl -fsS -o /dev/null "$api_url/api/config"; then
  echo "e2e: nothing answers on $api_url — start the stack with scripts/e2e.sh up" >&2
  exit 1
fi

device="${PATROL_DEVICE:-}"
if [[ -z "$device" ]]; then
  device=$(xcrun simctl list devices available -j | python3 -c '
import json, sys
devices = [d for runtime in json.load(sys.stdin)["devices"].values() for d in runtime]
print(next((d["udid"] for d in devices if d["name"] == "iPhone 17 Pro Max"), ""))')
fi
if [[ -z "$device" ]]; then
  echo "e2e: no \"iPhone 17 Pro Max\" simulator — set PATROL_DEVICE to a simulator UDID" >&2
  exit 1
fi
xcrun simctl bootstatus "$device" -b >/dev/null

export PATH="$PATH:$HOME/.pub-cache/bin"
exec patrol test \
  --device "$device" \
  --dart-define=API_BASE_URL="$api_url" \
  --dart-define=E2E_MAILHOG_URL="$mailhog_url" \
  "$@"
