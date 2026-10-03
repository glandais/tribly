#!/usr/bin/env bash
# Patrol end-to-end tests (patrol_test/) against the e2e stack — see patrol_test/README.md.
#
#   bash e2e.sh                                      # every test in patrol_test/
#   bash e2e.sh -t patrol_test/logout_test.dart      # one test; any `patrol test` argument passes through
#   E2E_PLATFORM=android bash e2e.sh                 # on the Android emulator (emulator-5554)
#
# The stack comes first: `scripts/e2e.sh up` from the repository root.
set -euo pipefail
cd "$(dirname "$0")"
source tool/e2e_env.sh

if ! curl -fsS -o /dev/null "$E2E_API_URL/api/config"; then
  echo "e2e: nothing answers on $E2E_API_URL — start the stack with scripts/e2e.sh up" >&2
  exit 1
fi
e2e_prepare_device
# On Android, patrol builds the app through Gradle: stop the daemon it spawned, even when a
# test fails.
if [[ "$E2E_PLATFORM" == android ]]; then
  trap '(cd android && ./gradlew --quiet --stop)' EXIT
fi

# shellcheck disable=SC2086 # E2E_DART_DEFINES holds several flags, none with a space.
patrol test --device "$E2E_DEVICE" $E2E_DART_DEFINES "$@"
