#!/usr/bin/env bash
# generate-icons.sh — Generate all platform icons from assets/icon.svg
#
# Prerequisites:
#   - One of: rsvg-convert (apt install librsvg2-bin / brew install librsvg),
#     inkscape (>= 1.0), or magick (brew install imagemagick)
#   - imagemagick (for Karoo/Garmin: apt install imagemagick / brew install imagemagick)
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

# Render SVG to PNG at NxN using the first available tool
svg_to_png() {
  local size="$1" output="$2"
  if command -v rsvg-convert >/dev/null 2>&1; then
    rsvg-convert -w "$size" -h "$size" -o "$output" "$MASTER_SVG"
  elif command -v inkscape >/dev/null 2>&1; then
    inkscape "$MASTER_SVG" -w "$size" -h "$size" -o "$output"
  elif command -v magick >/dev/null 2>&1; then
    magick -background none "$MASTER_SVG" -resize "${size}x${size}" "$output"
  else
    die "No SVG renderer found. Install one of: rsvg-convert, inkscape, or imagemagick (magick)"
  fi
}

# --- Per-platform generators -------------------------------------------------

generate_web() {
  echo "==> Generating web icons..."
  command -v pnpm >/dev/null 2>&1 || die "pnpm not found"
  # pwa-assets-generator outputs files next to the source image, so the source must be in public/
  cp "$MASTER_SVG" "$REPO_ROOT/frontend/public/icon.svg"
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

  # Generate iOS launch images from the 1024px PNG
  echo "  Generating iOS launch images..."
  local LAUNCH_DIR="$REPO_ROOT/mobile/ios/Runner/Assets.xcassets/LaunchImage.imageset"
  magick "$PNG" -resize 168x168 "$LAUNCH_DIR/LaunchImage.png"
  magick "$PNG" -resize 336x336 "$LAUNCH_DIR/LaunchImage@2x.png"
  magick "$PNG" -resize 504x504 "$LAUNCH_DIR/LaunchImage@3x.png"
  echo "  LaunchImage.png (168/336/504)"
}

generate_karoo() {
  echo "==> Generating Karoo launcher icons..."
  local RES="$REPO_ROOT/karoo/app/src/main/res"
  for entry in mdpi:48 hdpi:72 xhdpi:96 xxhdpi:144 xxxhdpi:192; do
    local density="${entry%%:*}"
    local size="${entry##*:}"
    local out_dir="$RES/mipmap-$density"
    mkdir -p "$out_dir"
    local TMP_KAROO
    TMP_KAROO=$(mktemp /tmp/icon_karoo_XXXX.png)
    svg_to_png "$size" "$TMP_KAROO"
    magick "$TMP_KAROO" -background "#228be6" -alpha remove -alpha off "$out_dir/ic_launcher.png"
    rm "$TMP_KAROO"
    echo "  mipmap-$density/ic_launcher.png (${size}x${size})"
  done
}

generate_garmin() {
  echo "==> Generating Garmin launcher icon..."
  command -v magick >/dev/null 2>&1 || die "ImageMagick 'magick' not found. Install: brew install imagemagick / apt install imagemagick"
  local DRAWABLES="$REPO_ROOT/garmin-app/resources/drawables"
  local TMP
  TMP=$(mktemp /tmp/icon_garmin_XXXX.png)
  svg_to_png 60 "$TMP"
  # Flatten alpha onto brand background color, convert to 16-bit RGB as Garmin Connect IQ requires
  magick "$TMP" -background "#228be6" -alpha remove -alpha off -depth 16 -type TrueColor "$DRAWABLES/launcher_icon.png"
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
