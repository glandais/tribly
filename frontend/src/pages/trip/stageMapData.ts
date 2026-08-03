import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'
import { useGetTrip, prefetchGetTripQuery, getGetTripQueryKey } from '@/api/endpoints/trips/trips'
import { useGetRoute, prefetchGetRouteQuery } from '@/api/endpoints/routes/routes'
import { stageRouteSlug } from './stageDetailData'
import type { TripDto } from '@/api/dto'

/**
 * The one description of what the stage fullscreen map screen reads, consumed two ways:
 * `StageFullscreenMapPage` calls {@link useStageMapData} for the query results, the `stage-map`
 * route in `routes.config.ts` calls {@link prefetchStageMap} for the same data server-side. Same
 * contract as `pages/route/routeDetailData.ts`'s `route-map` — a strict subset of `stage-detail`
 * (team + trip + the stage's route, no GPS export options), so it reuses
 * {@link stageRouteSlug} from `stageDetailData.ts` instead of re-deriving the lookup. Public route:
 * no team-scoped auth gate, unconditional prefetch.
 */

/**
 * Every query `StageFullscreenMapPage` itself owns, returned as the raw query results so the page
 * keeps using `.data` / `.isLoading` directly.
 */
export function useStageMapData(teamSlug?: string, tripSlug?: string, stageSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const trip = useGetTrip(teamSlug!, tripSlug!, {
    query: { enabled: !!teamSlug && !!tripSlug },
  })

  const routeSlug = stageRouteSlug(trip.data, stageSlug)

  // `routeSlug ?? ''` keeps the hook call unconditional (consistent hook order); `enabled` is what
  // actually gates the fetch.
  const route = useGetRoute(teamSlug!, routeSlug ?? '', {
    query: { enabled: !!teamSlug && !!routeSlug },
  })

  return { team, trip, route, routeSlug }
}

/**
 * Server-side counterpart of {@link useStageMapData}, in two phases like `stageDetailData.ts`'s
 * `prefetchStageDetail`: the route slug only exists once the trip query has resolved into the
 * cache. No GPS export options here — this screen mounts no export menu, unlike stage detail.
 */
export async function prefetchStageMap(
  queryClient: QueryClient,
  teamSlug: string,
  tripSlug: string,
  stageSlug: string
): Promise<void> {
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchGetTripQuery(queryClient, teamSlug, tripSlug),
  ])
  const trip = queryClient.getQueryData<TripDto>(getGetTripQueryKey(teamSlug, tripSlug))
  const routeSlug = stageRouteSlug(trip, stageSlug)
  if (routeSlug) {
    await prefetchGetRouteQuery(queryClient, teamSlug, routeSlug)
  }
}
