import type { QueryClient } from '@tanstack/react-query'
import {
  prefetchGetTeamQuery,
  prefetchGetTeamDashboardQuery,
  useGetTeamDashboard,
} from '@/api/endpoints/teams/teams'
import { prefetchGetAvailableServicesQuery } from '@/api/endpoints/gps-services/gps-services'
import { useAuthStore } from '@/store/authStore'

/**
 * The one description of what the team's home (`team-detail`) reads: the dashboard
 * (`GET /api/teams/{slug}/dashboard`), for everyone since API-86 — a visitor gets its public part
 * (upcoming rides, latest posts, new routes), a member their own blocks on top. The former feed is
 * gone (ledger WEB-68): its addresses redirect (pages/team/teamLegacyRedirects.ts).
 *
 * Same contract as the other `…Data.ts` modules: `TeamHomePage` reads it through
 * {@link useTeamDashboardData}, the route's `prefetch` through {@link prefetchTeamHome}, on the
 * same query keys. Its own module so `routes.config.ts`, which imports it eagerly, does not pull
 * the page out of its lazy chunk.
 */

/**
 * The dashboard itself, once the team is known — an unknown or members-only team answers 404/403
 * here too, and the page tells those apart from the team's own query. The GPS services are read
 * by the « Envoyer vers » menu of a member's next rides (`useGpsConnections`).
 */
export function useTeamDashboardData(teamSlug: string, enabled: boolean) {
  const dashboard = useGetTeamDashboard(teamSlug, { query: { enabled } })
  return { dashboard }
}

/**
 * Server-side (and hover) counterpart of `TeamHomePage`: the team and its dashboard side by side
 * — the dashboard no longer waits for the team's `role`, it answers everyone. The GPS services
 * only matter to a signed-in reader (« Mes prochaines » is a member's block).
 */
export async function prefetchTeamHome(queryClient: QueryClient, teamSlug: string): Promise<void> {
  const signedIn = useAuthStore.getState().isAuthenticated
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchGetTeamDashboardQuery(queryClient, teamSlug),
    signedIn ? prefetchGetAvailableServicesQuery(queryClient) : undefined,
  ])
}
