import type { QueryClient } from '@tanstack/react-query'
import type { RouterProvider } from 'react-router-dom'

type Router = Parameters<typeof RouterProvider>[0]['router']

// RouteGenerator.tsx stamps routes.config.ts's own `id` onto `handle.routeId` — the RouteObject's
// own `id` can't be reused for this: React Router requires it unique per object, and each locale
// variant of a route is a separate object sharing one config id.
function currentRouteName(router: Router): string {
  const routeId = (router.state.matches.at(-1)?.route.handle as { routeId?: string } | undefined)
    ?.routeId
  return routeId ?? router.state.location.pathname
}

// Different components mount (and start fetching) across separate React commits/effects rather
// than all at once, so queryClient.isFetching() can dip to 0 for a moment between two waves of
// fetches on the same page — e.g. an already-covered query finishing its own background revalidation
// right before a later effect kicks off the actual missing fetch. Closing the window immediately on
// that dip would miss it, so closing is debounced: only commit to "settled" after this many ms with
// no fetch activity at all.
const SETTLE_DEBOUNCE_MS = 300

/**
 * Logs, in a single console.warn per page arrival, the queries that got fetched on the client but
 * weren't already in the cache when the page "arrived" — either from the SSR-dehydrated state
 * (first load) or from the route's `prefetch()` (client-side navigation). Those are gaps in a
 * route's `prefetch()` declaration in routes.config.ts: the data is fetched anyway, just after the
 * first paint instead of before it.
 *
 * The watch window reopens on every navigation.state -> 'idle' transition (prefetch() just
 * resolved) and closes once queryClient.isFetching() has stayed at 0 for SETTLE_DEBOUNCE_MS (the
 * page has settled) — so fetches from later user interaction (pagination, opening a modal) aren't
 * flagged unless they themselves trigger a router navigation. Misses accumulate over the window and
 * are logged together when it closes, rather than one console.warn per query.
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
  let missed: string[] = []

  function flush() {
    if (missed.length === 0) return
    console.warn(
      `[prefetch-audit] route "${currentRouteName(router)}" (${router.state.location.pathname}): ` +
        `${missed.length} ${missed.length > 1 ? 'queries' : 'query'} fetched after page load, ` +
        `not covered by route prefetch: ${missed.join(', ')}`
    )
    missed = []
  }

  function scheduleSettleCheck() {
    if (!windowOpen) return
    if (queryClient.isFetching() === 0) {
      closeTimer ??= setTimeout(() => {
        windowOpen = false
        closeTimer = undefined
        flush()
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
        missed.push(JSON.stringify(event.query.queryKey))
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
      flush()
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
