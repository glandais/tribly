import type { QueryClient } from '@tanstack/react-query'
import { getGetTeamQueryKey } from '@/api/endpoints/teams/teams'
import { prefetchGetRoutesBulkQuery } from '@/api/endpoints/routes/routes'
import { ROUTES_BULK_MAX_SLUGS } from '@/hooks/useRoutesBulk'
import { COMMENT_LIST_OPTIONS } from '@/hooks/useComments'
import type { SortDirection, TeamDetailDto, GetRoutesBulkParams } from '@/api/dto'

/**
 * The prefetch primitives shared by `routes.config.ts` and the pages' own data companions (e.g.
 * `pages/ride/rideDetailData.ts`). Their own module rather than exports of `routes.config.ts`,
 * which imports those companions — the other direction would be a cycle.
 */

/**
 * Comments are member-only on rides/trips/posts/routes (each detail page's own `isMember`, from
 * the team's `role`) — resolved from the team query prefetched just before calling this, same
 * shape `useComments` builds. Skipped entirely for a non-member, matching what the page renders.
 */
export async function prefetchMemberComments(
  queryClient: QueryClient,
  teamSlug: string,
  entitySlug: string,
  listComments: (
    teamSlug: string,
    entitySlug: string,
    params: { page: number; size: number; sort: SortDirection }
  ) => Promise<unknown>,
  getQueryKey: (teamSlug: string, entitySlug: string) => readonly unknown[]
) {
  const team = queryClient.getQueryData<TeamDetailDto>(getGetTeamQueryKey(teamSlug))
  if (!team?.role) return

  await queryClient.prefetchInfiniteQuery({
    queryKey: [...getQueryKey(teamSlug, entitySlug), COMMENT_LIST_OPTIONS],
    queryFn: ({ pageParam }: { pageParam: number }) =>
      listComments(teamSlug, entitySlug, { page: pageParam, ...COMMENT_LIST_OPTIONS }),
    initialPageParam: 0,
  })
}

/** Mirrors `useRoutesBulk`'s own chunking against `ROUTES_BULK_MAX_SLUGS` so the query keys match. */
export async function prefetchRoutesBulkChunked(
  queryClient: QueryClient,
  teamSlug: string,
  slugs: string[],
  params?: Omit<GetRoutesBulkParams, 'slug'>
) {
  const dedupedSlugs = Array.from(new Set(slugs)).sort()
  if (dedupedSlugs.length === 0) return

  await Promise.all(
    Array.from({ length: Math.ceil(dedupedSlugs.length / ROUTES_BULK_MAX_SLUGS) }, (_, i) =>
      dedupedSlugs.slice(i * ROUTES_BULK_MAX_SLUGS, (i + 1) * ROUTES_BULK_MAX_SLUGS)
    ).map((slugChunk) =>
      prefetchGetRoutesBulkQuery(queryClient, teamSlug, { ...params, slug: slugChunk })
    )
  )
}
