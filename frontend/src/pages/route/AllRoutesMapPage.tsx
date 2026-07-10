import { useMemo, useState } from 'react'
import { Box, Group, Stack } from '@mantine/core'
import { useGetAllRoutesBounds } from '@/api/endpoints/routes/routes'
import { HomeLayout } from '../../components/home/HomeLayout'
import { MembershipSelect } from '../../components/common/MembershipSelect'
import { allRoutesTilesUrl, toRouteTileFilters } from '../../components/map/mapConstants'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RoutesTileMap } from '../../components/route/RoutesTileMap'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'
import { membershipToMinRole } from '../../hooks/filters/membership'
import {
  makeAllRouteFiltersSchema,
  allRouteFiltersAlias,
  allRouteFiltersAlwaysSerialize,
} from '../../hooks/filters/routeFilters'
import { useMembershipDefault } from '../../hooks/useMembershipDefault'
import { useRouteFilters } from '../../hooks/useRouteFilters'

export function AllRoutesMapPage() {
  const membershipDefault = useMembershipDefault()
  const schema = useMemo(() => makeAllRouteFiltersSchema(membershipDefault), [membershipDefault])

  const { filters, setFilters, filtersOpen, setFiltersOpen, handleFiltersChange } = useRouteFilters(
    {
      schema,
      alias: allRouteFiltersAlias,
      alwaysSerialize: allRouteFiltersAlwaysSerialize,
    }
  )

  const tilesUrl = useMemo(
    () =>
      allRoutesTilesUrl({
        ...toRouteTileFilters(filters),
        minRole: membershipToMinRole[filters.membership],
      }),
    [filters]
  )

  // Frozen at mount, so narrowing the filters reframes nothing and asks the server nothing. The
  // list/map toggle is a navigation, hence a remount, hence a fresh extent.
  const [initialFilters] = useState(() => ({
    ...toRouteTileFilters(filters),
    minRole: membershipToMinRole[filters.membership],
  }))
  const { data: routeBounds } = useGetAllRoutesBounds(initialFilters)

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

        <RoutesTileMap tilesUrl={tilesUrl} bounds={routeBounds?.bounds} />
      </Stack>
    </HomeLayout>
  )
}
