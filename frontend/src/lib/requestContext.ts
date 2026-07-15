// SSR-only module: uses Node.js built-ins and must ONLY be imported from SSR-only files
// (entry-server.tsx / server code). Client code reads request data via ssrContext.ts, which
// carries no Node imports so it stays safe in the client bundle.
import { AsyncLocalStorage } from 'node:async_hooks'
import type { Locale } from '@/config/paths.generated'
import type { ConfigDto } from '@/api/dto'

// Per-request data made available to the render tree via AsyncLocalStorage.
// SSR is anonymous and stateless: only tenant-routing / language headers are forwarded,
// never cookies or Authorization.
export interface SsrRequestStore {
  headers: Record<string, string>
  locale?: Locale
  config?: ConfigDto
}

export const requestContext = new AsyncLocalStorage<SsrRequestStore>()
