import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetTrip, prefetchGetTripQuery, getGetTripQueryKey } from '@/api/endpoints/trips/trips'
import { prefetchRoutesBulkChunked } from '@/config/prefetchHelpers'
import type { TripDto } from '@/api/dto'
import type { QueryClient } from '@tanstack/react-query'

/**
 * The one description of what `CreateTripPage` and `EditTripPage` read, consumed two ways: the
 * pages call {@link useCreateTripFormData} / {@link useEditTripFormData} for the query results,
 * the `trip-new` and `trip-edit` routes in `routes.config.ts` call {@link prefetchEditTripForm} for
 * the same data server-side. Describing it twice is what this file exists to prevent: a divergence
 * doesn't break anything visibly, it just yields a different query key, so the client refetches
 * after hydration and only `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than exports of the pages: `routes.config.ts` is imported eagerly and must
 * not pull either page out of its lazy chunk.
 *
 * The team query itself is deliberately NOT covered here: both routes are wrapped in
 * `teamScopedPrefetch` in `routes.config.ts`, which already prefetches `GET /api/teams/{slug}` (and
 * gates the whole prefetch on authentication) — the shared machinery ~15 admin routes reuse.
 *
 * `trip-new` reads nothing beyond the team: `CreateTripPage` mounts no other query, so there is no
 * `prefetchCreateTripForm` here — the route stays a bare `teamScopedPrefetch()` in
 * `routes.config.ts`, same as the team-only special case. Only `trip-edit` adds the trip itself.
 */

/**
 * Every query `CreateTripPage` itself owns, returned as the raw query result so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useCreateTripFormData(teamSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  return { team }
}

/**
 * Every query `EditTripPage` itself owns, returned as the raw query results so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useEditTripFormData(teamSlug?: string, tripSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const trip = useGetTrip(teamSlug!, tripSlug!, {
    query: { enabled: !!teamSlug && !!tripSlug },
  })
  return { team, trip }
}

/**
 * The routes the edit form summarises, one row per stage — `TripEditor`'s own `useRoutesBulk`,
 * deduped and **sorted** because the array goes into the query key.
 *
 * Read off `stage.route.slug` (the resolved `RouteDto` a `TripStageDto` carries), not off a
 * `routeSlug` field: that one only exists on the `StageRequest` the form edits, projected by
 * `EditTripPage`. `geometry: false` for the same reason as the ride form — the stage rows show only
 * name, distance and elevation gain, and geometry would change the key anyway.
 */
export function tripFormStageRouteSlugs(trip: TripDto | undefined): string[] {
  const slugs = (trip?.stages ?? []).map((s) => s.route?.slug).filter((s): s is string => !!s)
  return Array.from(new Set(slugs)).sort()
}

/**
 * Server-side counterpart of {@link useEditTripFormData}'s trip-form-specific data (the team itself
 * comes from the `teamScopedPrefetch` wrapper).
 *
 * Two phases, because the second depends on the first: the stages — and so the routes they point at
 * — are only knowable once the trip is in cache. This is the gap `scripts/ssr-audit.mjs` reported on
 * `tripEdit` once `routes-ssr.yml` was pointed at a trip whose stages actually have routes; before
 * that the query never fired and prefetching it on the symmetry with `rideEdit` would have primed a
 * key nobody read.
 *
 * `TripEditor`'s two `PlaceAutocomplete` fields per stage are deliberately NOT covered: the crawler
 * has never seen them query on the first paint. Add them if and when a report names them.
 */
export async function prefetchEditTripForm(
  queryClient: QueryClient,
  teamSlug: string,
  tripSlug: string
) {
  await prefetchGetTripQuery(queryClient, teamSlug, tripSlug)

  const trip = queryClient.getQueryData<TripDto>(getGetTripQueryKey(teamSlug, tripSlug))
  await prefetchRoutesBulkChunked(queryClient, teamSlug, tripFormStageRouteSlugs(trip), {
    geometry: false,
  })
}
