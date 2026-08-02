import { useListMyParticipations } from '@/api/endpoints/users/users'
import type { ListMyParticipationsParams } from '@/api/dto'
import { useAuth } from './useAuth'

/**
 * Thin facade on `GET /api/users/me/participations`.
 *
 * A plain `useQuery`, not a suspense one, so even when SSR resolves the visitor's session (see
 * SSR.md) this doesn't block the prerender — it never fires server-side, and the blocks it feeds
 * render after hydration. Callers must not put it in a route `prefetch`: that's the mechanism that
 * actually gets data into the SSR markup, and this hook was never wired into one.
 */
export function useMyParticipations(params: ListMyParticipationsParams, enabled = true) {
  const { isAuthenticated } = useAuth()

  return useListMyParticipations(params, {
    query: { enabled: isAuthenticated && enabled },
  })
}
