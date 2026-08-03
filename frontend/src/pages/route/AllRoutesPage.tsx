import { useCallback, useMemo } from 'react'
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
  resolveRouteDensity,
  allRouteFiltersAlias,
  allRouteFiltersAlwaysSerialize,
  allRouteApiParams,
} from '../../hooks/filters/routeFilters'
import { isSingleTeam } from '../../config/appConfig'
import { MembershipSelect } from '../../components/common/MembershipSelect'
import { HomeLayout } from '../../components/home/HomeLayout'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RouteListContent } from '../../components/route/RouteListContent'
import { ResultCount } from '@/components/common/ResultCount'
import { RouteDensityToggle } from '@/components/route/RouteDensityToggle'
import { RouteDeadEnd } from '@/components/route/RouteDeadEnd'
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
    clearFilters,
    pageSize,
  } = useRouteFilters({
    schema,
    alias: allRouteFiltersAlias,
    alwaysSerialize: allRouteFiltersAlwaysSerialize,
  })

  const apiParams = useMemo(() => allRouteApiParams(filters), [filters])

  const {
    data: routesData,
    isLoading,
    isError,
  } = useListAllRoutes(apiParams, {
    query: { placeholderData: keepPreviousData },
  })

  const density = resolveRouteDensity(filters.density, routesData?.total)

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

        <Group justify="space-between" align="center">
          <ResultCount total={routesData?.total} resource="routes" />
          <RouteDensityToggle
            value={density}
            onChange={(value) => setFilters({ density: value })}
          />
        </Group>

        <RouteListContent
          density={density}
          deadEnd={
            <RouteDeadEnd
              filters={filters}

              onSetFilters={setFilters}
              onClearFilters={clearFilters}
            />
          }
          routes={routesData?.routes}
          isLoading={isLoading}
          isError={isError}
          showTeam={!isSingleTeam()}
          hasFiltersOrSearch={hasFiltersOrSearch}
          currentPage={filters.page}
          totalPages={totalPages}
          onPageChange={handlePageChange}
          onClearFilters={clearFilters}
        />
      </Stack>
    </HomeLayout>
  )
}
