import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'
import { useGetTrip, prefetchGetTripQuery, getGetTripQueryKey } from '@/api/endpoints/trips/trips'
import {
  listTripComments,
  getListTripCommentsQueryKey,
} from '@/api/endpoints/trip-comments/trip-comments'
import { prefetchMemberComments, prefetchRoutesBulkChunked } from '@/config/prefetchHelpers'
import type { TripDto } from '@/api/dto'

/**
 * The one description of what the trip detail screen reads, consumed two ways: `TripDetailPage`
 * calls {@link useTripDetailData} for the query results, the `trip-detail` route in
 * `routes.config.ts` calls {@link prefetchTripDetail} for the same data server-side. Describing it
 * twice is what this file exists to prevent: a divergence doesn't break anything visibly, it just
 * yields a different query key, so the client refetches after hydration and only
 * `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than exports of `TripDetailPage.tsx`: `routes.config.ts` is imported
 * eagerly and must not pull the page (or the lazily-loaded `RoutesMapView`) out of its own chunk
 * (same contract as `pages/ride/rideDetailData.ts`).
 */

/**
 * The route slug(s) `RoutesMapView` fetches for this trip: the trip's own `routeSlug` for a
 * single-stage trip, otherwise every stage's route slug. Unlike `rideRouteSlugs`, this isn't
 * deduped/sorted here — `prefetchRoutesBulkChunked` does that itself, and so does `RoutesMapView`
 * client-side (it derives its own `dedupedSlugs` from the same `routeSlug`-bearing items before
 * calling `useRoutesBulk`) — both land on the same final slug set.
 */
export function tripRouteSlugs(trip: TripDto | undefined): string[] {
  if (!trip) return []
  return trip.routeSlug
    ? [trip.routeSlug]
    : (trip.stages ?? []).flatMap((stage) => (stage.route?.slug ? [stage.route.slug] : []))
}

/**
 * Every query `TripDetailPage` itself owns, returned as the raw query results so the page keeps
 * using `.data` / `.isLoading` / `.error` / `.refetch` directly in its `QueryStateBoundary`.
 *
 * The routes-bulk fetch for the map is deliberately not here: `TripDetailPage` only builds the
 * `MapRouteItem[]` list (id/name/routeSlug) it hands to `RoutesMapView`, which owns the
 * `useRoutesBulk` call itself — same split as the prefetch, which covers it as data fetched by a
 * mounted child, not by the page.
 *
 * Auth deliberately stays out: the page reads it through `useAuth()`, the prefetch through
 * `useAuthStore.getState()` (the server can't use the hook), so no shared hook can hold it.
 */
export function useTripDetailData(teamSlug?: string, tripSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const trip = useGetTrip(teamSlug!, tripSlug!, {
    query: { enabled: !!teamSlug && !!tripSlug },
  })

  return { team, trip }
}

/**
 * Server-side counterpart of {@link useTripDetailData}, in two phases: the routes bulk needs the
 * trip's own `routeSlug`/stages, which only exist once the trip query has resolved into the cache.
 *
 * It covers more than the hook does, on purpose: comments are fetched by `CommentSection`, and the
 * map's routes by `RoutesMapView` — both children `TripDetailPage` mounts, not the page itself.
 * Comments are additionally conditional on membership (`prefetchMemberComments` reads the team's
 * `role` off the cache).
 */
export async function prefetchTripDetail(
  queryClient: QueryClient,
  teamSlug: string,
  tripSlug: string
): Promise<void> {
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchGetTripQuery(queryClient, teamSlug, tripSlug),
  ])

  const trip = queryClient.getQueryData<TripDto>(getGetTripQueryKey(teamSlug, tripSlug))
  await Promise.all([
    prefetchMemberComments(
      queryClient,
      teamSlug,
      tripSlug,
      listTripComments,
      getListTripCommentsQueryKey
    ),
    prefetchRoutesBulkChunked(queryClient, teamSlug, tripRouteSlugs(trip)),
  ])
}
