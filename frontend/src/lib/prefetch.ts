/**
 * Route prefetching — warming a destination before the visitor commits to it.
 *
 * Two halves, and both matter: the destination's **lazy chunk** (its JS) and its **data** (the
 * `prefetch()` its route declares in routes.config.ts, the very same one the router loader runs on
 * navigation). Paid on hover/focus, a click then renders from cache instead of paying chunk *then*
 * API in series. This is React Router framework mode's `prefetch="intent"`, rebuilt on top of our
 * library-mode router — see `components/common/PrefetchLink.tsx` for the component that drives it.
 */

import type { QueryClient } from '@tanstack/react-query'
import type { ComponentType, LazyExoticComponent } from 'react'
import { pages, type LazyPage, type PageKey } from '@/config/pageComponents'
import { findMatchingRoute } from '@/config/routeUtils'
import { runRoutePrefetch } from '@/config/runRoutePrefetch'
import type { RouteConfig, RouteParams } from '@/config/routes.types'
import { isSingleTeam } from '@/config/appConfig'
import { useAuthStore } from '@/store/authStore'
import { isPrefetchAuditWindowOpen } from './prefetchAudit'

/** How a `<PrefetchLink>` decides when to warm its destination. See the component's docblock. */
export type PrefetchLinkMode = 'intent' | 'viewport' | 'render' | 'none'

/**
 * Chunks already requested, keyed by the page component itself rather than by name: two routes can
 * share one page (`PublicationListPage`), and they share its chunk too. A failed load is dropped
 * from the set so a later hover retries.
 */
const preloadedChunks = new WeakSet<object>()

function preloadPage(component: ComponentType | LazyExoticComponent<ComponentType>): void {
  // RouteConfig.component is declared as the wider ComponentType union; `preload` comes from
  // pageComponents.ts's LazyPage.
  if (typeof component !== 'object' || component === null || !('preload' in component)) return
  if (preloadedChunks.has(component)) return
  preloadedChunks.add(component)
  void (component as LazyPage).preload().catch(() => {
    preloadedChunks.delete(component)
  })
}

/** Skip prefetching entirely when the visitor is paying for bytes, or an audit is listening. */
function isPrefetchWorthwhile(): boolean {
  const connection = (
    navigator as Navigator & { connection?: { saveData?: boolean; effectiveType?: string } }
  ).connection
  if (connection?.saveData) return false
  if (connection?.effectiveType === '2g' || connection?.effectiveType === 'slow-2g') return false
  // Same compile-time define that installs the audit (App.tsx): in a normal build this whole branch
  // — and the import behind it — is dead code, so prefetchAudit never reaches the bundle.
  return !(__PREFETCH_AUDIT_ENABLED__ && isPrefetchAuditWindowOpen())
}

/**
 * Resolve an in-app href to the route that a click on it would actually render, or null when there
 * is nothing worth warming.
 *
 * `href` must be **router-space** — the space routesConfig patterns are written in. On a pinned
 * single-team host that is NOT what the browser shows: `pinnedHistory` maps `/equipes/np/sorties/x`
 * (router) to `/sorties/x` (browser), so an `href` read off the anchor matches nothing at all. Hence
 * `PrefetchLink` resolves with `useResolvedPath`, never `useHref`.
 */
export function resolvePrefetchTarget(
  href: string
): { route: RouteConfig; params: RouteParams; url: URL } | null {
  // Only same-origin app paths: rejects http(s)://, //host, mailto:, tel: and bare #anchors.
  if (!href.startsWith('/') || href.startsWith('//')) return null

  const url = new URL(href, window.location.origin)
  const match = findMatchingRoute(url.pathname)
  if (!match) return null

  const { route, params } = match

  // Everything below mirrors what RouteGenerator would actually render for this route: where it
  // renders a redirect instead of the page, warming it is pure waste (and, for an authenticated
  // route hovered anonymously, a guaranteed 401 in the console).
  if (route.hideWhenSingleTeam && isSingleTeam()) return null

  const { isAuthenticated } = useAuthStore.getState()
  if (route.auth === 'authenticated' && !isAuthenticated) return null
  if (route.auth === 'unauthenticated' && isAuthenticated) return null

  return { route, params, url }
}

/**
 * Warm a destination: its chunk, then its route data.
 *
 * Never throws and safe to call on every mouseenter — the chunk is deduped by component, and the
 * data goes through `queryClient.prefetchQuery`, a no-op while the entry is still fresh (client
 * `staleTime` is 3 min, see lib/queryClient.ts) and deduped against any identical fetch in flight.
 *
 * `chunkOnly` is what viewport mode passes: a feed of twenty cards must not fire twenty rounds of
 * API calls as it scrolls past, and they all share one chunk anyway.
 */
export function prefetchUrl(
  queryClient: QueryClient,
  href: string,
  opts?: { chunkOnly?: boolean }
): void {
  if (typeof window === 'undefined') return
  if (!isPrefetchWorthwhile()) return

  const target = resolvePrefetchTarget(href)
  if (!target) return

  // Not scheduled on requestIdleCallback: that is right for the speculative warm-up below, and
  // wrong here — on a busy main thread idle callbacks can be deferred for seconds, which is exactly
  // the window a hover is trying to win.
  preloadPage(target.route.component)

  if (opts?.chunkOnly) return
  void runRoutePrefetch(target.route, queryClient, target.params, target.url, 'link')
}

/**
 * Prefetch the chunks of the pages a visitor is most likely to reach, once the app has settled.
 * Unlike `prefetchUrl` these carry no URL, so no data can be warmed — it's the cold-start
 * complement to hover prefetching, not a replacement for it.
 */
export function prefetchCommonRoutes(): void {
  const commonPages: PageKey[] = [
    'TeamListPage',
    'PublicationListPage',
    'RideDetailPage',
    'RouteDetailPage',
    'PostDetailPage',
  ]

  // Delay so this never competes with the initial page load, then take whatever idle time is going.
  setTimeout(() => {
    const schedule = window.requestIdleCallback || ((cb: () => void) => setTimeout(cb, 1))
    for (const key of commonPages) {
      schedule(() => preloadPage(pages[key]))
    }
  }, 3000)
}
