#!/bin/bash
#
# Migration script: Local filesystem storage → MinIO (S3)
#
# This script migrates existing assets and avatars from local filesystem
# to MinIO/S3 storage. Run this ONCE after deploying the S3 storage changes.
#
# Prerequisites:
#   - MinIO client (mc) installed: https://min.io/docs/minio/linux/reference/minio-mc.html
#   - MinIO server running and accessible
#   - Docker compose services running (for dev) or production MinIO accessible
#
# Usage:
#   ./scripts/migrate-storage-to-minio.sh [--dry-run] [--prod]
#
# Options:
#   --dry-run   Show what would be migrated without actually copying
#   --prod      Use production settings (requires MINIO_* env vars)
#

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Parse arguments
DRY_RUN=false
PROD=false
for arg in "$@"; do
    case $arg in
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        --prod)
            PROD=true
            shift
            ;;
    esac
done

# Configuration
if [ "$PROD" = true ]; then
    # Production settings - require environment variables
    MINIO_ENDPOINT="${MINIO_ENDPOINT:?MINIO_ENDPOINT required for production}"
    MINIO_ACCESS_KEY="${MINIO_ACCESS_KEY:?MINIO_ACCESS_KEY required for production}"
    MINIO_SECRET_KEY="${MINIO_SECRET_KEY:?MINIO_SECRET_KEY required for production}"
    LOCAL_STORAGE_PATH="${STORAGE_PATH:-./data/storage}"
    BUCKET_NAME="${STORAGE_BUCKET:-tribly}"
else
    # Development settings
    MINIO_ENDPOINT="http://localhost:9000"
    MINIO_ACCESS_KEY="tribly"
    MINIO_SECRET_KEY="tribly_secret"
    LOCAL_STORAGE_PATH="./data/storage"
    BUCKET_NAME="tribly"
fi

ALIAS_NAME="tribly-migrate"

echo -e "${GREEN}=== Tribly Storage Migration ===${NC}"
echo ""
echo "Configuration:"
echo "  MinIO Endpoint: $MINIO_ENDPOINT"
echo "  Local Storage:  $LOCAL_STORAGE_PATH"
echo "  Bucket:         $BUCKET_NAME"
echo "  Dry Run:        $DRY_RUN"
echo ""

# Check if mc is installed
if ! command -v mc &> /dev/null; then
    echo -e "${RED}Error: MinIO client (mc) is not installed${NC}"
    echo "Install it from: https://min.io/docs/minio/linux/reference/minio-mc.html"
    echo ""
    echo "Quick install:"
    echo "  curl https://dl.min.io/client/mc/release/linux-amd64/mc -o mc"
    echo "  chmod +x mc"
    echo "  sudo mv mc /usr/local/bin/"
    exit 1
fi

# Check if local storage exists
if [ ! -d "$LOCAL_STORAGE_PATH" ]; then
    echo -e "${YELLOW}Warning: Local storage directory not found: $LOCAL_STORAGE_PATH${NC}"
    echo "Nothing to migrate."
    exit 0
fi

# Configure MinIO client alias
echo -e "${GREEN}Step 1: Configuring MinIO client...${NC}"
mc alias set "$ALIAS_NAME" "$MINIO_ENDPOINT" "$MINIO_ACCESS_KEY" "$MINIO_SECRET_KEY" --api S3v4

# Test connection
if ! mc admin info "$ALIAS_NAME" &> /dev/null; then
    echo -e "${RED}Error: Cannot connect to MinIO at $MINIO_ENDPOINT${NC}"
    echo "Make sure MinIO is running and credentials are correct."
    exit 1
fi
echo "Connected to MinIO successfully."

# Create bucket if it doesn't exist
echo ""
echo -e "${GREEN}Step 2: Ensuring bucket exists...${NC}"
if mc ls "$ALIAS_NAME/$BUCKET_NAME" &> /dev/null; then
    echo "Bucket '$BUCKET_NAME' already exists."
else
    if [ "$DRY_RUN" = true ]; then
        echo "[DRY RUN] Would create bucket: $BUCKET_NAME"
    else
        mc mb "$ALIAS_NAME/$BUCKET_NAME"
        echo "Created bucket: $BUCKET_NAME"
    fi
fi

# Count files to migrate
echo ""
echo -e "${GREEN}Step 3: Analyzing local storage...${NC}"

AVATARS_PATH="$LOCAL_STORAGE_PATH/avatars"
ASSETS_COUNT=0
AVATARS_COUNT=0

