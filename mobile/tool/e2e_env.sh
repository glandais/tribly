# Sourced by e2e.sh and tool/patrol_mcp.sh: where the e2e stack answers and which simulator runs
# the tests. Sets E2E_API_URL, E2E_MAILPIT_URL, E2E_ADMIN_EMAIL, E2E_DEVICE and E2E_DART_DEFINES.
#
# Ports come from the committed ../.env.e2e, written down once for the web and the mobile suites.
# The device is the iOS simulator named by PATROL_DEVICE (a UDID), or else the one called
# "iPhone 17 Pro Max". Never `booted`: that hits whichever simulator happens to be up.

_e2e_mobile_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
_e2e_env_value() { grep -E "^$1=" "$_e2e_mobile_dir/../.env.e2e" | cut -d= -f2-; }

E2E_API_URL="http://localhost:$(_e2e_env_value HTTP_PORT)"
E2E_MAILPIT_URL="http://localhost:$(_e2e_env_value E2E_MAILPIT_PORT)"
E2E_ADMIN_EMAIL="$(_e2e_env_value PEDALONS_BOOTSTRAP_ADMIN_EMAIL)"
E2E_DART_DEFINES="--dart-define=API_BASE_URL=$E2E_API_URL --dart-define=E2E_MAILPIT_URL=$E2E_MAILPIT_URL --dart-define=E2E_ADMIN_EMAIL=$E2E_ADMIN_EMAIL"

E2E_DEVICE="${PATROL_DEVICE:-}"
if [[ -z "$E2E_DEVICE" ]]; then
  E2E_DEVICE=$(xcrun simctl list devices available -j | python3 -c '
import json, sys
devices = [d for runtime in json.load(sys.stdin)["devices"].values() for d in runtime]
print(next((d["udid"] for d in devices if d["name"] == "iPhone 17 Pro Max"), ""))')
fi
if [[ -z "$E2E_DEVICE" ]]; then
  echo "e2e: no \"iPhone 17 Pro Max\" simulator — set PATROL_DEVICE to a simulator UDID" >&2
  return 1
fi

export PATH="$PATH:$HOME/.pub-cache/bin"
