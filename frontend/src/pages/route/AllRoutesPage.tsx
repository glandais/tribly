import { useCallback } from 'react'
import { keepPreviousData } from '@tanstack/react-query'
import { Stack } from '@mantine/core'
import {
  useListAllRoutes,
  listAllRoutes,
  getListAllRoutesQueryKey,
} from '@/api/endpoints/routes/routes'
import type { ListAllRoutesParams } from '@/api/dto'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useRouteFilters } from '../../hooks/useRouteFilters'
import { HomeLayout } from '../../components/home/HomeLayout'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RouteListContent } from '../../components/route/RouteListContent'

export function AllRoutesPage() {
  const {
    filters,
    filtersOpen,
    setFiltersOpen,
    handleFiltersChange,
    handlePageChange,
    hasFiltersOrSearch,
    pageSize,
  } = useRouteFilters<ListAllRoutesParams>()

  const {
    data: routesData,
    isLoading,
    isError,
  } = useListAllRoutes(filters, {
    query: { placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAllRoutesQueryKey({ ...filters, page: prefetchPageNum }),
      queryFn: () => listAllRoutes({ ...filters, page: prefetchPageNum }),
    }),
    [filters]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page ?? 0,
    pageSize,
    totalItems: routesData?.total ?? 0,
    prefetchPage,
  })

  return (
    <HomeLayout currentTab="routes">
      <Stack my="lg">
        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        <RouteListContent
          routes={routesData?.routes}
          isLoading={isLoading}
          isError={isError}
          showTeam={true}
          hasFiltersOrSearch={hasFiltersOrSearch}
          currentPage={filters.page ?? 0}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </Stack>
    </HomeLayout>
  )
}
