import type { QueryClient } from '@tanstack/react-query'
import type { RouterProvider } from 'react-router-dom'

type Router = Parameters<typeof RouterProvider>[0]['router']

// Different components mount (and start fetching) across separate React commits/effects rather
// than all at once, so queryClient.isFetching() can dip to 0 for a moment between two waves of
// fetches on the same page — e.g. an already-covered query finishing its own background revalidation
// right before a later effect kicks off the actual missing fetch. Closing the window immediately on
// that dip would miss it, so closing is debounced: only commit to "settled" after this many ms with
// no fetch activity at all.
const SETTLE_DEBOUNCE_MS = 300

/**
 * Logs queries that get fetched on the client but weren't already in the cache when the page
 * "arrived" — either from the SSR-dehydrated state (first load) or from the route's `prefetch()`
 * (client-side navigation). Those are gaps in a route's `prefetch()` declaration in
 * routes.config.ts: the data is fetched anyway, just after the first paint instead of before it.
 *
 * The watch window reopens on every navigation.state -> 'idle' transition (prefetch() just
 * resolved) and closes once queryClient.isFetching() has stayed at 0 for SETTLE_DEBOUNCE_MS (the
 * page has settled) — so fetches from later user interaction (pagination, opening a modal) aren't
 * flagged unless they themselves trigger a router navigation.
 */
export function installPrefetchAudit(queryClient: QueryClient, router: Router): void {
  let coveredHashes = new Set(
    queryClient
      .getQueryCache()
      .getAll()
      .map((q) => q.queryHash)
  )
  let windowOpen = true
  let closeTimer: ReturnType<typeof setTimeout> | undefined

  function scheduleSettleCheck() {
    if (!windowOpen) return
    if (queryClient.isFetching() === 0) {
      closeTimer ??= setTimeout(() => {
        windowOpen = false
        closeTimer = undefined
      }, SETTLE_DEBOUNCE_MS)
    } else if (closeTimer) {
      clearTimeout(closeTimer)
      closeTimer = undefined
    }
  }

  queryClient.getQueryCache().subscribe((event) => {
    if (windowOpen && event.type === 'updated' && event.action.type === 'fetch') {
      const hash = event.query.queryHash
      if (!coveredHashes.has(hash)) {
        coveredHashes.add(hash)
        console.warn(
          `[prefetch-audit] "${router.state.location.pathname}": query fetched after page load, ` +
            `not covered by route prefetch ${JSON.stringify(event.query.queryKey)}`
        )
      }
    }
    scheduleSettleCheck()
  })

  router.subscribe((state) => {
    if (state.navigation.state === 'idle') {
      if (closeTimer) {
        clearTimeout(closeTimer)
        closeTimer = undefined
      }
      coveredHashes = new Set(
        queryClient
          .getQueryCache()
          .getAll()
          .map((q) => q.queryHash)
      )
      windowOpen = true
      scheduleSettleCheck()
    }
  })

  // Also cover the very first page load: no navigation event fires for it (the router starts
  // already "idle" via hydrationData), so nothing would otherwise arm the close timer if the page
  // turns out to have zero post-load fetches.
  scheduleSettleCheck()
}
