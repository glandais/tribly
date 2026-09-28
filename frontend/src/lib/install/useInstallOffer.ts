import { useEffect, useState } from 'react'
import {
  devicePlatform,
  isStandalone,
  openInstallInstructions,
  promptInstall,
  useInstallStore,
} from './installStore'
import { APP_STORE_URL, PLAY_STORE_URL } from './storeLinks'

/**
 * What installing means on this browser:
 * - `store` — the mobile app, from its store (only when `withStore` and the app is published);
 * - `prompt` — the browser's own install prompt (Chromium);
 * - `instructions` — iOS, where the only way is "Share → Add to Home Screen".
 */
export type InstallOffer =
  { kind: 'store'; url: string } | { kind: 'prompt' } | { kind: 'instructions' }

export interface InstallState {
  /** Null when there is nothing to offer: already installed, or the browser cannot. */
  offer: InstallOffer | null
  /** The site is running as the installed app. */
  standalone: boolean
  /** Carries the offer out. */
  install: () => void
}

/**
 * The install offer for this browser. **Null on the first render**, server and client alike: it
 * depends on the user agent and on events only the browser knows, so it is computed after
 * hydration — rendering it straight away would not match the SSR markup (docs/SSR.md).
 */
export function useInstallOffer({ withStore = false }: { withStore?: boolean } = {}): InstallState {
  const deferredPrompt = useInstallStore((state) => state.deferredPrompt)
  const installed = useInstallStore((state) => state.installed)
  const [client, setClient] = useState<{
    standalone: boolean
    platform: ReturnType<typeof devicePlatform>
  } | null>(null)

  useEffect(() => {
    setClient({ standalone: isStandalone(), platform: devicePlatform() })
  }, [])

  const offer = resolveOffer()

  function resolveOffer(): InstallOffer | null {
    if (!client || client.standalone || installed) return null
    if (withStore) {
      const storeUrl =
        client.platform === 'ios'
          ? APP_STORE_URL
          : client.platform === 'android'
            ? PLAY_STORE_URL
            : null
      if (storeUrl) return { kind: 'store', url: storeUrl }
    }
    if (client.platform === 'ios') return { kind: 'instructions' }
    if (deferredPrompt) return { kind: 'prompt' }
    return null
  }

  const install = () => {
    if (!offer) return
    if (offer.kind === 'store') {
      window.open(offer.url, '_blank', 'noopener,noreferrer')
    } else if (offer.kind === 'prompt') {
      void promptInstall()
    } else {
      openInstallInstructions()
    }
  }

  return { offer, standalone: client?.standalone ?? false, install }
}
