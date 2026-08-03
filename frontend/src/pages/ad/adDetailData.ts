import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'
import { useGetAd, prefetchGetAdQuery } from '@/api/endpoints/ads/ads'

/**
 * The one description of what `AdDetailPage` reads, consumed two ways: the page calls
 * {@link useAdDetailData} for the query results, the `ad-detail` route in `routes.config.ts` calls
 * {@link prefetchAdDetail} for the same data server-side. Describing it twice is what this file
 * exists to prevent: a divergence doesn't break anything visibly, it just yields a different query
 * key, so the client refetches after hydration and only `scripts/ssr-audit.mjs` notices.
 *
 * Its own module rather than an export of the page: `routes.config.ts` is imported eagerly and must
 * not pull the page out of its lazy chunk.
 *
 * `ad-detail` is not wrapped in `teamScopedPrefetch` — unlike the admin screens, it prefetches the
 * team itself directly, matching {@link useAdDetailData}'s own `useGetTeam` call.
 *
 * An ad's position renders as a sector, never a pin (`AdLocationMap`) — this module fetches the
 * `AdDto` the map reads `locationGeometry` from, it does not touch how that geometry is rendered.
 */

/**
 * Every query `AdDetailPage` itself owns, returned as the raw query results so the page keeps
 * reading `.data` / `.isLoading` / `.error` / `.refetch` directly.
 */
export function useAdDetailData(teamSlug?: string, adSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const ad = useGetAd(teamSlug!, adSlug!, { query: { enabled: !!teamSlug && !!adSlug } })
  return { team, ad }
}

/**
 * Server-side counterpart of {@link useAdDetailData}.
 */
export async function prefetchAdDetail(
  queryClient: QueryClient,
  teamSlug: string,
  adSlug: string
): Promise<void> {
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchGetAdQuery(queryClient, teamSlug, adSlug),
  ])
}
