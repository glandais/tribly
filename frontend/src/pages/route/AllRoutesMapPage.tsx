import { Group, Stack } from '@mantine/core'
import { HomeLayout } from '../../components/home/HomeLayout'
import { allRoutesTilesUrl } from '../../components/map/mapConstants'
import { RoutesTileMap } from '../../components/route/RoutesTileMap'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'

export function AllRoutesMapPage() {
  return (
    <HomeLayout currentTab="routes">
      <Stack my="lg">
        <Group justify="flex-end">
          <RouteViewToggle current="map" />
        </Group>

        <RoutesTileMap tilesUrl={allRoutesTilesUrl()} />
      </Stack>
    </HomeLayout>
  )
}
