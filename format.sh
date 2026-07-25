#!/usr/bin/env bash
# Formats every module. Run this before any commit.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

ALL_MODULES=(backend frontend mobile karoo garmin-app)

usage() {
  local joined
  joined="$(printf '%s|' "${ALL_MODULES[@]}")"
  echo "Usage: $0 [all|${joined%|}]" >&2
  echo "       $0                 # same as: $0 all" >&2
  exit 1
}

# Fail loudly rather than silently skipping a module: a partially formatted commit
# is worse than an obvious error telling you what to install.
need() {
  command -v "$1" >/dev/null 2>&1 ||
    { echo "format.sh: '$1' not found in PATH — required to format $2" >&2; exit 1; }
}

banner() { echo "==> formatting $1"; }

format_backend() (
  banner backend
  need mvn backend
  cd "$ROOT/backend"
  mvn -q spotless:apply
)

format_frontend() (
  banner frontend
  need pnpm frontend
  cd "$ROOT/frontend"
  [ -d node_modules ] ||
    { echo "format.sh: frontend/node_modules is missing — run 'pnpm install' first" >&2; exit 1; }
  # Formatting only needs prettier, so don't let pnpm's deps-vs-lockfile check (which wants a
  # TTY to purge node_modules) turn a slightly stale install into a failed format.
  pnpm --config.verify-deps-before-run=false run format
)

format_mobile() (
  banner mobile
  need dart mobile
  cd "$ROOT/mobile"
  # `dart format` has no --exclude flag, and generated sources are committed but already
  # formatted by their generators, so filter them out here.
  find lib test -name '*.dart' \
    -not -path 'lib/api/generated/*' \
    -not -name '*.g.dart' \
    -not -name '*.freezed.dart' \
    -print0 | xargs -0 dart format
)

format_karoo() (
  banner karoo
  need java karoo
  cd "$ROOT/karoo"
  ./gradlew --quiet spotlessApply
)

format_garmin_app() (
  banner garmin-app
  need npx garmin-app
  cd "$ROOT/garmin-app"
  # No package.json here on purpose — Monkey C tooling is not a Node project.
  # Versions are pinned so formatting stays reproducible across machines.
  # Prettier resolves --plugin relative to the CWD, not to the npx cache, so the plugin
  # has to be passed as an absolute path to its entry point (derived from the npx bin dir).
  npx --yes --package=prettier@3 --package=@markw65/prettier-plugin-monkeyc@1.0.69 sh -c '
    plugin="${PATH%%:*}/../@markw65/prettier-plugin-monkeyc"
    entry=$(node -p "require(\"$plugin/package.json\").main")
    prettier --plugin="$plugin/$entry" --write "source/**/*.mc"'
)

format_module() {
  case "$1" in
    backend) format_backend ;;
    frontend) format_frontend ;;
    mobile) format_mobile ;;
    karoo) format_karoo ;;
    garmin-app) format_garmin_app ;;
    *) echo "format.sh: unknown module '$1'" >&2; usage ;;
  esac
}

if [ "$#" -eq 0 ] || { [ "$#" -eq 1 ] && [ "$1" = "all" ]; }; then
  TARGETS=("${ALL_MODULES[@]}")
else
  TARGETS=("$@")
fi

for module in "${TARGETS[@]}"; do
  format_module "$module"
done
