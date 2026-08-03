/**
 * `now`, rounded down to the hour.
 *
 * SSR prefetch and client hydration compute "now" a few hundred ms to a few seconds apart — using
 * the raw timestamp in a query key (e.g. `from`) would never byte-match between the two. Rounding
 * to the hour makes them agree as long as both happen within the same hour, without needing to
 * thread a single shared value across the server/client boundary.
 *
 * The rounding is **UTC** (`setUTCMinutes`), not local: the server process and the visitor's
 * browser rarely share a timezone, and rounding to the local hour yields two different instants on
 * any offset that isn't a whole hour (IST, ACST…) — a permanent cache miss for those visitors,
 * where UTC rounding only ever misses on the hour boundary itself.
 */
export function hourAlignedNow(): Date {
  const now = new Date()
  now.setUTCMinutes(0, 0, 0)
  return now
}

export function hourAlignedNowIso(): string {
  return hourAlignedNow().toISOString()
}
