import { QueryClient } from '@tanstack/react-query'
import Axios from 'axios'

export function makeQueryClient(opts?: { isServer?: boolean }): QueryClient {
  return new QueryClient({
    defaultOptions: {
      queries: {
        // On server: data never goes stale during a single SSR render.
        // On client: 3 minutes before data is considered stale.
        staleTime: opts?.isServer ? Infinity : 3 * 60 * 1000,
        // Server: 2000ms (NOT 0) — per TanStack docs, gcTime 0 can garbage-collect
        // query entries before dehydration completes, causing dropped
        // dehydration/hydration and mismatch errors. A short window keeps entries
        // alive across the dehydrate() call.
        // Client: 10 minutes to cover tab-away / back navigation. gcTime must exceed
        // staleTime so stale-but-cached data serves instantly while a refetch completes.
        gcTime: opts?.isServer ? 2000 : 10 * 60 * 1000,
        retry: (failureCount, error) => {
          if (opts?.isServer) return false
          // Defensive retry for 401 — the axios interceptor handles token refresh,
          // but allow one retry in case the interceptor rejection races with the new token.
          const status = Axios.isAxiosError(error) ? error.response?.status : undefined
          if (status === 401) return failureCount < 2
          // Any other 4xx is the server's final answer — a missing entity, a forbidden page, a bad
          // parameter. Retrying one only held the page on its skeleton for ~8 s, a toast per
          // attempt, before `QueryStateBoundary` could render its "not found". 408 and 429 are
          // the exceptions: they do mean "try again".
          if (status && status >= 400 && status < 500 && status !== 408 && status !== 429) {
            return false
          }
          return failureCount < 3
        },
        retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 5000),
        refetchOnWindowFocus: false,
      },
      mutations: {
        retry: false,
      },
    },
  })
}
