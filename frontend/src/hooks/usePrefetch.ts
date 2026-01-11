import { useCallback } from 'react'
import { prefetchRoute, getPrefetchHandlers, type RouteKey } from '../lib/prefetch'

/**
 * Hook for prefetching routes
 *
 * Usage:
 * ```tsx
 * const { prefetch, handlers } = usePrefetch('teamDetail')
 *
 * // Method 1: Use handlers on any element
 * <Link to={...} {...handlers}>Team</Link>
 *
 * // Method 2: Call prefetch manually
 * <button onMouseEnter={() => prefetch('rideDetail')}>View Ride</button>
 * ```
 */
export function usePrefetch(defaultRoute?: RouteKey) {
  const prefetch = useCallback((route: RouteKey) => {
    prefetchRoute(route)
  }, [])

  const handlers = defaultRoute ? getPrefetchHandlers(defaultRoute) : undefined

  return {
    prefetch,
    handlers,
  }
}
