import type { QueryClient } from '@tanstack/react-query'
import { getListPublicationsQueryKey } from '@/api/endpoints/publications/publications'
import { getGetTeamDashboardQueryKey } from '@/api/endpoints/teams/teams'

/**
 * Refetches the team dashboard. Its sections mirror many lists (drafts, upcoming rides, rides
 * without a route, reports, templates, routes, ads, newest members, webhook, team settings), so
 * every mutation that changes one of them calls this — otherwise the « À traiter » band stays
 * stale for the whole client staleTime after the organizer acted on it.
 */
export function invalidateTeamDashboard(queryClient: QueryClient, teamSlug: string): void {
  void queryClient.invalidateQueries({ queryKey: getGetTeamDashboardQueryKey(teamSlug) })
}

/**
 * After a publication (ride, post, trip) is created, edited or published: the team's publication
 * lists and the dashboard, which shows drafts, upcoming rides and rides without a route.
 */
export function invalidateTeamPublications(queryClient: QueryClient, teamSlug: string): void {
  void queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug) })
  invalidateTeamDashboard(queryClient, teamSlug)
}
