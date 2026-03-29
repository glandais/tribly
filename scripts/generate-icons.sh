#!/usr/bin/env bash
# generate-icons.sh — Generate all platform icons from assets/icon.svg
#
# Prerequisites:
#   - inkscape (>= 1.0) OR rsvg-convert (apt install librsvg2-bin)
#   - imagemagick (apt install imagemagick)
#   - pnpm (for web icons)
#   - dart + flutter (for mobile icons)
#
# Usage: ./scripts/generate-icons.sh [--web] [--mobile] [--karoo] [--garmin] [--all]
# Default (no args): --all

set -euo pipefail
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MASTER_SVG="$REPO_ROOT/assets/icon.svg"

# --- Helpers -----------------------------------------------------------------

die() { echo "ERROR: $1" >&2; exit 1; }

# Render SVG to PNG at NxN using inkscape or rsvg-convert (whichever is available)
svg_to_png() {
  local size="$1" output="$2"
  if command -v inkscape >/dev/null 2>&1; then
    inkscape --export-type=png --export-width="$size" --export-height="$size" \
      --export-filename="$output" "$MASTER_SVG" --batch-process 2>/dev/null
  elif command -v rsvg-convert >/dev/null 2>&1; then
    rsvg-convert -w "$size" -h "$size" -o "$output" "$MASTER_SVG"
  else
    die "Neither inkscape nor rsvg-convert found. Install one: apt install inkscape OR apt install librsvg2-bin"
  fi
}

# --- Per-platform generators -------------------------------------------------

generate_web() {
  echo "==> Generating web icons..."
  command -v pnpm >/dev/null 2>&1 || die "pnpm not found"
  (cd "$REPO_ROOT/frontend" && pnpm generate-icons)
}

generate_mobile() {
  echo "==> Generating Flutter mobile icons..."
  command -v dart >/dev/null 2>&1 || die "dart not found"
  # flutter_launcher_icons does not support SVG — generate a 1024px PNG first
  local PNG="$REPO_ROOT/assets/icon-1024.png"
  echo "  Rasterizing icon-1024.png..."
  svg_to_png 1024 "$PNG"
  (cd "$REPO_ROOT/mobile" && dart run flutter_launcher_icons)
}

generate_karoo() {
  echo "==> Generating Karoo launcher icons..."
  local RES="$REPO_ROOT/karoo/app/src/main/res"
  local -A SIZES=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)
  for density in "${!SIZES[@]}"; do
    local size="${SIZES[$density]}"
    local out_dir="$RES/mipmap-$density"
    mkdir -p "$out_dir"
    svg_to_png "$size" "$out_dir/ic_launcher.png"
    echo "  mipmap-$density/ic_launcher.png (${size}x${size})"
  done
}

generate_garmin() {
  echo "==> Generating Garmin launcher icon..."
  command -v convert >/dev/null 2>&1 || die "ImageMagick 'convert' not found. Install: apt install imagemagick"
  local DRAWABLES="$REPO_ROOT/garmin-app/resources/drawables"
  local TMP
  TMP=$(mktemp /tmp/icon_garmin_XXXX.png)
  svg_to_png 60 "$TMP"
  # Strip alpha, convert to 16-bit RGB as Garmin Connect IQ requires
  convert "$TMP" -depth 16 -type TrueColor "$DRAWABLES/launcher_icon.png"
  rm "$TMP"
  echo "  launcher_icon.png (60x60, 16-bit RGB)"
}

# --- Argument parsing --------------------------------------------------------

RUN_WEB=false
RUN_MOBILE=false
RUN_KAROO=false
RUN_GARMIN=false

if [[ $# -eq 0 ]]; then
  RUN_WEB=true; RUN_MOBILE=true; RUN_KAROO=true; RUN_GARMIN=true
fi

for arg in "$@"; do
  case "$arg" in
    --web)    RUN_WEB=true ;;
    --mobile) RUN_MOBILE=true ;;
    --karoo)  RUN_KAROO=true ;;
    --garmin) RUN_GARMIN=true ;;
    --all)    RUN_WEB=true; RUN_MOBILE=true; RUN_KAROO=true; RUN_GARMIN=true ;;
    *) echo "Unknown argument: $arg" >&2; exit 1 ;;
  esac
done

[ ! -f "$MASTER_SVG" ] && die "Master SVG not found: $MASTER_SVG"

$RUN_WEB    && generate_web
$RUN_MOBILE && generate_mobile
$RUN_KAROO  && generate_karoo
$RUN_GARMIN && generate_garmin

echo ""
echo "Done. Don't forget to commit the generated files."
