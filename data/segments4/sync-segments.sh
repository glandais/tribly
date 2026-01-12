#!/bin/bash
#
# BRouter Segments Sync Script
# Automatically download and sync rd5 segment files from brouter.de
#
# Usage: ./sync-segments.sh [options]
#
# Environment variables:
#   SEGMENTS_DIR    - Target directory (default: ./segments4)
#   SEGMENTS_URL    - Source URL (default: https://brouter.de/brouter/segments4/)
#   SEGMENTS_FILTER - Regex to filter segments (default: .* for all)
#   USE_DELTA       - Enable delta updates (default: true)
#   BROUTER_JAR     - Path to BRouter JAR for delta application
#   DRY_RUN         - Show what would be done without downloading (default: false)
#   PARALLEL        - Number of concurrent downloads (default: 4)
#

set -euo pipefail

# Configuration with defaults
SEGMENTS_DIR="${SEGMENTS_DIR:-./segments4}"
SEGMENTS_URL="${SEGMENTS_URL:-https://brouter.de/brouter/segments4/}"
SEGMENTS_FILTER="${SEGMENTS_FILTER:-.*}"
USE_DELTA="${USE_DELTA:-true}"
BROUTER_JAR="${BROUTER_JAR:-}"
DRY_RUN="${DRY_RUN:-false}"
PARALLEL="${PARALLEL:-4}"

# Script directory for finding BRouter JAR
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Statistics
DOWNLOADED=0
SKIPPED=0
DELTA_APPLIED=0
FAILED=0
BYTES_DOWNLOADED=0

# Temporary files
TMP_DIR=""
LOCK_FILE=""

# Colors for output (disabled if not a terminal)
if [ -t 1 ]; then
    RED='\033[0;31m'
    GREEN='\033[0;32m'
    YELLOW='\033[0;33m'
    BLUE='\033[0;34m'
    NC='\033[0m' # No Color
else
    RED=''
    GREEN=''
    YELLOW=''
    BLUE=''
    NC=''
fi

