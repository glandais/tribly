import { useEffect, useState, useCallback, useRef, useMemo } from 'react'
import type { LngLatBoundsLike } from 'maplibre-gl'
import { Source, Layer, MapRef, MapMouseEvent } from 'react-map-gl/maplibre'
import { Line } from 'react-chartjs-2'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Filler,
  ChartOptions,
  ChartData,
  TooltipItem,
} from 'chart.js'
import { useTranslation } from 'react-i18next'
import { Box, Center, Text, Paper, useMantineTheme } from '@mantine/core'
import { useMapHeight } from '@/hooks/useResponsive'
import { useRoutesBulk } from '@/hooks/useRoutesBulk'
import { useResolvedColorScheme } from '@/hooks/useResolvedColorScheme'
import type { RouteDetailDto } from '@/api/dto'
import { StartMarker, EndMarker } from '../map/MapMarkers'
import { calculateBounds, getElevationAxisBounds, routeToGeoJSON } from '../map/mapUtils'
import { PedalonsMap } from '../map/PedalonsMap'
import { useUnits } from '../../hooks/useUnits'
import { getOverlayBg } from '@/lib/colors'
// maplibre-gl CSS is provided by maplibre-theme in index.css

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Filler)

// Route colors (matching biketeam)
const ROUTE_COLORS = [
  '#566B13',
  '#1d32a8',
  '#732C7B',
  '#bdbd22',
  '#c90808',
  '#b81491',
  '#628de3',
  '#6dcc5c',
  '#c694d4',
  '#e3a209',
]

// Minimal interface RideGroupDto satisfies directly; a trip stage carries the whole RouteDto,
// so TripDetailPage narrows it down to the slug.
export interface MapRouteItem {
  id: string
  name: string
  routeSlug?: string
}

interface RouteData {
  itemId: string
  itemName: string
  color: string
  route: RouteDetailDto
  trackPoints: number[][]
  distance: number
  elevationGain: number
}

export interface RoutesMapViewProps {
  items: MapRouteItem[]
  teamSlug: string
  highlightedItemId?: string | null
  onItemHover?: (itemId: string | null) => void
  /** Map size variant: 'compact' for cards, 'standard' for detail pages, 'full' for full-screen */
  variant?: 'compact' | 'standard' | 'full'
}

