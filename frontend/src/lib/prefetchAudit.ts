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
const SETTLE_DEBOUNCE_MS = 5000

/**
 * Logs, in a single console.warn, the queries that got fetched on the client during the very first
 * page load but weren't already in the SSR-dehydrated cache. Those are gaps in that route's
 * `prefetch()` declaration in routes.config.ts: the data is fetched anyway, just after the first
 * paint instead of before it.
 *
 * First load only: the watch window closes once queryClient.isFetching() has stayed at 0 for
 * SETTLE_DEBOUNCE_MS (the page has settled), and the whole thing disarms for good the moment a
 * route change starts — no re-arming on client-side navigation.
 */
export function installPrefetchAudit(queryClient: QueryClient, router: Router): void {
  const coveredHashes = new Set(
    queryClient
      .getQueryCache()
      .getAll()
      .map((q) => q.queryHash)
  )
  let closeTimer: ReturnType<typeof setTimeout> | undefined
  const missed: string[] = []

  function flush() {
    if (missed.length === 0) return
    console.warn(
      `[prefetch-audit] route "${currentRouteName(router)}" (${router.state.location.pathname}): ` +
        `${missed.length} ${missed.length > 1 ? 'queries' : 'query'} fetched after page load, ` +
        `not covered by route prefetch: ${missed.join(', ')}`
    )
  }

  const unsubscribeCache = queryClient.getQueryCache().subscribe((event) => {
    if (event.type === 'updated' && event.action.type === 'fetch') {
      const hash = event.query.queryHash
      if (!coveredHashes.has(hash)) {
        coveredHashes.add(hash)
        missed.push(JSON.stringify(event.query.queryKey))
      }
    }
  })

  function disarm() {
    if (closeTimer) {
      clearTimeout(closeTimer)
      closeTimer = undefined
    }
    unsubscribeCache()
  }

  closeTimer = setTimeout(() => {
    disarm()
    flush()
  }, SETTLE_DEBOUNCE_MS)
}
