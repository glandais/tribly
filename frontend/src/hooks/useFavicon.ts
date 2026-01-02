import { useEffect } from 'react'

const DEFAULT_FAVICON = '/favicon.svg'

/**
 * Dynamically set the favicon. Restores default on unmount.
 */
export function useFavicon(url: string | null | undefined) {
  useEffect(() => {
    const link: HTMLLinkElement =
      document.querySelector("link[rel~='icon']") || document.createElement('link')
    link.rel = 'icon'

    if (url) {
      link.href = url
    } else {
      link.href = DEFAULT_FAVICON
    }

    if (!document.querySelector("link[rel~='icon']")) {
      document.head.appendChild(link)
    }

    return () => {
      link.href = DEFAULT_FAVICON
    }
  }, [url])
}
