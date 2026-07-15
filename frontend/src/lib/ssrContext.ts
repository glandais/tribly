// Getter bridge for SSR per-request data — no Node.js imports so it is safe in the client bundle.
// entry-server.tsx connects requestContext.getStore() to this getter via setStoreGetter().
// Off-server (client bundle), the getter stays the default and every accessor returns undefined.
import type { Locale } from '@/config/paths.generated'
import type { ConfigDto } from '@/api/dto'
import type { SsrRequestStore } from './requestContext'

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
