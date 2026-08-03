import { useSyncExternalStore } from 'react'
import { useMantineColorScheme } from '@mantine/core'

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
 * `useComputedColorScheme` resolves 'auto' via matchMedia behind a post-mount effect, so it always
 * renders 'light' through hydration for anonymous/auto visitors, then flips after mount — visible
 * as an extra image fetch or a color flash. index.html's pre-hydration script already resolves the
 * real value onto `<html data-mantine-color-scheme>` from localStorage/matchMedia (or server.js
 * embeds it server-side for a signed-in visitor's explicit theme) before React ever hydrates, so
 * reading that attribute through useSyncExternalStore lets React apply the correct value at
 * hydration time via its snapshot-mismatch check, instead of a delayed effect.
 */
export function useResolvedColorScheme(): 'light' | 'dark' {
  const { colorScheme } = useMantineColorScheme()
  const autoResolved = useSyncExternalStore(subscribe, getSnapshot, getServerSnapshot)
  return colorScheme === 'auto' ? autoResolved : colorScheme
}
