import { useGetTeam } from '@/api/endpoints/teams/teams'

/**
 * `TeamAdminPage` reads nothing beyond the team itself — it only checks the role and redirects
 * to the default admin tab. That query is already covered server-side by `teamScopedPrefetch()`
 * on the `team-admin` route in `routes.config.ts`, which prefetches the same `getGetTeamQueryKey`
 * entry. There is no `prefetchTeamAdmin` here: adding one would prefetch the team a second time
 * under the same key. This module exists only so the page's fetch and the route's prefetch stay
 * discoverable in one place, per the project's "always a companion" convention.
 */
export function useTeamAdminData(teamSlug: string | undefined) {
  return useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
}
