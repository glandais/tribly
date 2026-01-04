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
import { getRoute } from '@/api/endpoints/routes/routes'
import type { RouteDetailDto } from '@/api/dto'
import { StartMarker, EndMarker } from '../map/MapMarkers'
import { calculateBounds, routeToGeoJSON } from '../map/mapUtils'
import { MapStyleSwitcher } from '../map/MapStyleSwitcher'
import { useMapStyle } from '../../hooks/useMapStyle'
import 'maplibre-gl/dist/maplibre-gl.css'

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
}

export function RoutesMapView({
  items,
  teamSlug,
  highlightedItemId,
  onItemHover,
}: RoutesMapViewProps) {
  const { t } = useTranslation('common')
  const { t: tCommon } = useTranslation('common')

  const { styleId, setStyleId, style } = useMapStyle()
  const mapRef = useRef<MapRef>(null)
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
        labels: highlightedRoute.trackPoints.map((p) => (p[3] / 1000).toFixed(1)),
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

  const chartOptions: ChartOptions<'line'> = {
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
        callbacks: {
          title: (tooltipItems: TooltipItem<'line'>[]) => {
            if (tooltipItems.length > 0 && highlightedRoute) {
              const index = tooltipItems[0].dataIndex
              const point = highlightedRoute.trackPoints[index]
              return `${(point[3] / 1000).toFixed(1)} km`
            }
            return ''
          },
          label: (item: TooltipItem<'line'>) => {
            return `${Math.round(item.parsed.y ?? 0)} m`
          },
        },
      },
    },
    scales: {
      x: {
        display: true,
        title: {
          display: true,
          text: 'Distance (km)',
        },
        ticks: {
          maxTicksLimit: 10,
        },
      },
      y: {
        display: true,
        title: {
          display: true,
          text: 'Elevation (m)',
        },
      },
    },
    animation: { duration: 0 },
  }

  if (isLoading) {
    return (
      <div className="w-full h-[500px] flex items-center justify-center bg-gray-100 rounded">
        <div className="text-gray-500">{tCommon('loading')}</div>
      </div>
    )
  }

  if (routesData.length === 0) {
    return (
      <div className="w-full h-[500px] flex items-center justify-center bg-gray-100 rounded">
        <div className="text-gray-500">{t(`map.noRoutes`)}</div>
      </div>
    )
  }

  // End marker: always use last route's last point
  const lastRoute = routesData[routesData.length - 1]
  const endPoint = lastRoute.trackPoints[lastRoute.trackPoints.length - 1]

  return (
    <div className="border rounded overflow-hidden">
      {/* Map container */}
      <div className="relative w-full h-[500px] z-0">
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
          <MapStyleSwitcher
            position="bottom-left"
            currentStyleId={styleId}
            onStyleChange={setStyleId}
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
        <div className="absolute top-0 right-0 w-full sm:w-2/5 h-[150px] bg-white shadow-lg z-[1000] pointer-events-auto">
          {chartData && <Line data={chartData} options={chartOptions} />}
        </div>
      </div>
    </div>
  )
}
