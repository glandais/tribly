import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetTrip, prefetchGetTripQuery } from '@/api/endpoints/trips/trips'
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
 * Server-side counterpart of {@link useEditTripFormData}'s trip-form-specific data (the team itself
 * comes from the `teamScopedPrefetch` wrapper).
 */
export async function prefetchEditTripForm(
  queryClient: QueryClient,
  teamSlug: string,
  tripSlug: string
) {
  await prefetchGetTripQuery(queryClient, teamSlug, tripSlug)
}
