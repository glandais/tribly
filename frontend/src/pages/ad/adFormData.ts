import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetAdEdit, prefetchGetAdQuery } from '@/api/endpoints/ads/ads'

/**
 * The one description of what `CreateAdPage` and `EditAdPage` read, consumed two ways: the pages
 * call {@link useCreateAdFormData} / {@link useEditAdFormData} for the query results, the `ad-new`
 * and `ad-edit` routes in `routes.config.ts` call `teamScopedPrefetch()` /
 * {@link prefetchEditAdForm} for the same data server-side. Same shape as `pages/ride/rideFormData.ts`
 * — one module, two routes — because `AdEditor` (unlike `RideEditor`) mounts no data-fetching
 * children of its own: there is nothing beyond the team and, for the edit form, the ad itself.
 *
 * Its own module rather than exports of the pages: `routes.config.ts` is imported eagerly and must
 * not pull either page out of its lazy chunk.
 *
 * `CreateAdPage` reads nothing beyond the team, already covered by the bare `teamScopedPrefetch()`
 * on `ad-new` in `routes.config.ts` — so there is no `prefetchCreateAdForm` here, the same reasoning
 * as `pages/team/teamAdminData.ts`: adding one would prefetch the team a second time under the same
 * key.
 */

/**
 * Every query `CreateAdPage` itself owns, returned as the raw query result so the page keeps
 * reading `.data` / `.isLoading` directly.
 */
export function useCreateAdFormData(teamSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  return { team }
}

/**
 * Every query `EditAdPage` itself owns, returned as the raw query results so the page keeps
 * reading `.data` / `.isLoading` directly.
 *
 * Note: the page reads the ad through `useGetAdEdit` (the edit-scoped shape), not `useGetAd`.
 */
export function useEditAdFormData(teamSlug?: string, adSlug?: string) {
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })
  const ad = useGetAdEdit(teamSlug!, adSlug!, {
    query: { enabled: !!teamSlug && !!adSlug },
  })
  return { team, ad }
}

/**
 * Server-side counterpart of {@link useEditAdFormData}'s ad-specific data (the team itself comes
 * from the `teamScopedPrefetch` wrapper). Moved here verbatim from `routes.config.ts`, which called
 * `prefetchGetAdQuery` — not `prefetchGetAdEditQuery`, even though the page reads `useGetAdEdit`.
 * Kept as-is to preserve existing behaviour; the mismatch predates this module and is a candidate
 * fix on its own, not something to change silently here.
 */
export async function prefetchEditAdForm(
  queryClient: QueryClient,
  teamSlug: string,
  adSlug: string
): Promise<void> {
  await prefetchGetAdQuery(queryClient, teamSlug, adSlug)
}
