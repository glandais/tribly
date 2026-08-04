import { Box, Group, Stack } from '@mantine/core'
import { useAllRoutesMapData } from './allRouteListData'
import { HomeLayout } from '../../components/home/HomeLayout'
import { MembershipSelect } from '../../components/common/MembershipSelect'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RoutesTileMap } from '../../components/route/RoutesTileMap'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'

export function AllRoutesMapPage() {
  const {
    filters,
    setFilters,
    filtersOpen,
    setFiltersOpen,
    handleFiltersChange,
    tilesUrl,
    bounds,
  } = useAllRoutesMapData()

  return (
    <HomeLayout currentTab="routes">
      <Stack my="lg">
        <Group justify="space-between" wrap="wrap">
          <MembershipSelect
            value={filters.membership}
            onChange={(membership) => setFilters({ membership })}
          />
          <Box ml="auto">
            <RouteViewToggle current="map" />
          </Box>
        </Group>

        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
          showSort={false}
        />

        <RoutesTileMap
          tilesUrl={tilesUrl}
          bounds={bounds.data?.bounds}
          boundsPending={bounds.isLoading}
        />
      </Stack>
    </HomeLayout>
  )
}
