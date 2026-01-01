import { useEffect, useState, useCallback } from 'react'
import { MapContainer, TileLayer, Polyline, Marker, useMap, useMapEvents } from 'react-leaflet'
import { LatLngBounds, DivIcon } from 'leaflet'
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
  TooltipItem,
} from 'chart.js'
import { useTranslation } from 'react-i18next'
import { routesApi, unwrapResponse } from '../../lib/apiClient'
import type { TrackPointDto } from '../../api/api'
import 'leaflet/dist/leaflet.css'

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
  trackPoints: TrackPointDto[]
  distance: number
  elevationGain: number
}

export interface RoutesMapViewProps {
  items: MapRouteItem[]
  teamSlug: string
  highlightedItemId?: string | null
  onItemHover?: (itemId: string | null) => void
}

// Custom marker icons
const createStartIcon = () =>
  new DivIcon({
    className: '',
    html: '<div class="w-6 h-6 bg-green-500 border-2 border-white rounded-full shadow-lg"></div>',
    iconSize: [24, 24],
    iconAnchor: [12, 12],
  })

const createEndIcon = () =>
  new DivIcon({
    className: '',
    html: '<div class="w-6 h-6 bg-red-500 border-2 border-white rounded-full shadow-lg"></div>',
    iconSize: [24, 24],
    iconAnchor: [12, 12],
  })

// Component to fit map bounds when routes change
function MapBoundsSetter({ routesData }: { routesData: RouteData[] }) {
  const map = useMap()

  useEffect(() => {
    if (routesData.length > 0) {
      const bounds = new LatLngBounds([])
      routesData.forEach((route) => {
        route.trackPoints.forEach((point) => {
          bounds.extend([point.lat, point.lng])
        })
      })
      if (bounds.isValid()) {
        map.fitBounds(bounds)
      }
    }
  }, [routesData, map])

  return null
}

// Component to handle route highlighting via map interactions
function RouteInteractionHandler({
  onItemHover,
}: {
  onItemHover?: (itemId: string | null) => void
}) {
  useMapEvents({
    mouseout: () => {
      if (onItemHover) {
        onItemHover(null)
      }
    },
  })

  return null
}

export function RoutesMapView({
  items,
  teamSlug,
  highlightedItemId,
  onItemHover,
}: RoutesMapViewProps) {
  const { t } = useTranslation('common')
  const [routesData, setRoutesData] = useState<RouteData[]>([])
  const [highlightedRoute, setHighlightedRoute] = useState<RouteData | null>(null)
  const [isLoading, setIsLoading] = useState(true)

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
          const routeDetail = await unwrapResponse(routesApi.getRoute(item.routeSlug, teamSlug))
          const trackPoints = routeDetail.tracks?.flatMap((track) => track.trackPoints) || []
          if (trackPoints.length > 0) {
            routes.push({
              itemId: item.id,
              itemName: item.name,
              color: ROUTE_COLORS[i % ROUTE_COLORS.length],
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
      if (routes.length > 0) {
        setHighlightedRoute(routes[0]) // Highlight first route by default
      }
      setIsLoading(false)
    }

    loadRoutes()
  }, [items, teamSlug])

  // Update highlighted route when highlightedItemId changes
  useEffect(() => {
    if (highlightedItemId) {
      const route = routesData.find((r) => r.itemId === highlightedItemId)
      if (route) {
        setHighlightedRoute(route)
      }
    } else if (routesData.length > 0) {
      setHighlightedRoute(routesData[0])
    }
  }, [highlightedItemId, routesData])

  const handlePolylineClick = useCallback(
    (itemId: string) => {
      const route = routesData.find((r) => r.itemId === itemId)
      if (route) {
        setHighlightedRoute(route)
        if (onItemHover) {
          onItemHover(itemId)
        }
      }
    },
    [routesData, onItemHover]
  )

  // Prepare chart data from highlighted route
  const chartData = highlightedRoute
    ? {
        labels: highlightedRoute.trackPoints.map((p) => (p.dist / 1000).toFixed(1)),
        datasets: [
          {
            label: 'Elevation',
            data: highlightedRoute.trackPoints.map((p) => p.ele),
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
              return `${(point.dist / 1000).toFixed(1)} km`
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
  }

  if (isLoading) {
    return (
      <div className="w-full h-[500px] flex items-center justify-center bg-gray-100 rounded">
        <div className="text-gray-500">{t('map.loading')}</div>
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

  const defaultCenter: [number, number] = [
    routesData[0].trackPoints[0].lat,
    routesData[0].trackPoints[0].lng,
  ]

  // End marker: always use last route's last point
  const lastRoute = routesData[routesData.length - 1]
  const endPoint = lastRoute.trackPoints[lastRoute.trackPoints.length - 1]

  return (
    <div className="border rounded overflow-hidden">
      {/* Map container */}
      <div className="relative w-full h-[500px] z-0">
        <MapContainer
          center={defaultCenter}
          zoom={11}
          style={{ width: '100%', height: '100%' }}
          zoomControl={false}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {/* Render all routes */}
          {routesData.map((route) => {
            const isHighlighted = highlightedRoute?.itemId === route.itemId
            const positions = route.trackPoints.map((p) => [p.lat, p.lng] as [number, number])

            return (
              <Polyline
                key={route.itemId}
                positions={positions}
                color={route.color}
                weight={isHighlighted ? 8 : 5}
                opacity={isHighlighted ? 0.7 : 0.5}
                eventHandlers={{
                  click: () => handlePolylineClick(route.itemId),
                  mouseover: () => handlePolylineClick(route.itemId),
                }}
              />
            )
          })}

          {/* Start marker (first route's first point) */}
          {routesData.length > 0 && (
            <Marker
              position={[routesData[0].trackPoints[0].lat, routesData[0].trackPoints[0].lng]}
              icon={createStartIcon()}
            />
          )}

          {/* End marker (last route's last point) */}
          {routesData.length > 0 && (
            <Marker position={[endPoint.lat, endPoint.lng]} icon={createEndIcon()} />
          )}

          <MapBoundsSetter routesData={routesData} />
          <RouteInteractionHandler onItemHover={onItemHover} />
        </MapContainer>

        {/* Elevation chart overlay */}
        <div className="absolute top-0 right-0 w-full sm:w-2/5 h-[150px] bg-white shadow-lg z-[1000] pointer-events-auto">
          {chartData && <Line data={chartData} options={chartOptions} />}
        </div>
      </div>
    </div>
  )
}
