import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetRide, prefetchGetRideQuery, getGetRideQueryKey } from '@/api/endpoints/rides/rides'
import { prefetchListPlacesQuery, prefetchGetPlaceQuery } from '@/api/endpoints/places/places'
import { placeAutocompleteParams } from '@/components/common/placeAutocompleteParams'
import { prefetchRoutesBulkChunked } from '@/config/prefetchHelpers'
import type { RideDto } from '@/api/dto'

/**
 * The one description of what `CreateRidePage` and `EditRidePage` read, consumed two ways: the
 * pages call {@link useCreateRideFormData} / {@link useEditRideFormData} for the query results,
 * the `ride-new` and `ride-edit` routes in `routes.config.ts` call {@link prefetchRideFormPlaces}
 * / {@link prefetchEditRideForm} for the same data server-side. Describing it twice is what this
 * file exists to prevent: a divergence doesn't break anything visibly, it just yields a different
 * query key, so the client refetches after hydration and only `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than exports of the pages: `routes.config.ts` is imported eagerly and must
 * not pull either page out of its lazy chunk.
 *
 * The team query itself is deliberately NOT covered here: both routes are wrapped in
 * `teamScopedPrefetch` in `routes.config.ts`, which already prefetches `GET /api/teams/{slug}` (and
 * gates the whole prefetch on authentication) — the shared machinery ~15 admin routes reuse. This
 * module only adds what's specific to the ride form itself.
 */

/**
 * The two `PlaceAutocomplete` fields a ride form mounts (start and end), each querying its own
 * filtered place list before the visitor touches anything.
 *
 * Moved here verbatim from `routes.config.ts`, where it lived as a private helper — both `ride-new`
 * and `ride-edit` need it, and a screen-specific prefetch helper belongs next to the page it serves,
 * not in the route table.
 */
export async function prefetchRideFormPlaces(queryClient: QueryClient, teamSlug: string) {
  await Promise.all([
    prefetchListPlacesQuery(queryClient, teamSlug, placeAutocompleteParams({ filterStart: true })),
    prefetchListPlacesQuery(queryClient, teamSlug, placeAutocompleteParams({ filterEnd: true })),
  ])
}

/**
 * The places the edit form already has selected — `RideEditor` seeds `startPlaceId`/`endPlaceId`
 * from the ride, and each `PlaceAutocomplete` looks its own up by id to render a name rather than
 * an id. Nothing to open, nothing to click: both fire on the first paint.
 */
export function rideFormPlaceIds(ride: RideDto | undefined): string[] {
  return [ride?.startPlace?.id, ride?.endPlace?.id].filter((id): id is string => !!id)
}

/**
 * The routes the edit form summarises, one row per group — `RideEditor`'s own `useRoutesBulk`,
 * deduped and **sorted** because the array goes into the query key.
 *
 * Deliberately not `rideRouteSlugs` from `rideDetailData.ts`: the editor lists each group's own
 * route with no fallback to the ride's, and asks for `geometry: false` (only the name, distance and
 * elevation gain are shown). Both differences change the key — the same slugs with geometry would
 * prime an entry this screen never reads.
 */
export function rideFormGroupRouteSlugs(ride: RideDto | undefined): string[] {
  const slugs = (ride?.groups ?? []).map((g) => g.routeSlug).filter((s): s is string => !!s)
  return Array.from(new Set(slugs)).sort()
}

/**
 * Every query `CreateRidePage` itself owns, returned as the raw query result so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useCreateRideFormData(teamSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  return { team }
}

/**
 * Server-side counterpart of {@link useCreateRideFormData}'s ride-form-specific data (the team
 * itself comes from the `teamScopedPrefetch` wrapper). Covers more than the hook: the two
 * `PlaceAutocomplete` fields are queried by `RideEditor`'s children, not by `CreateRidePage` itself.
 */
export async function prefetchCreateRideForm(queryClient: QueryClient, teamSlug: string) {
  await prefetchRideFormPlaces(queryClient, teamSlug)
}

/**
 * Every query `EditRidePage` itself owns, returned as the raw query results so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useEditRideFormData(teamSlug?: string, rideSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const ride = useGetRide(teamSlug!, rideSlug!, {
    query: { enabled: !!teamSlug && !!rideSlug },
  })
  return { team, ride }
}

/**
 * Server-side counterpart of {@link useEditRideFormData}'s ride-form-specific data (the team itself
 * comes from the `teamScopedPrefetch` wrapper). Covers more than the hook the same way
 * {@link prefetchCreateRideForm} does: everything `RideEditor` and its children read on the first
 * paint, on top of the ride itself which `EditRidePage` does own.
 *
 * Two phases, because the second depends on the first: the form's current selections — the two
 * chosen places, and each group's route — are only knowable once the ride is in cache. They are
 * what the crawler reported on `rideEdit` after the pickers were gated: not lists waiting for a
 * click, but the values the form renders straight away.
 */
export async function prefetchEditRideForm(
  queryClient: QueryClient,
  teamSlug: string,
  rideSlug: string
) {
  await Promise.all([
    prefetchGetRideQuery(queryClient, teamSlug, rideSlug),
    prefetchRideFormPlaces(queryClient, teamSlug),
  ])

  const ride = queryClient.getQueryData<RideDto>(getGetRideQueryKey(teamSlug, rideSlug))
  await Promise.all([
    ...rideFormPlaceIds(ride).map((placeId) =>
      prefetchGetPlaceQuery(queryClient, teamSlug, placeId)
    ),
    prefetchRoutesBulkChunked(queryClient, teamSlug, rideFormGroupRouteSlugs(ride), {
      geometry: false,
    }),
  ])
}
