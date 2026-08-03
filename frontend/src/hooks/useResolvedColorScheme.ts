import { useSyncExternalStore } from 'react'

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

function getServerSnapshot(): 'light' | 'dark' {
  return 'light'
}

/**
 * SSR-safe replacement for `useComputedColorScheme('light')` wherever the resolved value picks a
 * different asset URL or computed color/className, not just a CSS-hidden icon swap.
 *
 * Always goes through `useSyncExternalStore` reading `data-mantine-color-scheme` — never
 * `useMantineColorScheme()`'s `colorScheme` directly, even for a concrete (non-'auto') value.
 * Mantine's own context state initializes from `localStorage` synchronously on the client's very
 * first render, so a visitor with an explicit stored theme (not just 'auto') already has the real
 * value on their first hydration render — mismatching the server, which always assumes 'light' for
 * an anonymous/auto visitor. That would be a normal hydration mismatch, except React's hydration
 * commit deliberately skips patching `src`/`href` on mismatch (to avoid an unwanted refetch) and
 * only warns — so if that first render is trusted, the wrong asset sticks forever, since nothing
 * else forces a second, *non-hydration* render to correct it.
 *
 * Routing through `useSyncExternalStore` avoids this: its server snapshot ('light') makes the
 * first hydration render deliberately match what the server actually assumed, so there's nothing
 * to skip, and its built-in snapshot-mismatch check then schedules a genuine follow-up update
 * (not a hydration commit) shortly after mount — which does patch `src` correctly. For a signed-in
 * visitor whose server-rendered theme was already correct, this costs one harmless extra render
 * with the same value.
 */
export function useResolvedColorScheme(): 'light' | 'dark' {
  return useSyncExternalStore(subscribe, getSnapshot, getServerSnapshot)
}
