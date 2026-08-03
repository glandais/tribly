import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { Layer, Popup, Source } from 'react-map-gl/maplibre'
import type { MapLayerMouseEvent, MapRef } from 'react-map-gl/maplibre'
import { Anchor, Box, Group, Stack, Text } from '@mantine/core'
import type { BoundsDto } from '@/api/dto'
import { paths } from '@/config/paths'
import { useMapHeight } from '@/hooks/useResponsive'
import { useUnits } from '@/hooks/useUnits'
import { useResolvedColorScheme } from '@/hooks/useResolvedColorScheme'
import { PedalonsMap } from '../map/PedalonsMap'
import {
  DEFAULT_MAP_VIEW,
  ROUTES_FIT_OPTIONS,
  ROUTES_SOURCE_LAYER,
  ROUTE_LINE_COLOR,
  ROUTE_LINE_HOVER_COLOR,
} from '../map/mapConstants'

const SOURCE_ID = 'routes-tiles'
const LINES_LAYER = 'routes-lines'
/** Wide transparent copy of the lines: thin tracks are near impossible to click otherwise. */
const HIT_LAYER = 'routes-hit'

/** Feature properties encoded in the tile by ST_AsMVT (see `route_mvt_row`). */
interface RouteFeature {
  slug: string
  name: string
  team_slug: string
  distance: number | null
  elevation_gain: number | null
}

interface SelectedRoute extends RouteFeature {
  longitude: number
  latitude: number
}

const featureOf = (event: MapLayerMouseEvent): RouteFeature | null => {
  const properties = event.features?.[0]?.properties
  return properties ? (properties as unknown as RouteFeature) : null
}

export interface RoutesTileMapProps {
  tilesUrl: string
  /**
   * Extent to open on. Applied once per mount: refraiming under the fingers of someone who is
   * narrowing the filters would be disorienting, so a later change is ignored.
   */
  bounds?: BoundsDto
}

export function RoutesTileMap({ tilesUrl, bounds }: RoutesTileMapProps) {
  const { distance, elevation } = useUnits()
  const colorScheme = useResolvedColorScheme()
  // A dedicated map page can afford more room than the maps embedded in detail pages.
  const mapHeight = useMapHeight('fullscreen')

  const [hoveredSlug, setHoveredSlug] = useState<string | null>(null)
  const [selected, setSelected] = useState<SelectedRoute | null>(null)

  const mapRef = useRef<MapRef>(null)
  const [mapLoaded, setMapLoaded] = useState(false)
  const fittedRef = useRef(false)
  const handleLoad = useCallback(() => setMapLoaded(true), [])

  // The extent and the map are loaded by two independent races, either of which may finish first.
  useEffect(() => {
    if (!mapLoaded || !bounds || fittedRef.current) return
    fittedRef.current = true
    mapRef.current?.fitBounds(
      [
        [bounds.minLon, bounds.minLat],
        [bounds.maxLon, bounds.maxLat],
      ],
      ROUTES_FIT_OPTIONS
    )
  }, [mapLoaded, bounds])

  const tiles = useMemo(() => [tilesUrl], [tilesUrl])

  const handleMouseMove = useCallback((event: MapLayerMouseEvent) => {
    setHoveredSlug(featureOf(event)?.slug ?? null)
  }, [])

  const handleMouseLeave = useCallback(() => setHoveredSlug(null), [])

  const handleClick = useCallback((event: MapLayerMouseEvent) => {
    const feature = featureOf(event)
    setSelected(
      feature ? { ...feature, longitude: event.lngLat.lng, latitude: event.lngLat.lat } : null
    )
  }, [])

  return (
    <Box
      pos="relative"
      w="100%"
      h={mapHeight}
      className={colorScheme === 'dark' ? 'dark' : undefined}
      style={{
        zIndex: 0,
        border: '1px solid var(--mantine-color-default-border)',
        borderRadius: 'var(--mantine-radius-sm)',
        overflow: 'hidden',
      }}
    >
      <PedalonsMap
        ref={mapRef}
        initialViewState={DEFAULT_MAP_VIEW}
        cursor={hoveredSlug ? 'pointer' : 'grab'}
        interactiveLayerIds={[HIT_LAYER]}
        onLoad={handleLoad}
        onMouseMove={handleMouseMove}
        onMouseLeave={handleMouseLeave}
        onClick={handleClick}
      >
        <Source id={SOURCE_ID} type="vector" tiles={tiles} minzoom={0} maxzoom={14}>
          <Layer
            id={LINES_LAYER}
            type="line"
            source-layer={ROUTES_SOURCE_LAYER}
            layout={{ 'line-cap': 'round', 'line-join': 'round' }}
            paint={{
              'line-color': [
                'case',
                ['==', ['get', 'slug'], hoveredSlug ?? ''],
                ROUTE_LINE_HOVER_COLOR,
                ROUTE_LINE_COLOR,
              ],
              'line-opacity': 0.75,
              'line-width': ['interpolate', ['linear'], ['zoom'], 5, 1, 10, 2.5, 14, 4],
            }}
          />
          <Layer
            id={HIT_LAYER}
            type="line"
            source-layer={ROUTES_SOURCE_LAYER}
            paint={{ 'line-color': ROUTE_LINE_COLOR, 'line-opacity': 0, 'line-width': 12 }}
          />
        </Source>

        {selected && (
          <Popup
            longitude={selected.longitude}
            latitude={selected.latitude}
            offset={12}
            closeOnClick={false}
            onClose={() => setSelected(null)}
          >
            <Stack gap={2}>
              <Anchor href={paths.route(selected.team_slug, selected.slug)} fw={600} size="sm">
                {selected.name}
              </Anchor>
              <Group gap="xs">
                {selected.distance !== null && (
                  <Text size="xs" c="dimmed">
                    {distance(selected.distance)}
                  </Text>
                )}
                {selected.elevation_gain !== null && (
                  <Text size="xs" c="dimmed">
                    {elevation(selected.elevation_gain)}
                  </Text>
                )}
              </Group>
            </Stack>
          </Popup>
        )}
      </PedalonsMap>
    </Box>
  )
}
