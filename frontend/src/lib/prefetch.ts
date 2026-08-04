/**
 * Route prefetching utilities
 *
 * Based on best practices from:
 * https://dev.to/yoskutik/how-to-make-your-app-indefinitely-lazy-part-4-preload-in-advance-2go1
 *
 * Prefetching downloads route chunks in advance when users hover/focus navigation links,
 * reducing perceived latency when they actually navigate.
 */

import { pages, type PageKey } from '@/config/pageComponents'

/**
 * A page to prefetch, named by its export (`'RideDetailPage'`). The list is `config/pageComponents.ts`
 * itself — the same `import()` the router lazy-loads, never a second copy of it here.
 */
export type RouteKey = PageKey

// Track which routes have been prefetched to avoid duplicate requests
const prefetchedRoutes = new Set<RouteKey>()

/**
 * Prefetch a route's chunk
 * Safe to call multiple times - will only fetch once
 */
export function prefetchRoute(route: RouteKey): void {
  if (prefetchedRoutes.has(route)) {
    return
  }

  prefetchedRoutes.add(route)

  // Use requestIdleCallback if available, otherwise setTimeout
  const schedulePreload = window.requestIdleCallback || ((cb: () => void) => setTimeout(cb, 1))

  schedulePreload(() => {
    pages[route].preload().catch(() => {
      // Remove from set if prefetch fails, allowing retry
      prefetchedRoutes.delete(route)
    })
  })
}

/**
 * Prefetch multiple routes at once
 */
export function prefetchRoutes(routes: RouteKey[]): void {
  routes.forEach(prefetchRoute)
}

/**
 * Get event handlers for prefetching on hover/focus
 */
export function getPrefetchHandlers(route: RouteKey) {
  return {
    onMouseEnter: () => prefetchRoute(route),
    onFocus: () => prefetchRoute(route),
  }
}

/**
 * Prefetch common routes that users are likely to visit
 * Call this after the app is idle (e.g., after initial render)
 */
export function prefetchCommonRoutes(): void {
  // These are the most commonly visited routes
  const commonRoutes: RouteKey[] = [
    'TeamListPage',
    'PublicationListPage',
    'RideDetailPage',
    'RouteDetailPage',
    'PostDetailPage',
  ]

  // Delay prefetching to not compete with initial page load
  setTimeout(() => {
    prefetchRoutes(commonRoutes)
  }, 3000)
}
