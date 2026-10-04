import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useGetAdEdit, prefetchGetAdEditQuery, prefetchGetAdQuery } from '@/api/endpoints/ads/ads'
import { prefetchTeamTags } from '@/config/prefetchHelpers'

/**
 * The one description of what `CreateAdPage` and `EditAdPage` read, consumed two ways: the pages
 * call {@link useCreateAdFormData} / {@link useEditAdFormData} for the query results, the `ad-new`
 * and `ad-edit` routes in `routes.config.ts` call {@link prefetchCreateAdForm} /
 * {@link prefetchEditAdForm} for the same data server-side. Same shape as `pages/ride/rideFormData.ts`
 * — one module, two routes. `AdEditor`'s only data-fetching child is its `TagPicker` (the team's ad
 * tags); beyond that there is the team and, for the edit form, the ad itself.
 *
 * Its own module rather than exports of the pages: `routes.config.ts` is imported eagerly and must
 * not pull either page out of its lazy chunk.
 *
 * The team is the `teamScopedPrefetch` wrapper's, on both routes: {@link prefetchCreateAdForm} must
 * not prefetch it again, the same reasoning as `pages/team/teamAdminData.ts` (same key, twice).
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

/** Server-side counterpart of what `AdEditor` reads on a new ad: its `TagPicker`'s tags. */
export async function prefetchCreateAdForm(queryClient: QueryClient, teamSlug: string) {
  await prefetchTeamTags(queryClient, teamSlug, 'AD')
}

/**
 * Server-side counterpart of {@link useEditAdFormData}'s ad-specific data and the editor's tags
 * (the team itself comes from the `teamScopedPrefetch` wrapper).
 *
 * **Both ad shapes, and both are needed.** `getAdEdit` is what the form reads; `routes.config.ts`
 * used to prime `getAd` alone, a different key, so the prefetched entry was dead weight and the form
 * fetched again after hydration. Priming only `getAdEdit` then moved the gap rather than closing it:
 * this route's breadcrumb trail renders its **parent** `ad-detail` crumb, whose dynamic `ad` entity
 * makes `useBreadcrumbData` call `useGetAd` on every route that carries an `adSlug` — the edit page
 * included. The prefetch audit caught exactly that on the run after the first fix.
 */
export async function prefetchEditAdForm(
  queryClient: QueryClient,
  teamSlug: string,
  adSlug: string
): Promise<void> {
  await Promise.all([
    prefetchGetAdEditQuery(queryClient, teamSlug, adSlug),
    prefetchGetAdQuery(queryClient, teamSlug, adSlug),
    prefetchCreateAdForm(queryClient, teamSlug),
  ])
}
