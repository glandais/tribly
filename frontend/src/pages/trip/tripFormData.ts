import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetTrip, prefetchGetTripQuery, getGetTripQueryKey } from '@/api/endpoints/trips/trips'
import { prefetchGetRouteQuery } from '@/api/endpoints/routes/routes'
import { prefetchRoutesBulkChunked, prefetchTeamTags } from '@/config/prefetchHelpers'
import type { TripDto, TripRequest, Status } from '@/api/dto'
import type { QueryClient } from '@tanstack/react-query'

/**
 * The one description of what `CreateTripPage` and `EditTripPage` read, consumed two ways: the
 * pages call {@link useCreateTripFormData} / {@link useEditTripFormData} for the query results,
 * the `trip-new` and `trip-edit` routes in `routes.config.ts` call {@link prefetchCreateTripForm} /
 * {@link prefetchEditTripForm} for the same data server-side. Describing it twice is what this file exists to prevent: a divergence
 * doesn't break anything visibly, it just yields a different query key, so the client refetches
 * after hydration and only the prefetch audit of
 * `e2e/routes-render.e2e.ts` notices.
 *
 * Its own module rather than exports of the pages: `routes.config.ts` is imported eagerly and must
 * not pull either page out of its lazy chunk.
 *
 * The team query itself is deliberately NOT covered here: both routes are wrapped in
 * `teamScopedPrefetch` in `routes.config.ts`, which already prefetches `GET /api/teams/{slug}` (and
 * gates the whole prefetch on authentication) — the shared machinery ~15 admin routes reuse.
 *
 * Both forms mount `TripEditor`, whose `TagPicker` reads the team's trip tags on the first paint:
 * {@link prefetchCreateTripForm} covers that alone, and `trip-edit` adds the trip itself.
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
 * The trip's own route, which the edit form's details tab previews (`RoutePreview`, a single
 * `getRoute`) as soon as it is set — read off {@link tripToRequest}, the projection `EditTripPage`
 * seeds the form with. Same as the ride form's `rideFormRouteSlug`.
 */
export function tripFormRouteSlug(trip: TripDto | undefined): string | undefined {
  return trip ? tripToRequest(trip).routeSlug : undefined
}

/** Server-side counterpart of what `TripEditor` reads on a new trip: its `TagPicker`'s tags. */
export async function prefetchCreateTripForm(queryClient: QueryClient, teamSlug: string) {
  await prefetchTeamTags(queryClient, teamSlug, 'TRIP')
}

/**
 * Server-side counterpart of {@link useEditTripFormData}'s trip-form-specific data (the team itself
 * comes from the `teamScopedPrefetch` wrapper).
 *
 * Two phases, because the second depends on the first: the stages — and so the routes they point at
 * — are only knowable once the trip is in cache. This is the gap the (since retired) manual SSR
 * crawler reported on `tripEdit` once it was pointed at a trip whose stages actually have routes —
 * the prefetch audit of `e2e/routes-render.e2e.ts` now guards it, its dataset giving the trip's
 * stages routes. Before that the query never fired, and prefetching it on the symmetry with
 * `rideEdit` would have primed a key nobody read.
 *
 * The same second phase primes the trip's own route ({@link tripFormRouteSlug}), which the details
 * tab previews on the first paint — the counterpart of the ride form's gap that
 * `e2e/routes-render.e2e.ts` reported on `rideEdit`.
 *
 * `TripEditor`'s two `PlaceAutocomplete` fields per stage are deliberately NOT covered: neither the
 * retired crawler nor the prefetch audit has seen them query on the first paint. Add them if and
 * when the audit names them.
 */
export async function prefetchEditTripForm(
  queryClient: QueryClient,
  teamSlug: string,
  tripSlug: string
) {
  await Promise.all([
    prefetchGetTripQuery(queryClient, teamSlug, tripSlug),
    prefetchCreateTripForm(queryClient, teamSlug),
  ])

  const trip = queryClient.getQueryData<TripDto>(getGetTripQueryKey(teamSlug, tripSlug))
  const routeSlug = tripFormRouteSlug(trip)
  await Promise.all([
    prefetchRoutesBulkChunked(queryClient, teamSlug, tripFormStageRouteSlugs(trip), {
      geometry: false,
    }),
    routeSlug ? prefetchGetRouteQuery(queryClient, teamSlug, routeSlug) : Promise.resolve(),
  ])
}

/**
 * The `TripRequest` that rewrites `trip` as it stands. A stage comes back as a `TripStageDto`, which
 * carries the resolved `route`/`startPlace`/`endPlace` objects where a `StageRequest` wants the
 * `routeSlug`/`…PlaceId` references, and the server applies every field of a PUT. Spreading the DTO
 * as-is is the trap: it clears each stage's route and places — the edit form did it until it
 * projected them, and the publish/unpublish/cancel menu of the detail page did it after.
 */
export function tripToRequest(trip: TripDto): TripRequest {
  return {
    name: trip.name,
    media: trip.media,
    dateTime: trip.dateTime,
    status: trip.status,
    visibility: trip.visibility,
    routeSlug: trip.routeSlug,
    publishAt: trip.publishAt,
    stages: trip.stages.map((stage) => ({
      id: stage.id,
      name: stage.name,
      dateTime: stage.dateTime,
      routeSlug: stage.route?.slug,
      startPlaceId: stage.startPlace?.id,
      endPlaceId: stage.endPlace?.id,
      media: stage.media,
    })),
    tagIds: trip.tags.map((tag) => tag.id),
  }
}

/**
 * The `TripRequest` of the detail page's publish/unpublish/cancel menu: `trip` as it stands with only
 * its status changed — and no `tagIds`, which the API reads as « unchanged ». Copying the cached
 * tags instead would send back a tag deleted since the page was loaded, and the API refuses an
 * unknown tag (`TAG_INVALID`) where it only meant to change the status.
 */
export function tripStatusRequest(trip: TripDto, status: Status): TripRequest {
  return { ...tripToRequest(trip), status, tagIds: undefined }
}
