import { useCallback } from 'react'
import { keepPreviousData } from '@tanstack/react-query'
import type { QueryClient } from '@tanstack/react-query'
import {
  useListMyPreviews,
  listMyPreviews,
  getListMyPreviewsQueryKey,
  prefetchListMyPreviewsQuery,
} from '@/api/endpoints/gpx-previews/gpx-previews'
import { usePaginatedQuery } from '@/hooks/usePaginatedQuery'
import { readUrlFilters, useUrlFilters } from '@/hooks/useUrlFilters'
import {
  GPX_PREVIEW_PAGE_SIZE,
  gpxPreviewFiltersSchema,
  gpxPreviewFiltersAlias,
} from '@/hooks/filters/gpxPreviewFilters'
import { prefetchPageWindow } from '@/config/prefetchHelpers'
import { useAuthStore } from '@/store/authStore'

/**
 * The one description of what `MyGpxPreviewsPage` reads, consumed two ways: the page calls
 * {@link useGpxPreviewListData} for the query results, the `gpx-tools-list` route in
 * `routes.config.ts` calls {@link prefetchGpxPreviewList} for the same data server-side. Same
 * contract as `pages/route/routeListData.ts` — its own module so `routes.config.ts`, which imports
 * it eagerly, doesn't pull the page out of its lazy chunk.
 *
 * The list is paginated through the query string (`useUrlFilters`), so both sides must read it
 * through the same schema/alias and land on the same `size` — the page never hand-writes `12`.
 *
 * Authenticated, not team-scoped: the endpoint returns the signed-in user's own previews, so the
 * prefetch gates on `useAuthStore.getState().isAuthenticated` (the server has no hook — see
 * `pages/auth/profileData.ts`) instead of a `teamScopedPrefetch` wrapper.
 */

/** The schema/alias pair both readers must use — the page through the URL, the prefetch through `url.searchParams`. */
export const gpxPreviewListFilterOptions = {
  schema: gpxPreviewFiltersSchema,
  alias: gpxPreviewFiltersAlias,
} as const

/**
 * The signed-in user's previews: the filtered page the URL asks for, plus the neighbouring pages
 * `usePaginatedQuery` fetches ahead. Returns the filter state as `useUrlFilters` gives it (the
 * page needs `setFilters` for its `<Pagination>`) plus the raw query result.
 */
export function useGpxPreviewListData() {
  const { filters, setFilters } = useUrlFilters(gpxPreviewListFilterOptions)

  const previews = useListMyPreviews(filters, {
    query: { placeholderData: keepPreviousData },
  })

  // The neighbouring pages, on the same key `prefetchPageWindow` primes server-side.
  const prefetchPage = useCallback(
    (page: number) => ({
      queryKey: getListMyPreviewsQueryKey({ ...filters, page }),
      queryFn: () => listMyPreviews({ ...filters, page }),
    }),
    [filters]
  )
  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: GPX_PREVIEW_PAGE_SIZE,
    totalItems: previews.data?.total ?? 0,
    prefetchPage,
  })

  return { filters, setFilters, previews, totalPages }
}

/**
 * Server-side counterpart of {@link useGpxPreviewListData}: the same filters, read from the
 * request's query string instead of the router's, through the same schema and alias, then the same
 * page-window fetch `usePaginatedQuery` performs on the client.
 */
export async function prefetchGpxPreviewList(queryClient: QueryClient, url: URL): Promise<void> {
  if (!useAuthStore.getState().isAuthenticated) return
  const filters = readUrlFilters(url.searchParams, gpxPreviewListFilterOptions)
  await prefetchPageWindow(filters, (p) => prefetchListMyPreviewsQuery(queryClient, p))
}
