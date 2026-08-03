import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery, getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import {
  useGetTeamEvents,
  prefetchGetTeamEventsQuery,
  prefetchGetTokenQuery,
} from '@/api/endpoints/calendar/calendar'
import { useCalendarDateRange, getInitialCalendarRange } from '@/hooks/useCalendarDateRange'
import { useAuthStore } from '@/store/authStore'
import type { TeamDetailDto } from '@/api/dto'

/**
 * Server/client counterpart of `TeamCalendarPage`: the team, its first events window, and the ICS
 * token `IcsFeedSettings` reads unconditionally (it renders on every paint, not behind a
 * disclosure). Escaped `calendarData.ts`'s fix because the crawler never measured this route — see
 * `SSR-BUGS.md`.
 *
 * Keyed the same way `calendarData.ts` keys the personal calendar: `getInitialCalendarRange()` is
 * what `useCalendarDateRange` seeds its state with, so the first `useGetTeamEvents` call and the
 * prefetch land on the same query key. FullCalendar's own re-query of its visible grid, right after
 * mount, depends on the viewport and can't be reproduced here — it stays a client-only fetch,
 * exactly like the personal calendar's second window.
 *
 * The route is `auth: 'authenticated'` but the API beneath it is member-only
 * (`CalendarAccessChecker` needs a team role, not just a session — see the route comment in
 * `routes.config.ts`). Mirrors `prefetchMemberComments`: fetch the team first, then read its
 * `role` back out of the cache before prefetching the events that would 403 for a non-member. The
 * token endpoint carries no such gate — it's the signed-in user's own feed, not the team's — so
 * it prefetches for any authenticated visitor, matching what `IcsFeedSettings` actually fetches.
 */
export function useTeamCalendarData(teamSlug?: string) {
  const { dateRange, handleDateRangeChange } = useCalendarDateRange()

  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })

  const events = useGetTeamEvents(
    teamSlug!,
    { from: dateRange.from, to: dateRange.to },
    { query: { enabled: !!teamSlug, staleTime: 1000 * 60 * 5 } }
  )

  return { team, events, dateRange, handleDateRangeChange }
}

export async function prefetchTeamCalendar(
  queryClient: QueryClient,
  teamSlug: string
): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return

  await prefetchGetTeamQuery(queryClient, teamSlug)
  const team = queryClient.getQueryData<TeamDetailDto>(getGetTeamQueryKey(teamSlug))

  await Promise.all([
    team?.role
      ? prefetchGetTeamEventsQuery(queryClient, teamSlug, getInitialCalendarRange())
      : Promise.resolve(queryClient),
    prefetchGetTokenQuery(queryClient),
  ])
}
