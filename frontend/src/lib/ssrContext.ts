// Getter bridge for SSR per-request data — no Node.js imports so it is safe in the client bundle.
// entry-server.tsx connects requestContext.getStore() to this getter via setStoreGetter().
// Off-server (client bundle), the getter stays the default and every accessor returns undefined.
import type { Locale } from '@/config/paths.generated'
import type { ConfigDto } from '@/api/dto'
import type { SsrAuthSnapshot, SsrRequestStore } from './requestContext'

type StoreGetter = () => SsrRequestStore | undefined

let _getStore: StoreGetter = () => undefined

export function setStoreGetter(fn: StoreGetter) {
  _getStore = fn
}

export function getSSRHeaders(): Record<string, string> | undefined {
  return _getStore()?.headers
}

export function getSSRLocale(): Locale | undefined {
  return _getStore()?.locale
}

export function getSSRConfig(): ConfigDto | undefined {
  return _getStore()?.config
}

/**
 * The session for the request currently being rendered, or undefined for an anonymous render.
 * This is the ONLY way server-side code may reach auth state — reading or writing the Zustand
 * singleton on the server would leak one visitor's session into another's page.
 */
export function getSSRAuth(): SsrAuthSnapshot | undefined {
  return _getStore()?.auth
}