usage() {
    cat <<EOF
BRouter Segments Sync Script

Usage: $(basename "$0") [OPTIONS]

Options:
  -h, --help          Show this help message
  -d, --dir DIR       Target directory for segments (default: ./segments4)
  -u, --url URL       Source URL (default: https://brouter.de/brouter/segments4/)
  -f, --filter REGEX  Filter segments by regex (e.g., 'E[0-9]+_N4[0-9]' for Europe)
  -j, --jar PATH      Path to BRouter JAR for delta updates
  -p, --parallel N    Number of concurrent downloads (default: 4)
  -n, --dry-run       Show what would be downloaded without downloading
  --no-delta          Disable delta updates

Environment variables:
  SEGMENTS_DIR, SEGMENTS_URL, SEGMENTS_FILTER, USE_DELTA, BROUTER_JAR, DRY_RUN, PARALLEL

Examples:
  # Sync all segments to default directory
  $(basename "$0")

  # Sync European segments only
  $(basename "$0") -f '[EW][0-9]+_N[34][0-9]'

  # Dry run to see what would be downloaded
  $(basename "$0") --dry-run

  # Sync to custom directory with 8 parallel downloads
  $(basename "$0") -d /var/brouter/segments4 -p 8

Cron example (daily at 4 AM):
  0 4 * * * /path/to/sync-segments.sh >> /var/log/brouter-sync.log 2>&1
EOF
}

log() {
    echo -e "[$(date '+%Y-%m-%d %H:%M:%S')] $*"
}

log_info() {
    log "${BLUE}INFO${NC}: $*"
}

log_success() {
    log "${GREEN}OK${NC}: $*"
}

log_warn() {
    log "${YELLOW}WARN${NC}: $*"
}

log_error() {
    log "${RED}ERROR${NC}: $*"
}

cleanup() {
    if [ -n "$TMP_DIR" ] && [ -d "$TMP_DIR" ]; then
        rm -rf "$TMP_DIR"
    fi
    if [ -n "$LOCK_FILE" ] && [ -f "$LOCK_FILE" ]; then
        rm -f "$LOCK_FILE"
    fi
}

check_dependencies() {
    local missing=()
    for cmd in curl grep awk sed; do
        if ! command -v "$cmd" >/dev/null 2>&1; then
            missing+=("$cmd")
        fi
    done
    if [ ${#missing[@]} -gt 0 ]; then
        log_error "Missing required commands: ${missing[*]}"
        exit 1
    fi

    # Check for md5sum (needed for delta updates)
    if [ "$USE_DELTA" = "true" ]; then
        if ! command -v md5sum >/dev/null 2>&1; then
            log_warn "md5sum not found, disabling delta updates"
            USE_DELTA="false"
        fi
    fi
}

find_brouter_jar() {
    if [ -n "$BROUTER_JAR" ] && [ -f "$BROUTER_JAR" ]; then
        return 0
    fi

    # Try to find in distribution structure
    local jar="$SCRIPT_DIR/../brouter.jar"
    if [ -f "$jar" ]; then
        BROUTER_JAR="$jar"
        return 0
    fi

    # Try to find in source checkout
    local pattern="$SCRIPT_DIR/../../../brouter-server/build/libs/brouter-*-all.jar"
    local found
    found=$(ls $pattern 2>/dev/null | sort --reverse --version-sort | head -n 1 || true)
    if [ -n "$found" ] && [ -f "$found" ]; then
        BROUTER_JAR="$found"
        return 0
    fi

    return 1
}

acquire_lock() {
    LOCK_FILE="$SEGMENTS_DIR/.sync-segments.lock"

    if [ -f "$LOCK_FILE" ]; then
        local pid
        pid=$(cat "$LOCK_FILE" 2>/dev/null || echo "")
        if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
            log_error "Another sync is already running (PID: $pid)"
            exit 1
        fi
        log_warn "Stale lock file found, removing"
        rm -f "$LOCK_FILE"
    fi

    echo $$ > "$LOCK_FILE"
}

# Parse directory listing HTML from brouter.de
# Format: <a href="E0_N10.rd5">E0_N10.rd5</a> 17-Oct-2021 01:03 9648604
parse_directory_listing() {
    local url="$1"

    curl -sS "$url" | \
        grep -oP '<a href="[EWew][0-9]+_[NSns][0-9]+\.rd5">[^<]+</a>\s+[0-9]{2}-[A-Za-z]{3}-[0-9]{4}\s+[0-9]{2}:[0-9]{2}\s+[0-9]+' | \
        sed -E 's/<a href="([^"]+)">[^<]+<\/a>\s+([0-9]{2}-[A-Za-z]{3}-[0-9]{4})\s+([0-9]{2}:[0-9]{2})\s+([0-9]+)/\1 \2 \3 \4/' | \
        while read -r filename date time size; do
            # Apply filter
            local basename="${filename%.rd5}"
            if echo "$basename" | grep -qE "$SEGMENTS_FILTER"; then
                echo "$filename $size $date $time"
            fi
        done
}

# Calculate MD5 hash of a file
calc_md5() {
    local file="$1"
    md5sum "$file" | awk '{print $1}'
}

# Check if delta file exists on server
check_delta_exists() {
    local segment="$1"
    local md5="$2"
    local delta_url="${SEGMENTS_URL}diff/${segment}/${md5}.df5"

    local http_code
    http_code=$(curl -sS -o /dev/null -w '%{http_code}' --head "$delta_url" 2>/dev/null || echo "000")

    [ "$http_code" = "200" ]
}

# Download and apply delta update
apply_delta() {
    local segment="$1"
    local local_file="$2"
    local md5="$3"
    local tmp_dir="$4"

    local delta_url="${SEGMENTS_URL}diff/${segment}/${md5}.df5"
    local delta_file="$tmp_dir/${segment}.df5"
    local new_file="$tmp_dir/${segment}.rd5"

    # Download delta
    if ! curl -sS -o "$delta_file" "$delta_url"; then
        return 1
    fi

    # Check delta is not empty
    if [ ! -s "$delta_file" ]; then
        rm -f "$delta_file"
        return 1
    fi

    # Apply delta using BRouter's Rd5DiffTool
    if ! java -cp "$BROUTER_JAR" btools.mapaccess.Rd5DiffTool "$local_file" "$delta_file" "$new_file" 2>/dev/null; then
        rm -f "$delta_file" "$new_file"
        return 1
    fi

    # Atomically replace the old file
    mv "$new_file" "$local_file"
    rm -f "$delta_file"

    return 0
}

# Download a single segment
download_segment() {
    local filename="$1"
    local remote_size="$2"
    local remote_date="$3"
    local remote_time="$4"
    local local_file="$SEGMENTS_DIR/$filename"
    local segment="${filename%.rd5}"
    local tmp_file="$TMP_DIR/${filename}.tmp"

    # Check if local file exists
    if [ -f "$local_file" ]; then
        local local_size
        local_size=$(stat -c %s "$local_file" 2>/dev/null || stat -f %z "$local_file" 2>/dev/null || echo 0)

        # If sizes match, skip (file hasn't changed)
        if [ "$local_size" = "$remote_size" ]; then
            log_info "Skipping $filename (unchanged)"
            ((SKIPPED++)) || true
            return 0
        fi

        # Try delta update if enabled and file is large enough
        if [ "$USE_DELTA" = "true" ] && [ -n "$BROUTER_JAR" ] && [ "$local_size" -ge 1048576 ]; then
            local md5
            md5=$(calc_md5 "$local_file")

            if check_delta_exists "$segment" "$md5"; then
                if [ "$DRY_RUN" = "true" ]; then
                    log_info "[DRY RUN] Would apply delta for $filename"
                    return 0
                fi

                log_info "Applying delta update for $filename"
                if apply_delta "$segment" "$local_file" "$md5" "$TMP_DIR"; then
                    log_success "Delta applied: $filename"
                    ((DELTA_APPLIED++)) || true
                    return 0
                else
                    log_warn "Delta failed for $filename, falling back to full download"
                fi
            fi
        fi
    fi

    # Full download
    if [ "$DRY_RUN" = "true" ]; then
        log_info "[DRY RUN] Would download $filename ($remote_size bytes)"
        return 0
    fi

    log_info "Downloading $filename ($remote_size bytes)"

    local url="${SEGMENTS_URL}${filename}"

    if curl -sS -o "$tmp_file" "$url"; then
        # Verify size
        local downloaded_size
        downloaded_size=$(stat -c %s "$tmp_file" 2>/dev/null || stat -f %z "$tmp_file" 2>/dev/null || echo 0)

        if [ "$downloaded_size" = "$remote_size" ]; then
            mv "$tmp_file" "$local_file"
            log_success "Downloaded: $filename"
            ((DOWNLOADED++)) || true
            ((BYTES_DOWNLOADED+=remote_size)) || true
            return 0
        else
            log_error "Size mismatch for $filename (expected $remote_size, got $downloaded_size)"
            rm -f "$tmp_file"
            ((FAILED++)) || true
            return 1
        fi
    else
        log_error "Failed to download $filename"
        rm -f "$tmp_file"
        ((FAILED++)) || true
        return 1
    fi
}

# Process downloads with parallelism
process_downloads() {
    local listing_file="$1"
    local total
    total=$(wc -l < "$listing_file")
    local current=0
    local pids=()

    log_info "Processing $total segments with $PARALLEL parallel downloads"

    while IFS=' ' read -r filename size date time; do
        current=$((current + 1))

        # Wait if we have too many parallel jobs
        while [ ${#pids[@]} -ge "$PARALLEL" ]; do
            local new_pids=()
            for pid in "${pids[@]}"; do
                if kill -0 "$pid" 2>/dev/null; then
                    new_pids+=("$pid")
                fi
            done
            pids=("${new_pids[@]}")
            if [ ${#pids[@]} -ge "$PARALLEL" ]; then
                sleep 0.5
            fi
        done

        # Start download in background
        (download_segment "$filename" "$size" "$date" "$time") &
        pids+=($!)

    done < "$listing_file"

    # Wait for remaining downloads
    for pid in "${pids[@]}"; do
        wait "$pid" 2>/dev/null || true
    done
}

# Format bytes to human readable
format_bytes() {
    local bytes=$1
    if [ "$bytes" -ge 1073741824 ]; then
        echo "$(awk "BEGIN {printf \"%.2f\", $bytes/1073741824}") GB"
    elif [ "$bytes" -ge 1048576 ]; then
        echo "$(awk "BEGIN {printf \"%.2f\", $bytes/1048576}") MB"
    elif [ "$bytes" -ge 1024 ]; then
        echo "$(awk "BEGIN {printf \"%.2f\", $bytes/1024}") KB"
    else
        echo "$bytes bytes"
    fi
}

main() {
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                usage
                exit 0
                ;;
            -d|--dir)
                SEGMENTS_DIR="$2"
                shift 2
                ;;
            -u|--url)
                SEGMENTS_URL="$2"
                shift 2
                ;;
            -f|--filter)
                SEGMENTS_FILTER="$2"
                shift 2
                ;;
            -j|--jar)
                BROUTER_JAR="$2"
                shift 2
                ;;
            -p|--parallel)
                PARALLEL="$2"
                shift 2
                ;;
            -n|--dry-run)
                DRY_RUN="true"
                shift
                ;;
            --no-delta)
                USE_DELTA="false"
                shift
                ;;
            *)
                log_error "Unknown option: $1"
                usage
                exit 1
                ;;
        esac
    done

    # Ensure URL ends with /
    [[ "$SEGMENTS_URL" != */ ]] && SEGMENTS_URL="${SEGMENTS_URL}/"

    log_info "BRouter Segments Sync"
    log_info "Target: $SEGMENTS_DIR"
    log_info "Source: $SEGMENTS_URL"
    [ "$SEGMENTS_FILTER" != ".*" ] && log_info "Filter: $SEGMENTS_FILTER"
    [ "$DRY_RUN" = "true" ] && log_warn "DRY RUN MODE - No changes will be made"

    # Check dependencies
    check_dependencies

    # Find BRouter JAR for delta updates
    if [ "$USE_DELTA" = "true" ]; then
        if find_brouter_jar; then
            log_info "Delta updates enabled (using $BROUTER_JAR)"
        else
            log_warn "BRouter JAR not found, delta updates disabled"
            USE_DELTA="false"
        fi
    fi

    # Create target directory if needed
    if [ ! -d "$SEGMENTS_DIR" ]; then
        if [ "$DRY_RUN" = "true" ]; then
            log_info "[DRY RUN] Would create directory $SEGMENTS_DIR"
        else
            mkdir -p "$SEGMENTS_DIR"
            log_info "Created directory $SEGMENTS_DIR"
        fi
    fi

    # Setup cleanup trap
    trap cleanup EXIT INT TERM

    # Acquire lock
    if [ "$DRY_RUN" != "true" ]; then
        acquire_lock
    fi

    # Create temporary directory
    TMP_DIR=$(mktemp -d)

    # Fetch and parse directory listing
    log_info "Fetching segment listing from $SEGMENTS_URL"
    local listing_file="$TMP_DIR/listing.txt"

    if ! parse_directory_listing "$SEGMENTS_URL" > "$listing_file"; then
        log_error "Failed to fetch directory listing"
        exit 1
    fi

    local segment_count
    segment_count=$(wc -l < "$listing_file")

    if [ "$segment_count" -eq 0 ]; then
        log_warn "No segments found matching filter '$SEGMENTS_FILTER'"
        exit 0
    fi

    log_info "Found $segment_count segments matching filter"

    # Process downloads
    process_downloads "$listing_file"

    # Print summary
    echo ""
    log_info "=== Sync Summary ==="
    log_info "Downloaded: $DOWNLOADED"
    log_info "Delta applied: $DELTA_APPLIED"
    log_info "Skipped (unchanged): $SKIPPED"
    log_info "Failed: $FAILED"
    if [ "$BYTES_DOWNLOADED" -gt 0 ]; then
        log_info "Total downloaded: $(format_bytes $BYTES_DOWNLOADED)"
    fi

    if [ "$FAILED" -gt 0 ]; then
        exit 1
    fi
}

main "$@"
