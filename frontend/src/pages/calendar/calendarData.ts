import type { QueryClient } from '@tanstack/react-query'
import { prefetchGetEventsQuery, prefetchGetTokenQuery } from '@/api/endpoints/calendar/calendar'
import { getInitialCalendarRange } from '@/hooks/useCalendarDateRange'
import { useAuthStore } from '@/store/authStore'

/**
 * Server-side counterpart of `CalendarPage`'s first events query, keyed on the same
 * `getInitialCalendarRange()` `useCalendarDateRange` seeds its state with — the only reason that
 * first query is prefetchable at all. FullCalendar re-queries its own visible grid right after
 * mount; that second range depends on the viewport and can't be known here, so it stays a client
 * fetch.
 *
 * Also primes `useGetToken`, the query `IcsFeedSettings` fires on mount: `CalendarPage` renders it
 * unconditionally at the bottom of the page, not behind a disclosure or accordion the visitor has
 * to open, so it's on-screen (below the fold, but present in the initial paint) the same as the
 * events grid above it. `useGetToken()` takes no params, so there's nothing to derive — the
 * prefetched key matches by construction.
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
  await Promise.all([
    prefetchGetEventsQuery(queryClient, getInitialCalendarRange()),
    prefetchGetTokenQuery(queryClient),
  ])
}
