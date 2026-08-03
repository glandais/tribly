import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetRide, prefetchGetRideQuery } from '@/api/endpoints/rides/rides'
import { prefetchListPlacesQuery } from '@/api/endpoints/places/places'
import { placeAutocompleteParams } from '@/components/common/placeAutocompleteParams'

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
 * {@link prefetchCreateRideForm} does: the ride-form's two `PlaceAutocomplete` fields, on top of the
 * ride itself which `EditRidePage` does own.
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
}
