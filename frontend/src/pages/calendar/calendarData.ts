import type { QueryClient } from '@tanstack/react-query'
import { prefetchGetEventsQuery } from '@/api/endpoints/calendar/calendar'
import { getInitialCalendarRange } from '@/hooks/useCalendarDateRange'
import { useAuthStore } from '@/store/authStore'

/**
 * Server-side counterpart of `CalendarPage`'s first events query, keyed on the same
 * `getInitialCalendarRange()` `useCalendarDateRange` seeds its state with — the only reason that
 * first query is prefetchable at all. FullCalendar re-queries its own visible grid right after
 * mount; that second range depends on the viewport and can't be known here, so it stays a client
 * fetch.
 *
 * Auth-gated here rather than via `teamScopedPrefetch`: unlike the team-scoped admin routes, the
 * `calendar` route itself isn't team-scoped, so it reads `useAuthStore.getState()` directly, the
 * same way `profileData.ts` does.
 *
 * `TeamCalendarPage` (route `team-calendar`) queries the team-scoped sibling endpoint
 * (`useGetTeamEvents`) through the same `useCalendarDateRange` hook, but that route has no
 * `prefetch` today — left alone here; not in scope for this module.
 */
export async function prefetchCalendar(queryClient: QueryClient): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  await prefetchGetEventsQuery(queryClient, getInitialCalendarRange())
}
