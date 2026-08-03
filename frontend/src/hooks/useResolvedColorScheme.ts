import { createContext, useContext, useSyncExternalStore } from 'react'

function subscribe(callback: () => void) {
  const observer = new MutationObserver(callback)
  observer.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['data-mantine-color-scheme'],
  })
  return () => observer.disconnect()
}

function getSnapshot(): 'light' | 'dark' {
  return document.documentElement.getAttribute('data-mantine-color-scheme') === 'dark'
    ? 'dark'
    : 'light'
}

/**
 * The concrete scheme `entry-server.tsx` resolved for this request/render — 'dark' for a signed-in
 * visitor whose stored preference is DARK, 'light' otherwise (LIGHT, SYSTEM, or anonymous). Provided
 * by `AppProviders` so it's identical on the server and on the client's first hydration render.
 */
export const SsrColorSchemeContext = createContext<'light' | 'dark'>('light')

/**
 * SSR-safe replacement for `useComputedColorScheme('light')` wherever the resolved value picks a
 * different asset URL or computed color/className, not just a CSS-hidden icon swap.
 *
 * Always goes through `useSyncExternalStore` reading `data-mantine-color-scheme` — never
 * `useMantineColorScheme()`'s `colorScheme` directly, even for a concrete (non-'auto') value.
 * Mantine's own context state initializes from `localStorage` synchronously on the client's very
 * first render, so an anonymous visitor whose *browser* has a stored preference (never seen by the
 * server) already has that value on their first hydration render — mismatching the server, which
 * always assumes 'light' for that case. That would be a normal hydration mismatch, except React's
 * hydration commit deliberately skips patching `src`/`href` on mismatch (to avoid an unwanted
 * refetch) and only warns — so if that first render is trusted, the wrong asset sticks forever,
 * since nothing else forces a second, *non-hydration* render to correct it.
 *
 * The `getServerSnapshot` passed to `useSyncExternalStore` comes from `SsrColorSchemeContext`
 * rather than a hardcoded 'light': for a signed-in visitor with an explicit DARK preference, the
 * server already rendered dark (see `entry-server.tsx`'s `themePreference`), so the first hydration
 * render must assume dark too — hardcoding 'light' here would make that first paint wrong (a visible
 * flash) even though nothing needed correcting. The DOM-attribute snapshot mismatch check still
 * schedules a genuine follow-up render shortly after mount to catch the anonymous/browser-only case.
 */
export function useResolvedColorScheme(): 'light' | 'dark' {
  const ssrColorScheme = useContext(SsrColorSchemeContext)
  return useSyncExternalStore(subscribe, getSnapshot, () => ssrColorScheme)
}
