#!/usr/bin/env bash
# Turns the production host's last push into a snapshot it can no longer touch — runs ON THE BACKUP
# HOST, from root's crontab, every quarter of an hour.
#
#   backup-promote.sh <backup root>
#
# The production host only ever writes to <root>/incoming/, a mirror it updates in place
# (scripts/backup.sh). Its key is `rrsync <root>` and unix permissions do the rest: <root> and every
# dated snapshot in it belong to root, group-readable by the backup account — which is how a restore
# still reads them through the same key — and writable by nobody else (docs/LEDGER_*.md SEC-32). A
# compromised production host can wreck incoming/, and poison the snapshots promoted from it from
# then on, but not delete, rename or rewrite one snapshot already taken.
#
# A push is promoted once its COMPLETE marker names the snapshot its MANIFEST describes — backup.sh
# removes the marker before it pushes and writes it last. The copy is hard-linked against the
# previous snapshot (unchanged MinIO objects cost no space), owned root:<group of incoming/> and
# read-only, checked against SHA256SUMS, then renamed into place: a dated directory is either whole
# or absent.
#
# At most one promotion per MIN_INTERVAL_HOURS (default 20): the snapshot name comes from the
# production host, and retention keeps a count (backup-prune.sh), so without a floor a compromised
# host could push thirty snapshots in a night and have prune expire every real one. A name must also
# be later than the last snapshot and not in the future. A manual extra run is promoted at the next
# window, or at once with MIN_INTERVAL_HOURS=0.
#
# Reports to BACKUP_PROMOTE_PING_URL (Healthchecks) when set in /etc/default/pedalons-backup-promote:
# a ping per promotion, /fail on an error. Nothing to promote is not an event.
set -euo pipefail

ROOT="${1:?usage: $0 <backup root>}"
ROOT="${ROOT%/}"
INCOMING="$ROOT/incoming"

[[ -f /etc/default/pedalons-backup-promote ]] && . /etc/default/pedalons-backup-promote
MIN_INTERVAL_HOURS="${MIN_INTERVAL_HOURS:-20}"

log() { printf '%s  %s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "$*"; }
ping_hc() {
  [[ -n "${BACKUP_PROMOTE_PING_URL:-}" ]] || return 0
  curl -fsS -m 15 --retry 3 --data-raw "$2" "${BACKUP_PROMOTE_PING_URL}$1" >/dev/null 2>&1 || true
}
die() {
  log "ERROR: $*" >&2
  ping_hc /fail "$*"
  exit 1
}

exec 9>/run/lock/pedalons-backup-promote.lock
flock -n 9 || exit 0

[[ -d "$INCOMING" ]] || exit 0
[[ -f "$INCOMING/COMPLETE" && -f "$INCOMING/MANIFEST" ]] || exit 0

# Everything read from incoming/ was written by the production host: the name is checked against
# the one shape backup.sh produces before it becomes a path.
STAMP="$(sed -n 's/^snapshot=//p' "$INCOMING/MANIFEST" | head -n1)"
[[ "$STAMP" =~ ^20[0-9]{2}-[01][0-9]-[0-3][0-9]T[0-2][0-9][0-5][0-9][0-5][0-9]Z$ ]] \
  || die "incoming/MANIFEST names no valid snapshot: '${STAMP:0:40}'"
# A marker left by a previous run, or a push in flight: not promotable yet.
[[ "$(head -n1 "$INCOMING/COMPLETE")" == "$STAMP" ]] || exit 0
[[ -e "$ROOT/$STAMP" ]] && exit 0

PREV="$(find "$ROOT" -maxdepth 1 -mindepth 1 -type d -name '20*' -printf '%f\n' | sort | tail -n1)"
NOW="$(date -u +%Y-%m-%dT%H%M%SZ)"
if [[ -n "$PREV" ]]; then
  [[ "$STAMP" > "$PREV" ]] || die "incoming snapshot $STAMP is not later than the last one, $PREV"
  # The age of the last snapshot is its directory's, set here by root: not something the production
  # host can forge.
  if [[ -n "$(find "$ROOT/$PREV" -maxdepth 0 -mmin -$((MIN_INTERVAL_HOURS * 60)))" ]]; then
    log "$STAMP waits: $PREV was promoted less than ${MIN_INTERVAL_HOURS}h ago"
    exit 0
  fi
fi
# The stamp is UTC, like NOW; an hour of slack for clock drift between the two hosts.
[[ "$STAMP" < "$(date -u -d '+1 hour' +%Y-%m-%dT%H%M%SZ)" ]] || die "incoming snapshot $STAMP is in the future (now $NOW)"

GROUP="$(stat -c %G "$INCOMING")"
WORK="$ROOT/.promoting-$STAMP"
rm -rf -- "$ROOT"/.promoting-*

log "promoting $STAMP${PREV:+ (hard-linked against $PREV)}"
link=()
[[ -n "$PREV" ]] && link=(--link-dest="$ROOT/$PREV")
rsync -a --numeric-ids --chown="root:$GROUP" --chmod=D0750,F0440 "${link[@]}" \
  "$INCOMING/" "$WORK/" || die "rsync of incoming/ into $WORK failed"

(cd "$WORK" && sha256sum --quiet -c SHA256SUMS) || {
  rm -rf -- "$WORK"
  die "$STAMP fails its SHA256SUMS: not promoted"
}

chown "root:$GROUP" "$WORK"
chmod 0750 "$WORK"
mv -T -- "$WORK" "$ROOT/$STAMP"
touch "$ROOT/$STAMP"

log "promoted $STAMP ($(du -sh --apparent-size "$ROOT/$STAMP/minio" 2>/dev/null | cut -f1) of objects)"
ping_hc "" "promoted $STAMP"
