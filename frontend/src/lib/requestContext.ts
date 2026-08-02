// SSR-only module: uses Node.js built-ins and must ONLY be imported from SSR-only files
// (entry-server.tsx / server code). Client code reads request data via ssrContext.ts, which
// carries no Node imports so it stays safe in the client bundle.
import { AsyncLocalStorage } from 'node:async_hooks'
import type { Locale } from '@/config/paths.generated'
import type { ConfigDto, UserDto } from '@/api/dto'

/**
 * The session resolved for this request, from the incoming `refresh_token` cookie. Absent when the
 * request carried no cookie or the refresh was rejected — in which case the render is anonymous,
 * exactly as it was before SSR became session-aware.
 */
export interface SsrAuthSnapshot {
  accessToken: string
  user: UserDto | null
  hasPasskeys: boolean
}

// Per-request data made available to the render tree via AsyncLocalStorage.
// SSR is session-aware but shares NOTHING between requests: the session lives here, never in the
// module-level Zustand singleton, which is common to every concurrent render in the Node process.
export interface SsrRequestStore {
  headers: Record<string, string>
  locale?: Locale
  config?: ConfigDto
  auth?: SsrAuthSnapshot
}

export const requestContext = new AsyncLocalStorage<SsrRequestStore>()
