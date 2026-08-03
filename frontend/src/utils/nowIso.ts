/**
 * `now`, rounded down to the hour, as an ISO string.
 *
 * SSR prefetch and client hydration compute "now" a few hundred ms to a few seconds apart — using
 * the raw timestamp in a query key (e.g. `from`) would never byte-match between the two. Rounding
 * to the hour makes them agree as long as both happen within the same hour, without needing to
 * thread a single shared value across the server/client boundary.
 */
export function hourAlignedNowIso(): string {
  const now = new Date()
  now.setMinutes(0, 0, 0)
  return now.toISOString()
}
