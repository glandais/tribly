#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

# install + generate-api + generate-routes + format + typecheck + lint + build
pnpm run check
pnpm test run
