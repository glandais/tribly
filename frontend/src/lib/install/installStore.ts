import { create } from 'zustand'

/**
 * Installing the site as an app ("PWA"): what the browser allows, captured once at startup.
 *
 * Nothing here runs on the server. The store stays at its initial state in the SSR process — the
 * listeners are installed by `entry-client.tsx` — and the helpers below touch `window` only when
 * called, never at module scope (`Layout` imports this file, and `entry-server` imports `Layout`).
 */

/** Chromium's install prompt (Chrome, Edge, Samsung Internet, Opera — not Safari nor Firefox). */
interface BeforeInstallPromptEvent extends Event {
  prompt: () => Promise<void>
  userChoice: Promise<{ outcome: 'accepted' | 'dismissed' }>
}

interface InstallState {
  /** The deferred prompt, kept until the member asks to install. Null when the browser has none. */
  deferredPrompt: BeforeInstallPromptEvent | null
  /** Installed during this visit (`appinstalled`). */
  installed: boolean
  /** The "Share → Add to Home Screen" instructions, for browsers with no prompt (iOS). */
  instructionsOpen: boolean
}

export const useInstallStore = create<InstallState>(() => ({
  deferredPrompt: null,
  installed: false,
  instructionsOpen: false,
}))

/**
 * Listens for the install prompt. Called once, as early as possible in `entry-client.tsx`: Chrome
 * may fire `beforeinstallprompt` before React has mounted anything.
 */
export function captureInstallPrompt(): void {
  window.addEventListener('beforeinstallprompt', (event) => {
    // Keep the browser's own mini-infobar out of the way: the site offers installation itself.
    event.preventDefault()
    useInstallStore.setState({ deferredPrompt: event as BeforeInstallPromptEvent })
  })
  window.addEventListener('appinstalled', () => {
    useInstallStore.setState({ deferredPrompt: null, installed: true })
  })
}

/** Shows the browser's install prompt. Resolves to whether the member accepted. */
export async function promptInstall(): Promise<boolean> {
  const prompt = useInstallStore.getState().deferredPrompt
  if (!prompt) return false
  // A prompt can be shown only once: whatever the answer, drop it.
  useInstallStore.setState({ deferredPrompt: null })
  await prompt.prompt()
  const { outcome } = await prompt.userChoice
  return outcome === 'accepted'
}

export function openInstallInstructions(): void {
  useInstallStore.setState({ instructionsOpen: true })
}

export function closeInstallInstructions(): void {
  useInstallStore.setState({ instructionsOpen: false })
}

/** Running as the installed app rather than in a browser tab. */
export function isStandalone(): boolean {
  if (typeof window === 'undefined') return false
  return (
    window.matchMedia?.('(display-mode: standalone)').matches ||
    (navigator as Navigator & { standalone?: boolean }).standalone === true
  )
}

export type DevicePlatform = 'ios' | 'android' | 'other'

export function devicePlatform(): DevicePlatform {
  if (typeof navigator === 'undefined') return 'other'
  const ua = navigator.userAgent
  // iPadOS presents itself as a Mac; only the touch points give it away.
  if (/iPhone|iPad|iPod/.test(ua) || (/Macintosh/.test(ua) && navigator.maxTouchPoints > 1)) {
    return 'ios'
  }
  if (/Android/.test(ua)) return 'android'
  return 'other'
}
