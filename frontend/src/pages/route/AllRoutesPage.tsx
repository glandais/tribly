import { Box, Group, Stack } from '@mantine/core'
import { useAllRouteListData } from './allRouteListData'
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
  const {
    filters,
    setFilters,
    setFiltersOpen,
    filtersOpen,
    handleFiltersChange,
    handlePageChange,
    hasFiltersOrSearch,
    clearFilters,
    routes,
    density,
    totalPages,
  } = useAllRouteListData()

  const { data: routesData, isLoading, isError } = routes

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
