import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetAdEdit, prefetchGetAdEditQuery } from '@/api/endpoints/ads/ads'

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
 * from the `teamScopedPrefetch` wrapper). Primes `prefetchGetAdEditQuery`, matching the
 * `useGetAdEdit` call the page actually reads — `routes.config.ts` used to call
 * `prefetchGetAdQuery` here, which built the `getAd` key instead of the `getAdEdit` one `EditAdPage`
 * reads, so the prefetched entry was dead weight and the form fetched again after hydration. Nothing
 * on this route reads the plain `getAd` shape (no breadcrumb here — `ad-edit`'s is static, and the
 * dynamic `ad` breadcrumb belongs to the parent `ad-detail` route, which primes it independently), so
 * there is no reason to keep both.
 */
export async function prefetchEditAdForm(
  queryClient: QueryClient,
  teamSlug: string,
  adSlug: string
): Promise<void> {
  await prefetchGetAdEditQuery(queryClient, teamSlug, adSlug)
}
