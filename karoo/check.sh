#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

../format.sh karoo
./gradlew --quiet assembleDebug
