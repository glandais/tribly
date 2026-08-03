import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'
import { useGetPage, prefetchGetPageQuery } from '@/api/endpoints/team-pages/team-pages'
import type { QueryClient } from '@tanstack/react-query'

/**
 * The one description of what the team-page screen reads, consumed two ways:
 * `TeamPageDetailPage` calls {@link useTeamPageData} for the query results, the `team-page` route
 * in `routes.config.ts` calls {@link prefetchTeamPage} for the same two calls server-side.
 * Describing it twice is what this file exists to prevent — even though there is no derivation to
 * share here, a divergence would still be silent: it just yields a different query key, so the
 * client refetches after hydration and only `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than exports of `TeamPageDetailPage.tsx`: `routes.config.ts` is imported
 * eagerly and must not pull the page out of its lazy chunk (same contract as
 * `pages/home/nextRideParams.ts` and `components/common/placeAutocompleteParams.ts`).
 */

/** Every query `TeamPageDetailPage` itself owns, returned as the raw query results. */
export function useTeamPageData(teamSlug?: string, pageSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const page = useGetPage(teamSlug!, pageSlug!, {
    query: { enabled: !!teamSlug && !!pageSlug },
  })

  return { team, page }
}

/** Server-side counterpart of {@link useTeamPageData}. */
export async function prefetchTeamPage(
  queryClient: QueryClient,
  teamSlug: string,
  pageSlug: string
): Promise<void> {
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchGetPageQuery(queryClient, teamSlug, pageSlug),
  ])
}
