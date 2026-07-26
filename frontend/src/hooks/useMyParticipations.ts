import { useListMyParticipations } from '@/api/endpoints/users/users'
import type { ListMyParticipationsParams } from '@/api/dto'
import { useAuth } from './useAuth'

/**
 * Thin facade on `GET /api/users/me/participations`.
 *
 * SSR is anonymous — the server forwards no credential — so this never fires
 * server-side and the blocks it feeds render after hydration. Callers must not
 * put it in a route `prefetch`.
 */
export function useMyParticipations(params: ListMyParticipationsParams, enabled = true) {
  const { isAuthenticated } = useAuth()

  return useListMyParticipations(params, {
    query: { enabled: isAuthenticated && enabled },
  })
}
