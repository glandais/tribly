import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useListPages, prefetchListPagesQuery } from '@/api/endpoints/team-pages/team-pages'

/**
 * The one description of what `TeamPagesAdminPage` reads, consumed two ways: the page calls
 * {@link useTeamPagesAdminData} for the query results, the `team-admin-pages` route in
 * `routes.config.ts` calls {@link prefetchTeamPagesAdmin} for the same data server-side.
 * Describing it twice is what this file exists to prevent: a divergence doesn't break anything
 * visibly, it just yields a different query key, so the client refetches after hydration and only
 * `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than exports of the page: `routes.config.ts` is imported eagerly and must
 * not pull the page out of its lazy chunk.
 *
 * The team query itself is deliberately NOT covered here: the route is wrapped in
 * `teamScopedPrefetch` in `routes.config.ts`, which already prefetches `GET /api/teams/{slug}` (and
 * gates the whole prefetch on authentication) — the shared machinery ~15 admin routes reuse. This
 * module only adds the single call specific to the pages list, so the screen's fetch and prefetch
 * stay declared in one place even though there's just the one query.
 */

/**
 * Every query `TeamPagesAdminPage` itself owns, returned as the raw query results so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useTeamPagesAdminData(teamSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const pages = useListPages(teamSlug!, { query: { enabled: !!teamSlug } })
  return { team, pages }
}

/**
 * Server-side counterpart of {@link useTeamPagesAdminData}'s screen-specific query (the team itself
 * comes from the `teamScopedPrefetch` wrapper).
 */
export async function prefetchTeamPagesAdmin(queryClient: QueryClient, teamSlug: string) {
  await prefetchListPagesQuery(queryClient, teamSlug)
}
