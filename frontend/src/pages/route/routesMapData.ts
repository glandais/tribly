import { useMemo, useState } from 'react'
import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'
import { useGetRoutesBounds, prefetchGetRoutesBoundsQuery } from '@/api/endpoints/routes/routes'
import { useRouteFilters } from '@/hooks/useRouteFilters'
import { readUrlFilters } from '@/hooks/useUrlFilters'
import { routeFiltersSchema, routeFiltersAlias } from '@/hooks/filters/routeFilters'
import { teamRoutesTilesUrl, toRouteTileFilters } from '@/components/map/mapConstants'

/**
 * The one description of what the team routes map screen reads, consumed two ways: `RoutesMapPage`
 * calls {@link useRoutesMapData} for the query results, the `routes-map` route in
 * `routes.config.ts` calls {@link prefetchRoutesMap} for the same data server-side. Same contract
 * as `pages/ride/rideDetailData.ts` — its own module so `routes.config.ts`, which imports it
 * eagerly, doesn't pull the page (or its map chunk) out of its lazy bundle.
 *
 * Public route: no team-scoped auth gate, unconditional prefetch like `routeDetailData.ts`'s
 * `prefetchRouteMap`.
 *
 * The bounds query is keyed on `toRouteTileFilters(filters)`, frozen at mount by the page (narrowing
 * the filters afterwards reframes nothing, only a remount asks for a fresh extent) — the prefetch
 * reads the *same* filters from the request's query string through the same schema/alias the page's
 * `useRouteFilters` uses, so a filtered link (`?hill=HILLY`) server-renders the framed extent instead
 * of the unfiltered one. The tiles themselves are fetched by MapLibre outside React Query, so there is
 * nothing to prefetch for them.
 */

/** The schema/alias pair both readers must use — the page through the URL, the prefetch through `url.searchParams`. */
export const routesMapFilterOptions = {
  schema: routeFiltersSchema,
  alias: routeFiltersAlias,
} as const

/**
 * The team, the tile URL MapLibre fetches client-side, and the initial bounds. Returns the filter
 * state as `useRouteFilters` gives it (the panel needs the setters) plus the raw query results, so
 * the page keeps reading `.data` / `.isLoading` directly.
 */
export function useRoutesMapData(teamSlug?: string) {
  const filterState = useRouteFilters(routesMapFilterOptions)
  const { filters } = filterState

  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })

  const tilesUrl = useMemo(
    () => (team.data ? teamRoutesTilesUrl(team.data.slug, toRouteTileFilters(filters)) : undefined),
    [team.data, filters]
  )

  // Frozen at mount, so narrowing the filters reframes nothing and asks the server nothing. The
  // list/map toggle is a navigation, hence a remount, hence a fresh extent.
  const [initialFilters] = useState(() => toRouteTileFilters(filters))
  const bounds = useGetRoutesBounds(teamSlug!, initialFilters, {
    query: { enabled: !!teamSlug },
  })

  return { ...filterState, team, tilesUrl, bounds }
}

/**
 * Server-side counterpart of {@link useRoutesMapData}: the team and its routes' bounds, the latter
 * keyed on the same filters the page's `useState(() => toRouteTileFilters(filters))` freezes at
 * mount, read here from the request's query string instead of the router's.
 */
export async function prefetchRoutesMap(
  queryClient: QueryClient,
  teamSlug: string,
  url: URL
): Promise<void> {
  const filters = readUrlFilters(url.searchParams, routesMapFilterOptions)
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchGetRoutesBoundsQuery(queryClient, teamSlug, toRouteTileFilters(filters)),
  ])
}
