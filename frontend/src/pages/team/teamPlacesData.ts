import type { QueryClient } from '@tanstack/react-query'
import { prefetchListPlacesQuery } from '@/api/endpoints/places/places'
import { placeFiltersSchema, placeFiltersAlias } from '@/hooks/filters/placeFilters'

/**
 * Server-side counterpart of the places list `PlaceList` renders under `team-admin-places`.
 * There is no `useTeamPlacesData` hook here: `TeamPlacesPage` itself owns no query — it only
 * resolves the team (via `teamScopedPrefetch`) and mounts `<PlaceList teamSlug canManage />`,
 * which reads the list directly through `useListPlaces(teamSlug, filters)` with `filters` from
 * `useUrlFilters({ schema: placeFiltersSchema, alias: placeFiltersAlias })` (`placeListFilterOptions`
 * below). Keep both sides pointed at that one export, or a filters change on one side silently
 * stops matching the other's query key.
 *
 * The prefetch only ever primes the **default** filters (`placeFiltersSchema.parse({})`) — it does
 * not read the request URL's query string. A link that adds `?search=…` or `?p=1` still renders
 * correctly, just after a client refetch instead of from the dehydrated cache. That gap is shared
 * with the other admin list screens and is left as-is here.
 */
export const placeListFilterOptions = {
  schema: placeFiltersSchema,
  alias: placeFiltersAlias,
} as const

export async function prefetchTeamPlaces(
  queryClient: QueryClient,
  teamSlug: string
): Promise<void> {
  await prefetchListPlacesQuery(queryClient, teamSlug, placeFiltersSchema.parse({}))
}
