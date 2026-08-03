import { useListMyParticipations } from '@/api/endpoints/users/users'
import type { ListMyParticipationsParams } from '@/api/dto'
import { useAuth } from './useAuth'

/**
 * Thin facade on `GET /api/users/me/participations`.
 *
 * A plain `useQuery`, not a suspense one, so it never blocks the prerender itself. For a
 * session-carrying SSR request, callers whose route `prefetch` populated the query cache with a
 * byte-matching key (see the `home` route in `routes.config.ts`) get the data in the initial HTML;
 * otherwise it renders after hydration, same as an anonymous visitor.
 */
export function useMyParticipations(params: ListMyParticipationsParams, enabled = true) {
  const { isAuthenticated } = useAuth()

  return useListMyParticipations(params, {
    query: { enabled: isAuthenticated && enabled },
  })
}
