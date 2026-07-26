import { useCallback, useMemo } from 'react'
import { ListViewMode } from '@/api/dto'
import { keepPreviousData } from '@tanstack/react-query'
import { Box, Group, Stack } from '@mantine/core'
import {
  useListAllRoutes,
  listAllRoutes,
  getListAllRoutesQueryKey,
} from '@/api/endpoints/routes/routes'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useRouteFilters } from '../../hooks/useRouteFilters'
import { useMembershipDefault } from '../../hooks/useMembershipDefault'
import {
  makeAllRouteFiltersSchema,
  allRouteFiltersAlias,
  allRouteFiltersAlwaysSerialize,
} from '../../hooks/filters/routeFilters'
import { membershipToMinRole } from '../../hooks/filters/membership'
import { isSingleTeam } from '../../config/appConfig'
import { MembershipSelect } from '../../components/common/MembershipSelect'
import { HomeLayout } from '../../components/home/HomeLayout'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RouteListContent } from '../../components/route/RouteListContent'
import { ResultCount } from '@/components/common/ResultCount'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'

export function AllRoutesPage() {
  const membershipDefault = useMembershipDefault()
  const schema = useMemo(() => makeAllRouteFiltersSchema(membershipDefault), [membershipDefault])

  const {
    filters,
    setFilters,
    setFiltersOpen,
    filtersOpen,
    handleFiltersChange,
    handlePageChange,
    hasFiltersOrSearch,
    pageSize,
  } = useRouteFilters({
    schema,
    alias: allRouteFiltersAlias,
    alwaysSerialize: allRouteFiltersAlwaysSerialize,
  })

  // `membership` is the page's own value; the API wants a MinRole.
  const apiParams = useMemo(() => {
    const { membership, ...rest } = filters
    return {
      ...rest,
      minRole: membershipToMinRole[membership],
      view: ListViewMode.COMPACT,
    }
  }, [filters])

  const {
    data: routesData,
    isLoading,
    isError,
  } = useListAllRoutes(apiParams, {
    query: { placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAllRoutesQueryKey({ ...apiParams, page: prefetchPageNum }),
      queryFn: () => listAllRoutes({ ...apiParams, page: prefetchPageNum }),
    }),
    [apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize,
    totalItems: routesData?.total ?? 0,
    prefetchPage,
  })

  return (
    <HomeLayout currentTab="routes">
      <Stack my="lg">
        <Group justify="space-between" wrap="wrap">
          <MembershipSelect
            value={filters.membership}
            onChange={(membership) => setFilters({ membership })}
          />
          <Box ml="auto">
            <RouteViewToggle current="list" />
          </Box>
        </Group>

        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        <ResultCount total={routesData?.total} resource="routes" />

        <RouteListContent
          routes={routesData?.routes}
          isLoading={isLoading}
          isError={isError}
          showTeam={!isSingleTeam()}
          hasFiltersOrSearch={hasFiltersOrSearch}
          currentPage={filters.page}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </Stack>
    </HomeLayout>
  )
}
