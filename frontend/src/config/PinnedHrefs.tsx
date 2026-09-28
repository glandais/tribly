import { useContext, useMemo, type ReactNode } from 'react'
import { UNSAFE_NavigationContext, createPath, parsePath, type To } from 'react-router-dom'
import { toBrowser } from './pinnedHistory'

/**
 * On a pinned host, every `<Link>` href is written in browser space (`/sorties/x`, not
 * `/equipes/<team>/sorties/x`).
 *
 * The client's pinned history already does it, but the server cannot: `StaticRouterProvider` hands
 * the links a stateless navigator of its own, with no way to wrap it — so the server markup carried
 * the prefixed hrefs, and React does not patch a mismatched attribute on hydration. Links read the
 * navigator from this context, which is overridden here, on both renders so the trees match; on
 * the client the mapping is a no-op, the path being clean already.
 */
export function PinnedHrefs({ children }: { children: ReactNode }) {
  const context = useContext(UNSAFE_NavigationContext)
  const value = useMemo(() => {
    const { navigator } = context
    return {
      ...context,
      navigator: {
        ...navigator,
        createHref: (to: To) => {
          const parsed = parsePath(navigator.createHref(to))
          return createPath({ ...parsed, pathname: toBrowser(parsed.pathname ?? '/') })
        },
      },
    }
  }, [context])
  return (
    <UNSAFE_NavigationContext.Provider value={value}>{children}</UNSAFE_NavigationContext.Provider>
  )
}
