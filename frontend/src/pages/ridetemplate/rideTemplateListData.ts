import { useCallback } from 'react'
import type { QueryClient } from '@tanstack/react-query'
import {
  useListTemplates,
  listTemplates,
  getListTemplatesQueryKey,
  prefetchListTemplatesQuery,
} from '@/api/endpoints/ride-templates/ride-templates'
import { usePaginatedQuery } from '@/hooks/usePaginatedQuery'
import { useUrlFilters } from '@/hooks/useUrlFilters'
import {
  rideTemplateFiltersSchema,
  rideTemplateFiltersAlias,
} from '@/hooks/filters/rideTemplateFilters'

/**
 * The one description of what the team's ride-template list reads, consumed two ways:
 * `RideTemplateListPage` calls {@link useRideTemplateListData} for the query results, the
 * `ride-templates` route in `routes.config.ts` calls {@link prefetchRideTemplateList} for the same
 * data server-side (wrapped in `teamScopedPrefetch`, which already covers the team itself). Same
 * contract as `pages/route/routeListData.ts` — its own module so `routes.config.ts`, which imports
 * it eagerly, doesn't pull the page out of its lazy chunk.
 *
 * `rideTemplateFiltersSchema` maps directly onto `ListTemplatesParams` (search/page/size) — no
 * separate params builder, unlike the route list's `routeApiParams`.
 *
 * Known gap, not fixed here: the prefetch primes `rideTemplateFiltersSchema.parse({})` — the
 * *default* filters — rather than reading the request's query string, so it only covers the
 * default list; a filtered URL (`?q=…&p=2`) refetches on the client after hydration. It also
 * prefetches only the requested page, not the neighbours `usePaginatedQuery` fetches ahead
 * client-side (unlike `pages/route/routeListData.ts`'s `prefetchPageWindow`) — matches today's
 * behaviour, not flagged as a fix here.
 */

/** The schema/alias pair the page reads the URL through. */
export const rideTemplateListFilterOptions = {
  schema: rideTemplateFiltersSchema,
  alias: rideTemplateFiltersAlias,
} as const

/**
 * The team's ride-template list: the filtered page the URL asks for, plus the neighbouring pages
 * `usePaginatedQuery` fetches ahead. Returns the filter state as `useUrlFilters` gives it (the page
 * needs `setFilters` for the search box and pagination) plus the raw query results.
 */
export function useRideTemplateListData(teamSlug?: string) {
  const filterState = useUrlFilters(rideTemplateListFilterOptions)
  const { filters } = filterState

  const templates = useListTemplates(teamSlug!, filters)

  // The neighbouring pages, on the same key `prefetchPageWindow` primes server-side.
  const prefetchPage = useCallback(
    (page: number) => ({
      queryKey: getListTemplatesQueryKey(teamSlug!, { ...filters, page }),
      queryFn: () => listTemplates(teamSlug!, { ...filters, page }),
    }),
    [teamSlug, filters]
  )
  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: templates.data?.total ?? 0,
    prefetchPage,
  })

  return { ...filterState, templates, totalPages }
}

/**
 * Server-side counterpart of {@link useRideTemplateListData}. Prefetches the *default* filters
 * (see docblock above) — not the request's query string — matching the route's current
 * `rideTemplateFiltersSchema.parse({})` call.
 */
export async function prefetchRideTemplateList(
  queryClient: QueryClient,
  teamSlug: string
): Promise<void> {
  const filters = rideTemplateFiltersSchema.parse({})
  await prefetchListTemplatesQuery(queryClient, teamSlug, filters)
}
