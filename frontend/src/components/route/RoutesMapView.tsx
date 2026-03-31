import { useEffect, useState, useCallback, useRef, useMemo } from 'react'
import Map, { Source, Layer, MapRef, MapMouseEvent, NavigationControl } from 'react-map-gl/maplibre'
import maplibregl from 'maplibre-gl'
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
import { Box, Center, Text, Paper, useComputedColorScheme, useMantineTheme } from '@mantine/core'
import { responsiveMapHeight } from '@/hooks/useResponsive'
import { getRoute } from '@/api/endpoints/routes/routes'
import type { RouteDetailDto } from '@/api/dto'
import { StartMarker, EndMarker } from '../map/MapMarkers'
import { calculateBounds, routeToGeoJSON } from '../map/mapUtils'
import { MapStyleSwitcher } from '../map/MapStyleSwitcher'
import { Terrain3D } from '../map/Terrain3D'
import { useMapStyle } from '../../hooks/useMapStyle'
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

// Minimal interface that both RideGroupDto and TripStageDto satisfy
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
  const mapHeight = responsiveMapHeight[variant]
  const colorScheme = useComputedColorScheme('light')
  const theme = useMantineTheme()
  const { styleId, setStyleId, style, terrain3d, setTerrain3d, hillshade, setHillshade } =
    useMapStyle()
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
  const [routesData, setRoutesData] = useState<RouteData[]>([])
  const [selectedRouteId, setSelectedRouteId] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [cursor, setCursor] = useState<string>('grab')
  const [isMapLoaded, setIsMapLoaded] = useState(false)

  // Load route data for all items with routes
  useEffect(() => {
    const loadRoutes = async () => {
      setIsLoading(true)
      const routes: RouteData[] = []

      // Filter items that have routes
      const itemsWithRoutes = items.filter((item) => item.routeSlug)

      for (let i = 0; i < itemsWithRoutes.length; i++) {
        const item = itemsWithRoutes[i]
        if (!item.routeSlug) continue

        try {
          // Fetch route details from API using the generated API client
          const routeDetail = await getRoute(teamSlug, item.routeSlug)
          const trackPoints = routeDetail.tracks?.flatMap((track) => track.line.coordinates) || []
          if (trackPoints.length > 0) {
            routes.push({
              itemId: item.id,
              itemName: item.name,
              color: ROUTE_COLORS[i % ROUTE_COLORS.length],
              route: routeDetail,
              trackPoints,
              distance: routeDetail.distance,
              elevationGain: routeDetail.elevationGain,
            })
          }
        } catch (error) {
          console.error(`Failed to load route for item ${item.id}:`, error)
        }
      }

      setRoutesData(routes)
      setIsLoading(false)
    }

    loadRoutes()
  }, [items, teamSlug])

  // Fit bounds when map is loaded AND routes are available
  const fitBoundsToRoutes = useCallback(() => {
    if (mapRef.current && routesData.length > 0) {
      const allTrackPoints = routesData.flatMap((r) => r.trackPoints)
      const bounds = calculateBounds(allTrackPoints)
      // Chart overlay is 150px at top-right, add top padding to keep route visible
      mapRef.current.fitBounds(bounds, {
        padding: { top: 170, bottom: 50, left: 50, right: 50 },
        duration: 0,
      })
    }
  }, [routesData])

  // Handle map load
  const handleMapLoad = useCallback(() => {
    setIsMapLoaded(true)
  }, [])

  // Fit bounds when both map is loaded and routes are available
  useEffect(() => {
    if (isMapLoaded) {
      fitBoundsToRoutes()
    }
  }, [isMapLoaded, fitBoundsToRoutes])

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
          title: {
            display: true,
            text: `Elevation (${config.elevationUnit})`,
            color: chartColors.text,
          },
          ticks: {
            color: chartColors.text,
          },
          grid: {
            color: chartColors.grid,
          },
        },
      },
      animation: { duration: 0 },
    }),
    [chartColors, highlightedRoute, config, distance, elevation]
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
        <Map
          ref={mapRef}
          mapLib={maplibregl}
          mapStyle={style}
          initialViewState={{
            longitude: routesData[0].trackPoints[0][0],
            latitude: routesData[0].trackPoints[0][1],
            zoom: 11,
          }}
          style={{ width: '100%', height: '100%' }}
          cursor={cursor}
          onLoad={handleMapLoad}
          onClick={handleClick}
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
          interactiveLayerIds={interactiveLayerIds}
        >
          <NavigationControl position="top-left" />
          <Terrain3D terrain={terrain3d} hillshade={hillshade} />
          <MapStyleSwitcher
            position="bottom-left"
            currentStyleId={styleId}
            onStyleChange={setStyleId}
            terrain3d={terrain3d}
            onTerrain3DChange={setTerrain3d}
            hillshade={hillshade}
            onHillshadeChange={setHillshade}
          />
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
        </Map>

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
