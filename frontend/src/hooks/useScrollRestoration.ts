import { useEffect, useLayoutEffect, useRef } from 'react'
import { useLocation, useNavigationType } from 'react-router-dom'

const positions = new Map<string, number>()

/** Give up rather than spin forever if the list never grows tall enough. */
const MAX_ATTEMPTS = 60

/**
 * A hidden tab never fires `requestAnimationFrame`, and a route can perfectly
 * well be restored in one (opened in a background tab, then switched to).
 */
const schedule = (cb: () => void): (() => void) => {
  if (document.hidden) {
    const id = window.setTimeout(cb, 16)
    return () => window.clearTimeout(id)
  }
  const id = requestAnimationFrame(cb)
  return () => cancelAnimationFrame(id)
}

/**
 * Restores the scroll position on back/forward navigation.
 *
 * `<ScrollRestoration>` only works with a data router, and this app mounts a
 * plain `<BrowserRouter>`, so positions are tracked by hand.
 *
 * Mount once, in `Layout`, alongside the scroll-to-top effect: the two never
 * fight because they key off mutually exclusive navigation types. PUSH scrolls
 * to top, POP restores, REPLACE (a filter edit) leaves the scroll alone.
 */
export function useScrollRestoration(): void {
  const { key } = useLocation()
  const navigationType = useNavigationType()

  // The position the user actually reached. Never read `window.scrollY` when
  // leaving a route: by then the DOM already holds the incoming — usually
  // shorter — page and the browser has clamped it. Clamping does not dispatch a
  // scroll event, so this ref still holds the real value.
  const lastScroll = useRef(0)

  useEffect(() => {
    if ('scrollRestoration' in window.history) {
      window.history.scrollRestoration = 'manual'
    }
  }, [])

  useEffect(() => {
    const onScroll = () => {
      lastScroll.current = window.scrollY
    }
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  // Declared before the restore effect so its cleanup runs first: the outgoing
  // position is banked before the incoming one is read.
  useLayoutEffect(() => {
    lastScroll.current = window.scrollY
    return () => {
      positions.set(key, lastScroll.current)
    }
  }, [key])

  useLayoutEffect(() => {
    if (navigationType !== 'POP') return
    const target = positions.get(key) ?? 0
    if (target === 0) return

    let attempts = 0
    let cancel: (() => void) | undefined

    // The list may still be a skeleton: wait until the page is tall enough,
    // otherwise the browser clamps the scroll and the position is lost.
    const attempt = (): boolean => {
      const maxScroll = document.documentElement.scrollHeight - window.innerHeight
      if (maxScroll >= target || attempts >= MAX_ATTEMPTS) {
        window.scrollTo(0, target)
        return true
      }
      return false
    }

    // Often enough on its own: React Query serves the list from cache, so the
    // page is already full height by the time this layout effect runs.
    if (attempt()) return

    const retry = () => {
      attempts += 1
      if (!attempt()) cancel = schedule(retry)
    }
    cancel = schedule(retry)
    return () => cancel?.()
  }, [key, navigationType])
}