# Count asset files (everything except avatars directory)
if [ -d "$LOCAL_STORAGE_PATH" ]; then
    ASSETS_COUNT=$(find "$LOCAL_STORAGE_PATH" -type f ! -path "$AVATARS_PATH/*" 2>/dev/null | wc -l)
fi

# Count avatar files
if [ -d "$AVATARS_PATH" ]; then
    AVATARS_COUNT=$(find "$AVATARS_PATH" -type f 2>/dev/null | wc -l)
fi

echo "Found:"
echo "  - $ASSETS_COUNT asset files"
echo "  - $AVATARS_COUNT avatar files"
echo "  - $((ASSETS_COUNT + AVATARS_COUNT)) total files"

if [ $((ASSETS_COUNT + AVATARS_COUNT)) -eq 0 ]; then
    echo ""
    echo -e "${YELLOW}No files to migrate.${NC}"
    mc alias rm "$ALIAS_NAME"
    exit 0
fi

# Migrate assets (team files)
echo ""
echo -e "${GREEN}Step 4: Migrating assets...${NC}"

# Find all team directories (TSIDs, not 'avatars')
for team_dir in "$LOCAL_STORAGE_PATH"/*/; do
    team_name=$(basename "$team_dir")

    # Skip avatars directory
    if [ "$team_name" = "avatars" ]; then
        continue
    fi

    # Skip if not a directory
    if [ ! -d "$team_dir" ]; then
        continue
    fi

    SOURCE="$team_dir"
    DEST="$ALIAS_NAME/$BUCKET_NAME/assets/$team_name/"

    echo "  Migrating team: $team_name"

    if [ "$DRY_RUN" = true ]; then
        echo "    [DRY RUN] mc mirror $SOURCE $DEST"
        mc mirror --dry-run "$SOURCE" "$DEST" 2>/dev/null || true
    else
        mc mirror --overwrite "$SOURCE" "$DEST"
    fi
done

# Migrate avatars
echo ""
echo -e "${GREEN}Step 5: Migrating avatars...${NC}"

if [ -d "$AVATARS_PATH" ]; then
    SOURCE="$AVATARS_PATH/"
    DEST="$ALIAS_NAME/$BUCKET_NAME/avatars/"

    if [ "$DRY_RUN" = true ]; then
        echo "  [DRY RUN] mc mirror $SOURCE $DEST"
        mc mirror --dry-run "$SOURCE" "$DEST" 2>/dev/null || true
    else
        mc mirror --overwrite "$SOURCE" "$DEST"
    fi
else
    echo "  No avatars directory found, skipping."
fi

# Verify migration
echo ""
echo -e "${GREEN}Step 6: Verifying migration...${NC}"

if [ "$DRY_RUN" = false ]; then
    S3_ASSETS_COUNT=$(mc ls --recursive "$ALIAS_NAME/$BUCKET_NAME/assets/" 2>/dev/null | wc -l)
    S3_AVATARS_COUNT=$(mc ls --recursive "$ALIAS_NAME/$BUCKET_NAME/avatars/" 2>/dev/null | wc -l)
    S3_TOTAL=$((S3_ASSETS_COUNT + S3_AVATARS_COUNT))

    echo "Files in MinIO:"
    echo "  - $S3_ASSETS_COUNT asset files"
    echo "  - $S3_AVATARS_COUNT avatar files"
    echo "  - $S3_TOTAL total files"

    LOCAL_TOTAL=$((ASSETS_COUNT + AVATARS_COUNT))

    if [ "$S3_TOTAL" -eq "$LOCAL_TOTAL" ]; then
        echo ""
        echo -e "${GREEN}Migration successful! All $LOCAL_TOTAL files migrated.${NC}"
    else
        echo ""
        echo -e "${YELLOW}Warning: File count mismatch.${NC}"
        echo "  Local:  $LOCAL_TOTAL files"
        echo "  MinIO:  $S3_TOTAL files"
        echo "Please verify manually."
    fi
else
    echo "[DRY RUN] Skipping verification."
fi

# Cleanup
echo ""
echo -e "${GREEN}Step 7: Cleanup...${NC}"
mc alias rm "$ALIAS_NAME"
echo "Removed temporary MinIO alias."

echo ""
echo -e "${GREEN}=== Migration Complete ===${NC}"
echo ""
echo "Next steps:"
echo "  1. Test the application to ensure assets load correctly from MinIO"
echo "  2. Once verified, you can optionally remove the local storage:"
echo "     rm -rf $LOCAL_STORAGE_PATH"
echo "  3. Remove storage.path from your configuration if not needed for rollback"
echo ""
