import { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import type { MapRef } from 'react-map-gl/maplibre'
import { IconArrowsMaximize } from '@tabler/icons-react'
import { ActionIcon, Box, Center, Paper, Text, useComputedColorScheme } from '@mantine/core'
import type { RouteDetailDto } from '@/api/dto'
import { useMapHeight } from '@/hooks/useResponsive'
import { getOverlayBg } from '@/lib/colors'
import { RouteTrackMap } from './RouteTrackMap'
import { ElevationChart } from './ElevationChart'

/**
 * The subset of a route these views actually render. Satisfied by both `RouteDetailDto` and
 * `GpxPreviewDto`, which is what lets the GPX tools reuse the map and elevation chart as-is.
 */
export type MappableRoute = Pick<RouteDetailDto, 'tracks' | 'waypoints' | 'distance'>

// Stable padding for the embedded layout: leave room at the bottom for the chart overlay so the
// whole track stays visible. Kept module-level so re-renders don't retrigger fit-to-bounds.
const EMBEDDED_FIT_PADDING = { top: 50, bottom: 220, left: 50, right: 50 }

interface RouteMapViewProps {
  route: MappableRoute
  /** When set, shows a fullscreen action linking to this path (built by the caller via paths.xxx). */
  fullscreenPath?: string
}

export function RouteMapView({ route, fullscreenPath }: RouteMapViewProps) {
  const { t } = useTranslation()
  const colorScheme = useComputedColorScheme('light')
  const mapHeight = useMapHeight('full')
  const [hoveredPointIndex, setHoveredPointIndex] = useState<number>(-1)
  const mapRef = useRef<MapRef>(null)

  const trackPoints = route.tracks.flatMap((track) => track.line.coordinates)

  if (trackPoints.length === 0) {
    return (
      <Center
        h={mapHeight}
        bg="var(--mantine-color-default-hover)"
        style={{ borderRadius: 'var(--mantine-radius-default)' }}
      >
        <Text c="dimmed">{t('map.noTrackData')}</Text>
      </Center>
    )
  }

  const overlayBg = getOverlayBg(colorScheme, true)

  return (
    <Paper withBorder style={{ overflow: 'hidden' }}>
      <Box w="100%" h={mapHeight}>
        <RouteTrackMap
          route={route}
          mapRef={mapRef}
          hoveredPointIndex={hoveredPointIndex}
          onHoverPoint={setHoveredPointIndex}
          fitPadding={EMBEDDED_FIT_PADDING}
        >
          {/* Fullscreen action, following the MapStyleSwitcher absolute-position pattern */}
          {fullscreenPath && (
            <Box pos="absolute" top={8} right={56} style={{ zIndex: 10 }}>
              <ActionIcon
                component={Link}
                to={fullscreenPath}
                variant="default"
                size="lg"
                radius="md"
                aria-label={t('map.fullscreen.open')}
                title={t('map.fullscreen.open')}
                style={{ boxShadow: 'var(--mantine-shadow-lg)' }}
              >
                <IconArrowsMaximize size={20} />
              </ActionIcon>
            </Box>
          )}

          {/* Elevation chart overlay */}
          <Box
            pos="absolute"
            bottom={0}
            left={0}
            right={0}
            // Proportional to the (viewport-bounded) map so it never dominates a short map,
            // while staying readable on tall ones.
            h="clamp(110px, 38%, 200px)"
            style={{
              zIndex: 1000,
              pointerEvents: 'auto',
              boxShadow: 'var(--mantine-shadow-lg)',
              backgroundColor: overlayBg,
            }}
          >
            <ElevationChart
              route={route}
              hoveredPointIndex={hoveredPointIndex}
              onHoverPoint={setHoveredPointIndex}
            />
          </Box>
        </RouteTrackMap>
      </Box>
    </Paper>
  )
}
