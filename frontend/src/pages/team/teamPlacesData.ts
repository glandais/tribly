import type { QueryClient } from '@tanstack/react-query'
import { prefetchListPlacesQuery } from '@/api/endpoints/places/places'
import { placeFiltersSchema, placeFiltersAlias } from '@/hooks/filters/placeFilters'
import { readUrlFilters } from '@/hooks/useUrlFilters'

/**
 * Server-side counterpart of the places list `PlaceList` renders under `team-admin-places`.
 * There is no `useTeamPlacesData` hook here: `TeamPlacesPage` itself owns no query — it only
 * resolves the team (via `teamScopedPrefetch`) and mounts `<PlaceList teamSlug canManage />`,
 * which reads the list directly through `useListPlaces(teamSlug, filters)` with `filters` from
 * `useUrlFilters({ schema: placeFiltersSchema, alias: placeFiltersAlias })` (`placeListFilterOptions`
 * below). Keep both sides pointed at that one export, or a filters change on one side silently
 * stops matching the other's query key.
 *
 * The prefetch reads the request's query string through that same `placeListFilterOptions`, via
 * `readUrlFilters`, so a link that adds `?search=…` or `?p=1` now server-renders that list instead
 * of the default one. `PlaceList` computes `totalPages` itself and never calls `usePaginatedQuery`
 * — it doesn't fetch neighbouring pages — so only the single requested page is primed here, unlike
 * `routeListData.ts`'s `prefetchPageWindow`.
 */
export const placeListFilterOptions = {
  schema: placeFiltersSchema,
  alias: placeFiltersAlias,
} as const

export async function prefetchTeamPlaces(
  queryClient: QueryClient,
  teamSlug: string,
  url: URL
): Promise<void> {
  const filters = readUrlFilters(url.searchParams, placeListFilterOptions)
  await prefetchListPlacesQuery(queryClient, teamSlug, filters)
}
