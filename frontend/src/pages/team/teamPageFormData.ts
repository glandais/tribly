import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetPage, prefetchGetPageQuery } from '@/api/endpoints/team-pages/team-pages'

/**
 * The one description of what `CreateTeamPagePage` and `EditTeamPagePage` read, consumed two ways:
 * the pages call {@link useCreateTeamPageFormData} / {@link useEditTeamPageFormData} for the query
 * results, the `team-admin-page-edit` route in `routes.config.ts` calls
 * {@link prefetchEditTeamPageForm} for the same data server-side. Describing it twice is what this
 * file exists to prevent: a divergence doesn't break anything visibly, it just yields a different
 * query key, so the client refetches after hydration and only `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than exports of the pages: `routes.config.ts` is imported eagerly and must
 * not pull either page out of its lazy chunk.
 *
 * The team query itself is deliberately NOT covered here: both routes are wrapped in
 * `teamScopedPrefetch` in `routes.config.ts`, which already prefetches `GET /api/teams/{slug}` (and
 * gates the whole prefetch on authentication) — the shared machinery ~15 admin routes reuse.
 *
 * `team-admin-page-new` has no `prefetch<Screen>` function here: beyond the team, which
 * `teamScopedPrefetch` already covers, `CreateTeamPagePage` and the `TeamPageForm` it mounts read
 * nothing else — no `PlaceAutocomplete`-style child query to add. Its route entry stays a bare
 * `teamScopedPrefetch()` in `routes.config.ts`. The hook lives here anyway, next to its edit
 * sibling, so both routes' fetch and prefetch stay declared in one place.
 */

/**
 * Every query `CreateTeamPagePage` itself owns, returned as the raw query result so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useCreateTeamPageFormData(teamSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  return { team }
}

/**
 * Every query `EditTeamPagePage` itself owns, returned as the raw query results so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useEditTeamPageFormData(teamSlug?: string, pageSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const page = useGetPage(teamSlug!, pageSlug!, {
    query: { enabled: !!teamSlug && !!pageSlug },
  })
  return { team, page }
}

/**
 * Server-side counterpart of {@link useEditTeamPageFormData}'s screen-specific query (the team
 * itself comes from the `teamScopedPrefetch` wrapper).
 */
export async function prefetchEditTeamPageForm(
  queryClient: QueryClient,
  teamSlug: string,
  pageSlug: string
) {
  await prefetchGetPageQuery(queryClient, teamSlug, pageSlug)
}
