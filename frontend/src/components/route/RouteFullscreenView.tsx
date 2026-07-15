import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Link } from 'react-router-dom'
import { IconArrowLeft, IconArrowUp, IconArrowDown, IconRoute } from '@tabler/icons-react'
import { ActionIcon, Box, Center, Group, Text, useComputedColorScheme } from '@mantine/core'
import { getOverlayBg } from '@/lib/colors'
import { useUnits } from '@/hooks/useUnits'
import { useMapChartZoomSync } from '@/hooks/useMapChartZoomSync'
import { RouteTrackMap } from './RouteTrackMap'
import { ElevationChart } from './ElevationChart'
import type { MappableRoute } from './RouteMapView'

/** A route rich enough for the fullscreen toolbar's distance / D+ / D− readout. */
export type FullscreenRoute = MappableRoute & {
  elevationGain: number
  elevationLoss: number
}

interface RouteFullscreenViewProps {
  route: FullscreenRoute
  /** Title shown in the toolbar (the route/stage/track name). */
  title: string
  /** Locale-aware path to return to (built by the page via paths.xxx). */
  backTo: string
}

/**
 * Fullscreen route viewer à la prendslaroue: a slim toolbar, a map filling the viewport, and a
 * fixed elevation strip pinned below it. Cursor sync (map ↔ chart) plus bidirectional zoom/pan
 * sync via {@link useMapChartZoomSync}; hover updates are suppressed while either side is driving
 * the other so the crosshair doesn't jump mid-gesture.
 */
export function RouteFullscreenView({ route, title, backTo }: RouteFullscreenViewProps) {
  const { t } = useTranslation()
  const colorScheme = useComputedColorScheme('light')
  const { distance, elevation } = useUnits()
  const [hoveredPointIndex, setHoveredPointIndex] = useState<number>(-1)

  const {
    mapRef,
    chartRef,
    mapMoving,
    chartMoving,
    onMapMoveStart,
    onMapMoveEnd,
    onChartMoveStart,
    onChartMoveComplete,
  } = useMapChartZoomSync(route)

  // Ignore hover while a zoom/pan gesture is driving the counterpart view. Left unmemoized so the
  // React Compiler can optimize it (manual useCallback over ref reads is rejected by lint).
  const handleHoverPoint = (index: number) => {
    if (mapMoving.current || chartMoving.current) return
    setHoveredPointIndex(index)
  }

  const trackPoints = route.tracks.flatMap((track) => track.line.coordinates)

  if (trackPoints.length === 0) {
    return (
      <Center h="100dvh" bg="var(--mantine-color-body)">
        <Text c="dimmed">{t('map.noTrackData')}</Text>
      </Center>
    )
  }

  const stripBg = getOverlayBg(colorScheme, true)

  return (
    <Box h="100dvh" style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
      {/* Toolbar */}
      <Group
        h={48}
        px="sm"
        gap="sm"
        wrap="nowrap"
        style={{
          flexShrink: 0,
          borderBottom: '1px solid var(--mantine-color-default-border)',
        }}
      >
        <ActionIcon
          component={Link}
          to={backTo}
          variant="subtle"
          size="lg"
          aria-label={t('actions.back')}
        >
          <IconArrowLeft size={20} />
        </ActionIcon>

        <Text fw={600} truncate style={{ flex: 1, minWidth: 0 }}>
          {title}
        </Text>

        <Group gap="xs" wrap="nowrap" c="dimmed">
          <Group gap={4} wrap="nowrap">
            <IconRoute size={16} />
            <Text size="sm">{distance(route.distance)}</Text>
          </Group>
          <Group gap={4} wrap="nowrap" visibleFrom="sm">
            <IconArrowUp size={16} />
            <Text size="sm">{elevation(route.elevationGain)}</Text>
          </Group>
          <Group gap={4} wrap="nowrap" visibleFrom="sm">
            <IconArrowDown size={16} />
            <Text size="sm">{elevation(route.elevationLoss)}</Text>
          </Group>
        </Group>
      </Group>

      {/* Map fills the remaining space above the chart strip */}
      <Box style={{ flex: 1, minHeight: 0 }}>
        <RouteTrackMap
          route={route}
          mapRef={mapRef}
          hoveredPointIndex={hoveredPointIndex}
          onHoverPoint={handleHoverPoint}
          fitPadding={50}
          onMoveStart={onMapMoveStart}
          onMoveEnd={onMapMoveEnd}
        />
      </Box>

      {/* Fixed elevation strip below the map (no overlap) */}
      <Box
        h={160}
        style={{
          flexShrink: 0,
          borderTop: '1px solid var(--mantine-color-default-border)',
          backgroundColor: stripBg,
        }}
      >
        <ElevationChart
          ref={chartRef}
          route={route}
          hoveredPointIndex={hoveredPointIndex}
          onHoverPoint={handleHoverPoint}
          filled
          zoom={{
            onMoveStart: onChartMoveStart,
            onMoveComplete: onChartMoveComplete,
          }}
        />
      </Box>
    </Box>
  )
}
