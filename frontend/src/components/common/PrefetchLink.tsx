import { forwardRef, useCallback, useEffect, useRef } from 'react'
import { Link, useResolvedPath, type LinkProps } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { useMergedRef } from '@mantine/hooks'
import { prefetchUrl, type PrefetchLinkMode } from '@/lib/prefetch'

export interface PrefetchLinkProps extends Omit<LinkProps, 'prefetch'> {
  /**
   * When to warm the destination — its chunk and (except in `viewport`) its route data:
   *
   * - `intent` (default) — a moment after mouseenter/focus, cancelled if the pointer leaves first;
   *   immediately on touchstart, where there is no hover to read intent from.
   * - `viewport` — chunk ONLY, once the link is half visible. Opt-in for long lists: twenty visible
   *   cards would otherwise fire twenty rounds of API calls, and they all share one chunk anyway.
   * - `render` — chunk and data on mount. For a single, near-certain next step; never inside a list.
   * - `none` — a plain `<Link>`.
   */
  prefetch?: PrefetchLinkMode
}

/** Long enough that sweeping the pointer across a list prefetches nothing; short enough to win the
 * click. Same heuristic React Router uses for `prefetch="intent"` in framework mode. */
const INTENT_DELAY_MS = 100

/**
 * `<Link>` that prefetches what it points at.
 *
 * React Router's own `prefetch` prop only works in framework mode — in library mode (what this app
 * runs) it is a silent no-op, which is why it is `Omit`ted above and ours is destructured out rather
 * than spread onto `<Link>`. The rendered markup is identical to a plain `<Link>`, so hydration
 * parity is unaffected; everything here happens in handlers and effects, client-side only.
 *
 * Used as `component={PrefetchLink}` far more often than as an element — Mantine's polymorphic
 * `Paper`/`Anchor`/`Button` are how this app links — hence the forwarded ref.
 */
export const PrefetchLink = forwardRef<HTMLAnchorElement, PrefetchLinkProps>(function PrefetchLink(
  { prefetch = 'intent', to, onMouseEnter, onMouseLeave, onFocus, onBlur, onTouchStart, ...rest },
  ref
) {
  const queryClient = useQueryClient()
  // Router-space, with a relative `to` already resolved. NOT useHref: on a pinned single-team host
  // that returns the browser-space path (/sorties/x), which matches no route pattern — the
  // prefetch would silently do nothing on exactly those hosts.
  const resolved = useResolvedPath(to)
  const href = resolved.pathname + resolved.search
  const nodeRef = useRef<HTMLAnchorElement | null>(null)
  const mergedRef = useMergedRef(ref, nodeRef)
  const timerRef = useRef<ReturnType<typeof setTimeout> | undefined>(undefined)

  const run = useCallback(
    (chunkOnly: boolean) => prefetchUrl(queryClient, href, { chunkOnly }),
    [queryClient, href]
  )

  const disarm = useCallback(() => {
    clearTimeout(timerRef.current)
    timerRef.current = undefined
  }, [])

  const arm = useCallback(() => {
    if (prefetch !== 'intent' || timerRef.current) return
    timerRef.current = setTimeout(() => {
      timerRef.current = undefined
      run(false)
    }, INTENT_DELAY_MS)
  }, [prefetch, run])

  useEffect(() => disarm, [disarm])

  useEffect(() => {
    if (prefetch === 'render') {
      run(false)
      return
    }
    if (prefetch !== 'viewport') return
    const node = nodeRef.current
    if (!node || typeof IntersectionObserver === 'undefined') return
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((entry) => entry.isIntersecting)) {
          run(true)
          observer.disconnect()
        }
      },
      { threshold: 0.5 }
    )
    observer.observe(node)
    return () => observer.disconnect()
  }, [prefetch, run])

  return (
    <Link
      {...rest}
      to={to}
      ref={mergedRef}
      onMouseEnter={(event) => {
        onMouseEnter?.(event)
        arm()
      }}
      onMouseLeave={(event) => {
        onMouseLeave?.(event)
        disarm()
      }}
      onFocus={(event) => {
        onFocus?.(event)
        arm()
      }}
      onBlur={(event) => {
        onBlur?.(event)
        disarm()
      }}
      onTouchStart={(event) => {
        onTouchStart?.(event)
        run(false)
      }}
    />
  )
})
