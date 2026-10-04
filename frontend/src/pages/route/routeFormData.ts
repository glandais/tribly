import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetRoute, prefetchGetRouteQuery } from '@/api/endpoints/routes/routes'
import { prefetchTeamTags } from '@/config/prefetchHelpers'

/**
 * The one description of what `CreateRoutePage` and `EditRoutePage` read, consumed two ways: the
 * pages call {@link useCreateRouteFormData} / {@link useEditRouteFormData} for the query results,
 * the `route-new` and `route-edit` routes in `routes.config.ts` prefetch the same data server-side.
 * Same contract as `pages/ride/rideFormData.ts` — its own module so `routes.config.ts`, which
 * imports it eagerly, doesn't pull either page (or its lazy chunk, `RouteEditor` in particular) out
 * of its bundle.
 *
 * The team query itself is deliberately NOT covered here: both routes are wrapped in
 * `teamScopedPrefetch` in `routes.config.ts`, which already prefetches `GET /api/teams/{slug}` (and
 * gates the whole prefetch on authentication) — the shared machinery ~15 admin routes reuse. This
 * module only adds what's specific to each form.
 */

/**
 * `CreateRoutePage` reads the team itself — the form starts from an empty route, no GPX or points
 * to preload — and `RouteEditor`'s `TagPicker` reads the team's route tags. The team is covered
 * server-side by the `teamScopedPrefetch` wrapper on `route-new` in `routes.config.ts`;
 * {@link prefetchCreateRouteForm} adds the tags only, since prefetching the team again would fetch
 * it a second time under the same key.
 */
export function useCreateRouteFormData(teamSlug?: string) {
  return useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
}

/**
 * Every query `EditRoutePage` itself owns, returned as the raw query results so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useEditRouteFormData(teamSlug?: string, routeSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const route = useGetRoute(teamSlug!, routeSlug!, {
    query: { enabled: !!teamSlug && !!routeSlug },
  })
  return { team, route }
}

/** Server-side counterpart of what `RouteEditor` reads on a new route: its `TagPicker`'s tags. */
export async function prefetchCreateRouteForm(queryClient: QueryClient, teamSlug: string) {
  await prefetchTeamTags(queryClient, teamSlug, 'ROUTE')
}

/**
 * Server-side counterpart of {@link useEditRouteFormData}'s route-specific query and the editor's
 * tags (the team itself comes from the `teamScopedPrefetch` wrapper).
 */
export async function prefetchEditRouteForm(
  queryClient: QueryClient,
  teamSlug: string,
  routeSlug: string
) {
  await Promise.all([
    prefetchGetRouteQuery(queryClient, teamSlug, routeSlug),
    prefetchCreateRouteForm(queryClient, teamSlug),
  ])
}
