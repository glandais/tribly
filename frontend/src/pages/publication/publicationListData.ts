import { useCallback, useMemo } from 'react'
import type { QueryClient } from '@tanstack/react-query'
import { useGetTeam, prefetchGetTeamQuery } from '@/api/endpoints/teams/teams'
import {
  useListPublications,
  listPublications,
  getListPublicationsQueryKey,
  prefetchListPublicationsQuery,
} from '@/api/endpoints/publications/publications'
import { usePaginatedQuery } from '@/hooks/usePaginatedQuery'
import { useUrlFilters, readUrlFilters } from '@/hooks/useUrlFilters'
import {
  teamPublicationFiltersSchema,
  teamPublicationFiltersAlias,
  teamPublicationApiParams,
  publicationFilterToType,
  type TeamPublicationFilters,
} from '@/hooks/filters/publicationFilters'
import { prefetchPageWindow, prefetchTeamTags } from '@/config/prefetchHelpers'
import { hourAlignedNowIso } from '@/utils/nowIso'

/**
 * The one description of what the team's publication feed (`team-detail`) reads, consumed two
 * ways: `PublicationListPage` calls {@link usePublicationListData} for the query results, the
 * `team-detail` route in `routes.config.ts` calls {@link prefetchPublicationList} for the same
 * data server-side. Same contract as `pages/route/routeListData.ts` — its own module so
 * `routes.config.ts`, which imports it eagerly, doesn't pull the page (and `TeamLayout`) out of
 * its lazy chunk.
 *
 * Two things must line up byte-for-byte between the two sides: the filters, read through the same
 * schema/alias as everywhere else, and `nowIso` — both derive it via {@link hourAlignedNowIso},
 * which floors to the hour precisely so a page rendered and one prefetched a few seconds apart
 * still land on the same `from` and the same query key.
 */

/** The schema/alias pair both readers must use — the page through the URL, the prefetch through `url.searchParams`. */
export const publicationListFilterOptions = {
  schema: teamPublicationFiltersSchema,
  alias: teamPublicationFiltersAlias,
} as const

/**
 * A team list narrowed to one kind by its route — `teamRides`, `teamTrips`, the « Sorties » and
 * « Voyages » tabs (docs/LEDGER_*.md WEB-64) — rather than by `?type=`. Same feed, same query: the
 * kind only overrides the `filter` read from the URL, on both sides, so the keys still match.
 */
export type PublicationListKind = 'ride' | 'trip'

/** The URL's filters, with the route's kind in place of `?type=` on a narrowed list. */
export function withListKind(
  filters: TeamPublicationFilters,
  kind: PublicationListKind | undefined
): TeamPublicationFilters {
  return kind ? { ...filters, filter: kind } : filters
}

/**
 * The team's own publication feed: the team, the filtered page the URL asks for, and the
 * neighbouring pages `usePaginatedQuery` fetches ahead. Returns the filter state as
 * `useUrlFilters` gives it (the page reads `filters` and calls `setFilters` directly, unlike the
 * route list there's no dedicated wrapper hook here) plus the raw query results.
 */
export function usePublicationListData(teamSlug?: string, kind?: PublicationListKind) {
  const { filters: urlFilters, setFilters } = useUrlFilters(publicationListFilterOptions)
  const filters = useMemo(() => withListKind(urlFilters, kind), [urlFilters, kind])

  // Hour-aligned and frozen per mount so `from` does not change the query key on every render.
  const nowIso = useMemo(() => hourAlignedNowIso(), [])

  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })

  // `filter` is the page's own value; the API wants a PublicationType.
  const apiParams = useMemo(() => teamPublicationApiParams(filters, nowIso), [filters, nowIso])
  const publications = useListPublications(teamSlug!, apiParams, {
    query: { enabled: !!teamSlug },
  })

  const prefetchPage = useCallback(
    (page: number) => ({
      queryKey: getListPublicationsQueryKey(teamSlug!, { ...apiParams, page }),
      queryFn: () => listPublications(teamSlug!, { ...apiParams, page }),
    }),
    [teamSlug, apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: publications.data?.total ?? 0,
    prefetchPage,
  })

  return { filters, setFilters, team, publications, totalPages }
}

/**
 * Server-side counterpart of {@link usePublicationListData}: the same filters, read from the
 * request's query string instead of the router's, through the same schema and alias, and the same
 * `nowIso` derivation.
 */
export async function prefetchPublicationList(
  queryClient: QueryClient,
  teamSlug: string,
  url: URL,
  kind?: PublicationListKind
): Promise<void> {
  const filters = withListKind(readUrlFilters(url.searchParams, publicationListFilterOptions), kind)
  // The tag filter only shows on a feed narrowed to one kind (`TagFilter` in the page).
  const tagTarget = publicationFilterToType[filters.filter]
  await Promise.all([
    prefetchGetTeamQuery(queryClient, teamSlug),
    prefetchPageWindow(teamPublicationApiParams(filters, hourAlignedNowIso()), (p) =>
      prefetchListPublicationsQuery(queryClient, teamSlug, p)
    ),
    tagTarget ? prefetchTeamTags(queryClient, teamSlug, tagTarget) : undefined,
  ])
}