export function RoutesMapView({
  items,
  teamSlug,
  highlightedItemId,
  onItemHover,
  variant = 'full',
}: RoutesMapViewProps) {
  const { t } = useTranslation()
  const mapHeight = useMapHeight(variant)
  const colorScheme = useResolvedColorScheme()
  const theme = useMantineTheme()
  const { config, distance, formatDistance, elevation } = useUnits()
  const mapRef = useRef<MapRef>(null)

  // Chart colors based on color scheme
  const chartColors = useMemo(
    () => ({
      text: colorScheme === 'dark' ? theme.colors.dark[0] : theme.colors.dark[7],
      grid: colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[3],
      background: getOverlayBg(colorScheme, true),
    }),
    [colorScheme, theme.colors.dark, theme.colors.gray]
  )
  const [selectedRouteId, setSelectedRouteId] = useState<string | null>(null)
  const [cursor, setCursor] = useState<string>('grab')
  const [isMapLoaded, setIsMapLoaded] = useState(false)

  // One bulk request for every distinct route slug on the screen, instead of one `getRoute`
  // per item (several ride groups, or every stage of a trip, often share the same route).
  // Slugs are deduped and the array is memoized on their sorted, joined value so the query
  // key — and so the request — stays stable across renders that don't actually change the set.
  const itemsWithRoutes = useMemo(() => items.filter((item) => item.routeSlug), [items])
  const dedupedSlugsKey = useMemo(
    () =>
      Array.from(new Set(itemsWithRoutes.map((item) => item.routeSlug!)))
        .sort()
        .join(','),
    [itemsWithRoutes]
  )
  const dedupedSlugs = useMemo(
    () => (dedupedSlugsKey ? dedupedSlugsKey.split(',') : []),
    [dedupedSlugsKey]
  )

  const { data: bulkData, isLoading } = useRoutesBulk(teamSlug, {
    slug: dedupedSlugs,
  })

  const routesBySlug = useMemo(() => {
    const map = new Map<string, RouteDetailDto>()
    for (const route of bulkData?.routes ?? []) {
      map.set(route.slug, route)
    }
    return map
  }, [bulkData])

  const routesData = useMemo<RouteData[]>(
    () =>
      itemsWithRoutes.flatMap((item, i) => {
        const routeDetail = routesBySlug.get(item.routeSlug!)
        if (!routeDetail) return []
        const trackPoints = routeDetail.tracks?.flatMap((track) => track.line.coordinates) || []
        if (trackPoints.length === 0) return []
        return [
          {
            itemId: item.id,
            itemName: item.name,
            color: ROUTE_COLORS[i % ROUTE_COLORS.length],
            route: routeDetail,
            trackPoints,
            distance: routeDetail.distance,
            elevationGain: routeDetail.elevationGain,
          },
        ]
      }),
    [itemsWithRoutes, routesBySlug]
  )

  // Fit bounds when map is loaded AND routes are available. Prefer the server-computed
  // `extent` (built from the same decimated geometry actually returned) over recomputing it
  // client-side from `routesData`.
  const fitBoundsToRoutes = useCallback(() => {
    if (mapRef.current && routesData.length > 0) {
      const bounds = bulkData?.extent
        ? ([
            [bulkData.extent.minLon, bulkData.extent.minLat],
            [bulkData.extent.maxLon, bulkData.extent.maxLat],
          ] satisfies LngLatBoundsLike)
        : calculateBounds(routesData.flatMap((r) => r.trackPoints))
      // Chart overlay is 150px at top-right, add top padding to keep route visible
      mapRef.current.fitBounds(bounds, {
        padding: { top: 170, bottom: 50, left: 50, right: 50 },
        duration: 0,
      })
    }
  }, [routesData, bulkData])

  // Handle map load
  const handleMapLoad = useCallback(() => {
    setIsMapLoaded(true)
  }, [])

  // Frame the map ONCE per route set. After that the camera belongs to the visitor: hovering or
  // clicking a trace re-renders this component, and re-running fitBounds there would snap their
  // zoom back to the whole-ride extent mid-inspection. A genuinely different set of routes
  // (`dedupedSlugsKey`) is the only thing that earns a new frame.
  const framedSlugsKeyRef = useRef<string | null>(null)
  useEffect(() => {
    if (!isMapLoaded || routesData.length === 0) return
    if (framedSlugsKeyRef.current === dedupedSlugsKey) return
    framedSlugsKeyRef.current = dedupedSlugsKey
    fitBoundsToRoutes()
  }, [isMapLoaded, dedupedSlugsKey, routesData, fitBoundsToRoutes])

  // Derive highlighted route from props or selected state
  const highlightedRoute = useMemo(() => {
    // Priority: prop > selected > first route
    const targetId = highlightedItemId ?? selectedRouteId
    if (targetId) {
      return routesData.find((r) => r.itemId === targetId) ?? routesData[0] ?? null
    }
    return routesData[0] ?? null
  }, [highlightedItemId, selectedRouteId, routesData])

  const handlePolylineClick = useCallback(
    (itemId: string) => {
      setSelectedRouteId(itemId)
      if (onItemHover) {
        onItemHover(itemId)
      }
    },
    [onItemHover]
  )

  // Handle click on route layers
  const handleClick = useCallback(
    (event: MapMouseEvent) => {
      if (!mapRef.current) return

      const layerIds = routesData.map((r) => `line-${r.itemId}`)
      const features = mapRef.current.queryRenderedFeatures(event.point, { layers: layerIds })

      if (features.length > 0) {
        const clickedId = features[0].layer.id.replace('line-', '')
        handlePolylineClick(clickedId)
      }
    },
    [routesData, handlePolylineClick]
  )

  // Handle mouse enter on route layers
  const handleMouseEnter = useCallback(() => {
    setCursor('pointer')
  }, [])

  // Handle mouse leave
  const handleMouseLeave = useCallback(() => {
    setCursor('grab')
    if (onItemHover) {
      onItemHover(null)
    }
  }, [onItemHover])

  // Create GeoJSON data for each route
  const routeGeoJSONs = useMemo(() => {
    return routesData.map((route) => ({
      itemId: route.itemId,
      color: route.color,
      geojson: routeToGeoJSON(route.route, { id: route.itemId }),
    }))
  }, [routesData])

  // Interactive layer IDs for hover detection
  const interactiveLayerIds = useMemo(() => {
    return routesData.map((r) => `line-${r.itemId}`)
  }, [routesData])

  // Prepare chart data from highlighted route
  const chartData: ChartData<'line'> | null = highlightedRoute
    ? {
        labels: highlightedRoute.trackPoints.map((p) => formatDistance(p[3])),
        datasets: [
          {
            label: 'Elevation',
            data: highlightedRoute.trackPoints.map((p) => p[2]),
            fill: true,
            backgroundColor: `${highlightedRoute.color}33`,
            borderColor: highlightedRoute.color,
            borderWidth: 2,
            pointRadius: 0,
            tension: 0.1,
          },
        ],
      }
    : null

  const yBounds = useMemo(
    () =>
      highlightedRoute
        ? getElevationAxisBounds(highlightedRoute.trackPoints.map((p) => p[2]))
        : null,
    [highlightedRoute]
  )

  const chartOptions: ChartOptions<'line'> = useMemo(
    () => ({
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: 'index',
        intersect: false,
      },
      plugins: {
        legend: {
          display: false,
        },
        tooltip: {
          enabled: true,
          backgroundColor: chartColors.background,
          titleColor: chartColors.text,
          bodyColor: chartColors.text,
          borderColor: chartColors.grid,
          borderWidth: 1,
          callbacks: {
            title: (tooltipItems: TooltipItem<'line'>[]) => {
              if (tooltipItems.length > 0 && highlightedRoute) {
                const index = tooltipItems[0].dataIndex
                const point = highlightedRoute.trackPoints[index]
                return `${distance(point[3])}`
              }
              return ''
            },
            label: (item: TooltipItem<'line'>) => {
              return `${elevation(item.parsed.y ?? 0)}`
            },
          },
        },
      },
      scales: {
        x: {
          display: true,
          title: {
            display: true,
            text: `Distance (${config.distanceUnit})`,
            color: chartColors.text,
          },
          ticks: {
            maxTicksLimit: 10,
            color: chartColors.text,
          },
          grid: {
            color: chartColors.grid,
          },
        },
        y: {
          display: true,
          min: yBounds?.min,
          max: yBounds?.max,
          title: {
            display: true,
            text: `Elevation (${config.elevationUnit})`,
            color: chartColors.text,
          },
          ticks: {
            color: chartColors.text,
            // The bounds are the route's raw min/max, never round numbers — let Chart.js label
            // only the round ticks it picks inside the range.
            includeBounds: false,
          },
          grid: {
            color: chartColors.grid,
          },
        },
      },
      animation: { duration: 0 },
    }),
    [chartColors, highlightedRoute, config, distance, elevation, yBounds]
  )

  if (isLoading) {
    return (
      <Center
        w="100%"
        h={mapHeight}
        bg="var(--mantine-color-default-hover)"
        style={{ borderRadius: 'var(--mantine-radius-sm)' }}
      >
        <Text c="dimmed">{t('loading')}</Text>
      </Center>
    )
  }

  if (routesData.length === 0) {
    return (
      <Center
        w="100%"
        h={mapHeight}
        bg="var(--mantine-color-default-hover)"
        style={{ borderRadius: 'var(--mantine-radius-sm)' }}
      >
        <Text c="dimmed">{t(`map.noRoutes`)}</Text>
      </Center>
    )
  }

  // End marker: always use last route's last point
  const lastRoute = routesData[routesData.length - 1]
  const endPoint = lastRoute.trackPoints[lastRoute.trackPoints.length - 1]

  return (
    <Box
      style={{
        border: '1px solid var(--mantine-color-default-border)',
        borderRadius: 'var(--mantine-radius-sm)',
        overflow: 'hidden',
      }}
    >
      {/* Map container */}
      <Box
        pos="relative"
        w="100%"
        h={mapHeight}
        className={colorScheme === 'dark' ? 'dark' : undefined}
        style={{ zIndex: 0 }}
      >
        <PedalonsMap
          ref={mapRef}
          initialViewState={{
            longitude: routesData[0].trackPoints[0][0],
            latitude: routesData[0].trackPoints[0][1],
            zoom: 11,
          }}
          cursor={cursor}
          onLoad={handleMapLoad}
          onClick={handleClick}
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
          interactiveLayerIds={interactiveLayerIds}
        >
          {/* Render all routes */}
          {routeGeoJSONs.map((route) => {
            const isHighlighted = highlightedRoute?.itemId === route.itemId

            return (
              <Source
                key={`source-${route.itemId}`}
                id={`route-${route.itemId}`}
                type="geojson"
                data={route.geojson}
              >
                <Layer
                  id={`line-${route.itemId}`}
                  type="line"
                  paint={{
                    'line-color': route.color,
                    'line-width': isHighlighted ? 8 : 5,
                    'line-opacity': isHighlighted ? 0.9 : 0.5,
                  }}
                />
              </Source>
            )
          })}

          {/* Start marker (first route's first point) */}
          <StartMarker
            longitude={routesData[0].trackPoints[0][0]}
            latitude={routesData[0].trackPoints[0][1]}
          />

          {/* End marker (last route's last point) */}
          <EndMarker longitude={endPoint[0]} latitude={endPoint[1]} />
        </PedalonsMap>

        {/* Elevation chart overlay */}
        <Paper
          pos="absolute"
          top={0}
          right={0}
          w={{ base: '100%', sm: '50%', md: '40%' }}
          h={{ base: 120, sm: 140, md: 150 }}
          shadow="lg"
          style={{ zIndex: 1000, pointerEvents: 'auto', backgroundColor: chartColors.background }}
        >
          {chartData && <Line data={chartData} options={chartOptions} />}
        </Paper>
      </Box>
    </Box>
  )
}
