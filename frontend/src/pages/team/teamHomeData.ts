import type { QueryClient } from '@tanstack/react-query'
import {
  getGetTeamQueryKey,
  prefetchGetTeamQuery,
  prefetchGetTeamDashboardQuery,
  useGetTeamDashboard,
} from '@/api/endpoints/teams/teams'
import { prefetchGetAvailableServicesQuery } from '@/api/endpoints/gps-services/gps-services'
import type { TeamDetailDto } from '@/api/dto'
import { paths } from '@/config/paths'
import { useAuthStore } from '@/store/authStore'
import { prefetchPublicationList } from '@/pages/publication/publicationListData'

/**
 * The one description of what the team's home (`team-detail`) reads. A member of the team lands on
 * the dashboard (`GET /api/teams/{slug}/dashboard`, one call for every section of their role); an
 * anonymous visitor or a non-member keeps the public feed, unchanged. `?tab=publications` gives a
 * member the feed too: it is the « Publications » tab of the team page, on the same URL.
 *
 * Same contract as `pages/publication/publicationListData.ts`: `TeamHomePage` reads it through
 * {@link showsTeamDashboard} and {@link useTeamDashboardData}, the route's `prefetch` through
 * {@link prefetchTeamHome}, so both sides pick the same branch and fill the same query keys. Its
 * own module so `routes.config.ts`, which imports it eagerly, does not pull the page out of its
 * lazy chunk.
 */

/** Value of `?tab=` that shows the feed to a member. Declared in `teamPublicationFiltersSchema`. */
export const TEAM_FEED_TAB = 'publications'

/**
 * Whether the team page shows the dashboard: only to a member of the team (`role` is set from
 * the session, server-side), and not when the URL asks for the feed.
 */
export function showsTeamDashboard(
  team: Pick<TeamDetailDto, 'role'> | undefined,
  searchParams: URLSearchParams
): boolean {
  return !!team?.role && searchParams.get('tab') !== TEAM_FEED_TAB
}

/**
 * The feed of the team, as a member reaches it from the dashboard — optionally narrowed with the
 * feed's own URL filters (`type`, `w`…, see `publicationFiltersAlias`).
 */
export function teamFeedPath(teamSlug: string, filters?: Record<string, string>): string {
  const query = new URLSearchParams({ tab: TEAM_FEED_TAB, ...filters })
  return `${paths.team(teamSlug)}?${query.toString()}`
}

/**
 * The dashboard itself. The GPS services are read by the « Envoyer vers » menu of the user's next
 * rides (`useGpsConnections`), hence their place in the prefetch.
 */
export function useTeamDashboardData(teamSlug: string, enabled: boolean) {
  const dashboard = useGetTeamDashboard(teamSlug, { query: { enabled } })
  return { dashboard }
}

export async function prefetchTeamDashboard(
  queryClient: QueryClient,
  teamSlug: string
): Promise<void> {
  await Promise.all([
    prefetchGetTeamDashboardQuery(queryClient, teamSlug),
    prefetchGetAvailableServicesQuery(queryClient),
  ])
}

/**
 * Server-side (and hover) counterpart of `TeamHomePage`: an anonymous request goes straight to
 * the public feed; a signed-in one needs the team first, since only its `role` says which of the
 * two pages renders.
 */
export async function prefetchTeamHome(
  queryClient: QueryClient,
  teamSlug: string,
  url: URL
): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) {
    await prefetchPublicationList(queryClient, teamSlug, url)
    return
  }
  await prefetchGetTeamQuery(queryClient, teamSlug)
  const team = queryClient.getQueryData<TeamDetailDto>(getGetTeamQueryKey(teamSlug))
  // An unknown team or a failed read: the page tells them apart from the query's error. Going on
  // to the feed's prefetch would read the team a second time (an errored query has no data, so it
  // is stale) — a 404 twice, a 5xx through a second retry cycle (e2e/error-states.e2e.ts).
  if (!team) return
  if (showsTeamDashboard(team, url.searchParams)) {
    await prefetchTeamDashboard(queryClient, teamSlug)
  } else {
    await prefetchPublicationList(queryClient, teamSlug, url)
  }
}
