#!/usr/bin/env bash
# Retention for the snapshot store — runs ON THE BACKUP HOST, from root's crontab.
#
#   backup-prune.sh <snapshot root> [keep]
#
# It cannot live in backup.sh: the production host pushes through a key restricted to
# `command="rrsync <root>"`, which accepts an rsync transfer and nothing else. That is deliberate —
# a compromised production host must not be able to delete its own history — and the price is that
# expiry is decided here, by the machine that owns the disk.
#
# Keeps the `keep` most recent COMPLETE snapshots. Snapshots without a COMPLETE marker are failed
# runs; they are dropped once they are older than a day, never immediately (a run in flight has no
# marker yet).
#
# Retention is by count, not by age: were backups to stop running, an age rule would end up erasing
# the very snapshots it was meant to protect.
set -euo pipefail

ROOT="${1:?usage: $0 <snapshot root> [keep]}"
KEEP="${2:-30}"

[[ -d "$ROOT" ]] || { echo "no such directory: $ROOT" >&2; exit 1; }
cd "$ROOT"

log() { printf '%s  %s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*"; }

mapfile -t all < <(find . -maxdepth 1 -mindepth 1 -type d -name '20*' -printf '%f\n' | sort)
complete=()
incomplete=()
for snap in "${all[@]}"; do
  if [[ -f "$snap/COMPLETE" ]]; then complete+=("$snap"); else incomplete+=("$snap"); fi
done

log "$ROOT: ${#complete[@]} complete, ${#incomplete[@]} incomplete, keeping $KEEP"

# Failed runs, given a day of grace so a backup still transferring is never touched.
for snap in "${incomplete[@]}"; do
  if [[ -z "$(find "$snap" -maxdepth 0 -mtime +1)" ]]; then
    log "  keeping incomplete $snap (less than a day old — may still be running)"
    continue
  fi
  log "  removing incomplete $snap"
  rm -rf -- "$snap"
done

# Oldest complete snapshots beyond the retention count.
if ((${#complete[@]} > KEEP)); then
  for snap in "${complete[@]:0:${#complete[@]} - KEEP}"; do
    log "  removing $snap"
    rm -rf -- "$snap"
  done
fi

log "done - $(find . -maxdepth 1 -mindepth 1 -type d -name '20*' | wc -l) snapshots, $(du -sh "$ROOT" | cut -f1) on disk"
