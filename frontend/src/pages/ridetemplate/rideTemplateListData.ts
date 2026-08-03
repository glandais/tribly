import { useCallback } from 'react'
import type { QueryClient } from '@tanstack/react-query'
import {
  useListTemplates,
  listTemplates,
  getListTemplatesQueryKey,
  prefetchListTemplatesQuery,
} from '@/api/endpoints/ride-templates/ride-templates'
import { usePaginatedQuery } from '@/hooks/usePaginatedQuery'
import { useUrlFilters, readUrlFilters } from '@/hooks/useUrlFilters'
import {
  rideTemplateFiltersSchema,
  rideTemplateFiltersAlias,
} from '@/hooks/filters/rideTemplateFilters'
import { prefetchPageWindow } from '@/config/prefetchHelpers'

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
 * The prefetch reads the request's query string through the same `rideTemplateListFilterOptions`
 * the page's `useUrlFilters` uses, via `readUrlFilters`, then primes the page the URL asks for plus
 * the neighbours `usePaginatedQuery` fetches ahead — same shape as `routeListData.ts`'s
 * `prefetchPageWindow`. A filtered URL (`?q=…&p=2`) now server-renders that list.
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
 * Server-side counterpart of {@link useRideTemplateListData}: the same filters, read from the
 * request's query string instead of the router's, through the same schema and alias.
 */
export async function prefetchRideTemplateList(
  queryClient: QueryClient,
  teamSlug: string,
  url: URL
): Promise<void> {
  const filters = readUrlFilters(url.searchParams, rideTemplateListFilterOptions)
  await prefetchPageWindow(filters, (p) => prefetchListTemplatesQuery(queryClient, teamSlug, p))
}
