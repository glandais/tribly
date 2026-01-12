import { useEffect } from 'react'

const DEFAULT_FAVICON = '/favicon.svg'

/**
 * Dynamically set the favicon.
 * Does not restore on unmount to avoid unnecessary downloads during route transitions.
 */
export function useFavicon(url: string | null | undefined) {
  useEffect(() => {
    const link: HTMLLinkElement =
      document.querySelector("link[rel~='icon']") || document.createElement('link')
    link.rel = 'icon'

    const newHref = url || DEFAULT_FAVICON

    // Only update if href actually changed to avoid unnecessary downloads
    if (link.href !== newHref && !link.href.endsWith(newHref)) {
      link.href = newHref
    }

    if (!document.querySelector("link[rel~='icon']")) {
      document.head.appendChild(link)
    }
  }, [url])
}
