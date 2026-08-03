import { useMemo } from 'react'
import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'
import { useGetRide, prefetchGetRideQuery, getGetRideQueryKey } from '@/api/endpoints/rides/rides'
import {
  listRideComments,
  getListRideCommentsQueryKey,
} from '@/api/endpoints/ride-comments/ride-comments'
import { prefetchGetAvailableServicesQuery } from '@/api/endpoints/gps-services/gps-services'
import { useRoutesBulk } from '@/hooks/useRoutesBulk'
import { prefetchMemberComments, prefetchRoutesBulkChunked } from '@/config/prefetchHelpers'
import { useAuthStore } from '@/store/authStore'
import type { RideDto, RouteDetailDto } from '@/api/dto'

/**
 * The one description of what the ride detail screen reads, consumed two ways: `RideDetailPage`
 * calls {@link useRideDetailData} for the query results, the `ride-detail` route in
 * `routes.config.ts` calls {@link prefetchRideDetail} for the same data server-side. Describing it
 * twice is what this file exists to prevent: a divergence doesn't break anything visibly, it just
 * yields a different query key, so the client refetches after hydration and only
 * `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than exports of `RideDetailPage.tsx`: `routes.config.ts` is imported
 * eagerly and must not pull the page out of its lazy chunk (same contract as
 * `pages/home/nextRideParams.ts` and `components/common/placeAutocompleteParams.ts`).
 */

/**
 * The ride's own route plus every group's, deduped and **sorted** — the order is canonical because
 * the slug array goes into `useRoutesBulk`'s query key.
 *
 * One set for three call sites: the page (group asset links), `RoutesMapView` (which derives this
 * exact set from its `mapItems`, sort included) and the prefetch. The page used to derive
 * `group.routeSlug || ride.routeSlug` instead, which differs from the map's set as soon as a ride
 * with its own route has groups that name theirs — two keys, two `/routes/bulk` requests, and a
 * prefetch that could only ever cover one of them.
 */
export function rideRouteSlugs(ride: RideDto | undefined): string[] {
  if (!ride) return []
  const slugs = [ride.routeSlug, ...(ride.groups ?? []).map((g) => g.routeSlug)].filter(
    (s): s is string => !!s
  )
  return Array.from(new Set(slugs)).sort()
}

/**
 * Every query `RideDetailPage` itself owns, returned as the raw query results so the page keeps
 * using `.data` / `.isLoading` / `.error` / `.refetch` directly in its `QueryStateBoundary`.
 *
 * Auth deliberately stays out: the page reads it through `useAuth()`, the prefetch through
 * `useAuthStore.getState()` (the server can't use the hook), so no shared hook can hold it.
 */
export function useRideDetailData(teamSlug?: string, rideSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const ride = useGetRide(teamSlug!, rideSlug!, {
    query: { enabled: !!teamSlug && !!rideSlug },
  })

  // Memoized on the slugs' own values, so the request stays stable across unrelated renders.
  const slugsKey = rideRouteSlugs(ride.data).join(',')
  const slugs = useMemo(() => (slugsKey ? slugsKey.split(',') : []), [slugsKey])

  // No `geometry: false` here: `RoutesMapView` (rendered alongside, on the same non-empty
  // condition) fetches this exact slug set with full geometry anyway, so asking for metadata only
  // would just add a second, redundant request instead of sharing its cache entry.
  const routes = useRoutesBulk(teamSlug!, { slug: slugs })

  const routesBySlug = useMemo(() => {
    const map = new Map<string, RouteDetailDto>()
    for (const route of routes.data?.routes ?? []) {
      map.set(route.slug, route)
    }
    return map
  }, [routes.data])

  return { team, ride, routes, routesBySlug }
}

/**
 * Server-side counterpart of {@link useRideDetailData}, in two phases: the routes bulk needs the
 * ride's groups, which only exist once the ride query has resolved into the cache.
 *
 * It covers more than the hook does, on purpose: comments and GPS services are fetched by children
 * the page mounts (`CommentSection`, the export menu), not by the page itself — both conditional,
 * comments on membership, services on being signed in, exactly as those children are.
 */
export async function prefetchRideDetail(
  queryClient: QueryClient,
  teamSlug: string,
  rideSlug: string
): Promise<void> {
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchGetRideQuery(queryClient, teamSlug, rideSlug),
  ])

  const ride = queryClient.getQueryData<RideDto>(getGetRideQueryKey(teamSlug, rideSlug))
  await Promise.all([
    prefetchMemberComments(
      queryClient,
      teamSlug,
      rideSlug,
      listRideComments,
      getListRideCommentsQueryKey
    ),
    prefetchRoutesBulkChunked(queryClient, teamSlug, rideRouteSlugs(ride)),
    useAuthStore.getState().isAuthenticated
      ? prefetchGetAvailableServicesQuery(queryClient)
      : Promise.resolve(queryClient),
  ])
}
