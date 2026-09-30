import { useEffect } from 'react'

/**
 * Marks `<html data-hydrated>` once the first client render is committed (docs/LEDGER_*.md WEB-35).
 *
 * The last sibling of the tree, so its effect runs after every other effect of that commit: from
 * then on the server markup is React's, and a click reaches its handler. Before it, a node can
 * already carry React's `__reactProps` (set while the hydration render is still in progress) and a
 * click on it is lost — which is what the e2e helper `hydrated()` waits past. Client-side only: an
 * effect never runs on the server, so the SSR document cannot claim it. Rendering without
 * hydration (`createRoot`, no dehydrated state) sets it too, after its first commit.
 */
export function HydrationMarker() {
  useEffect(() => {
    document.documentElement.dataset.hydrated = 'true'
  }, [])
  return null
}
