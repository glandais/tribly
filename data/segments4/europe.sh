#!/bin/bash
#
# Download/update all European BRouter segments
#
# Europe coverage:
#   Longitude: W30 to E60 (Iceland to Western Russia)
#   Latitude:  N35 to N75 (Mediterranean to Arctic)
#
# Usage: ./europe.sh [options]
#   Options are passed through to sync-segments.sh
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# European segments filter:
# - Longitude: W[0-3][0-9] or E[0-5][0-9] (W30 to E60)
# - Latitude: N[3-7][0-9] (N35 to N75)
EUROPE_FILTER='([EW][0-5]?[0-9]_N[3-7][0-9])'

# Find brouter.jar (check common locations)
BROUTER_JAR=""
if [ -f "$SCRIPT_DIR/../brouter.jar" ]; then
    BROUTER_JAR="$SCRIPT_DIR/../brouter.jar"
elif [ -f "$SCRIPT_DIR/brouter.jar" ]; then
    BROUTER_JAR="$SCRIPT_DIR/brouter.jar"
fi

# Build arguments
ARGS=(-d "$SCRIPT_DIR" -f "$EUROPE_FILTER")

if [ -n "$BROUTER_JAR" ]; then
    ARGS+=(-j "$BROUTER_JAR")
fi

# Pass through any additional arguments
ARGS+=("$@")

exec "$SCRIPT_DIR/sync-segments.sh" "${ARGS[@]}"
