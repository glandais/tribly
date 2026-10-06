import { useSyncExternalStore } from 'react'

/** Every IANA zone the browser knows, canonical names only (no aliases such as Asia/Calcutta). */
export const TIMEZONE_OPTIONS: readonly string[] = Intl.supportedValuesOf('timeZone')

const noSubscription = () => () => {}
const browserZone = () => Intl.DateTimeFormat().resolvedOptions().timeZone
const noZoneOnServer = () => null

/**
 * The browser's own zone, read only once hydrated (null on the server and on the hydration
 * render): the server rendering the page has its own zone, not the visitor's, and a field
 * prefilled with it would not match the markup the client hydrates.
 */
export function useBrowserTimezone(): string | null {
  return useSyncExternalStore(noSubscription, browserZone, noZoneOnServer)
}
