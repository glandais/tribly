import { useEffect, useState, useRef } from 'react'
import { useTranslation } from 'react-i18next'
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
  Plugin,
} from 'chart.js'
import type { RouteDetailDto, TrackPointDto, ClimbDto } from '../../api/api'
import 'leaflet/dist/leaflet.css'

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Filler)

// Color calculation (matching biketeam single-map.js)
const NEUTRAL_HUE = 210
const MIN_HUE = 85 // Green for low gradients
const MAX_HUE = 255 - 105 // Red for high gradients (360 - 105 = 255)
const SATURATION = '86%'
const LIGHTNESS = '62%'
const NEUTRAL_COLOR = `hsl(${NEUTRAL_HUE},${SATURATION},${LIGHTNESS})`

// Calculate color based on gradient
function getColorFromGradient(gradient: number): string {
  if (gradient === 0) return NEUTRAL_COLOR

  // Map gradient (0% to 18%) to hue (MIN_HUE to MAX_HUE)
  const normalizedGradient = Math.min(18, Math.max(0, gradient))
  let hue = Math.round(MIN_HUE + (normalizedGradient / 18.0) * (MAX_HUE - MIN_HUE))

  // Ensure hue is in valid range
  hue = Math.min(MIN_HUE, Math.max(MAX_HUE, hue))

  if (hue < 0) {
    hue = hue + 360
  }

  return `hsl(${hue},${SATURATION},${LIGHTNESS})`
}

// Determine if a point is in a climb and get its gradient
function getPointClimbGradient(point: TrackPointDto, climbs: ClimbDto[]): number {
  for (const climb of climbs) {
    if (point.dist >= climb.startDistance && point.dist <= climb.endDistance) {
      return climb.averageGradient
    }
  }
  return 0
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

const createHoverIcon = () =>
  new DivIcon({
    className: '',
    html: '<div class="w-4 h-4 bg-blue-500 border-2 border-white rounded-full shadow-lg"></div>',
    iconSize: [16, 16],
    iconAnchor: [8, 8],
  })

const createWaypointIcon = (name?: string) =>
  new DivIcon({
    className: '',
    html: `<div class="flex items-center">
      <div class="w-5 h-5 bg-amber-500 border-2 border-white rounded-full shadow-lg"></div>
      ${name ? `<span class="ml-1 px-1 bg-white/90 rounded text-xs font-medium shadow-sm whitespace-nowrap">${name}</span>` : ''}
    </div>`,
    iconSize: [20, 20],
    iconAnchor: [10, 10],
  })

// Crosshair plugin for Chart.js
const crosshairPlugin: Plugin<'line'> = {
  id: 'crosshair',
  afterDraw: (chart: any) => {
    if (chart.crosshair?.x) {
      const ctx = chart.ctx
      const x = chart.crosshair.x
      const yAxis = chart.scales.y

      ctx.save()
      ctx.beginPath()
      ctx.moveTo(x, yAxis.top)
      ctx.lineTo(x, yAxis.bottom)
      ctx.lineWidth = 1
      ctx.strokeStyle = 'rgba(0, 0, 0, 0.3)'
      ctx.stroke()
      ctx.restore()
    }
  },
}

// Component to fit map bounds
function MapBoundsSetter({ trackPoints }: { trackPoints: TrackPointDto[] }) {
  const map = useMap()

  useEffect(() => {
    if (trackPoints.length > 0) {
      const bounds = new LatLngBounds(trackPoints.map((p) => [p.lat, p.lng] as [number, number]))
      if (bounds.isValid()) {
        map.fitBounds(bounds)
      }
    }
  }, [trackPoints, map])

  return null
}

// Simple distance calculation (Haversine formula)
function distance(lng1: number, lat1: number, lng2: number, lat2: number): number {
  const R = 6371 // Earth radius in km
  const dLat = ((lat2 - lat1) * Math.PI) / 180
  const dLon = ((lng2 - lng1) * Math.PI) / 180
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

// Find nearest point to lat/lng
function findNearestPoint(
  trackPoints: TrackPointDto[],
  lat: number,
  lng: number,
  maxDistance: number
): number {
  let nearestIndex = -1
  let minDist = maxDistance

  for (let i = 0; i < trackPoints.length; i++) {
    const dist = distance(lng, lat, trackPoints[i].lng, trackPoints[i].lat)
    if (dist < minDist) {
      minDist = dist
      nearestIndex = i
    }
  }

  return nearestIndex
}

// Component to handle map interactions
function MapInteractionHandler({
  trackPoints,
  onPointHover,
}: {
  trackPoints: TrackPointDto[]
  onPointHover: (index: number) => void
}) {
  const map = useMapEvents({
    mousemove: (e) => {
      const bounds = map.getBounds()
      const sw = bounds.getSouthWest()
      const ne = bounds.getNorthEast()
      const mapViewSize = distance(sw.lng, sw.lat, ne.lng, ne.lat)

      const nearestIndex = findNearestPoint(
        trackPoints,
        e.latlng.lat,
        e.latlng.lng,
        mapViewSize / 20.0
      )

      if (nearestIndex >= 0) {
        onPointHover(nearestIndex)
      }
    },
    mouseout: () => {
      onPointHover(-1)
    },
  })

  return null
}

interface RouteMapViewProps {
  route: RouteDetailDto
}

export function RouteMapView({ route }: RouteMapViewProps) {
  const { t } = useTranslation('common')
  const [hoveredPointIndex, setHoveredPointIndex] = useState<number>(-1)
  const chartRef = useRef<ChartJS<'line'>>(null)

  // Flatten all track points and climbs from multiple tracks
  const trackPoints = route.tracks?.flatMap((track) => track.trackPoints) || []
  const climbs = route.tracks?.flatMap((track) => track.climbs) || []
  const waypoints = route.waypoints || []

  // Calculate colors for each segment based on climbs
  const segmentColors = trackPoints.map((point) => {
    const gradient = getPointClimbGradient(point, climbs)
    return getColorFromGradient(gradient)
  })

  // Group polylines by color for efficient rendering
  const polylineSegments: { color: string; positions: [number, number][] }[] = []
  let currentColor = segmentColors[0]
  let currentSegment: [number, number][] = []

  for (let i = 0; i < trackPoints.length; i++) {
    const point = trackPoints[i]
    const color = segmentColors[i]

    if (color !== currentColor && currentSegment.length > 0) {
      polylineSegments.push({ color: currentColor, positions: currentSegment })
      currentSegment = [[point.lat, point.lng]]
      currentColor = color
    } else {
      currentSegment.push([point.lat, point.lng])
    }
  }

  if (currentSegment.length > 0) {
    polylineSegments.push({ color: currentColor, positions: currentSegment })
  }

  // Prepare chart data with climb coloring
  const chartData = {
    labels: trackPoints.map((p) => (p.dist / 1000).toFixed(1)),
    datasets: [
      {
        label: 'Elevation',
        data: trackPoints.map((p) => p.ele),
        fill: true,
        backgroundColor: (context: any) => {
          const index = context.dataIndex
          if (index === undefined) return NEUTRAL_COLOR
          const gradient = getPointClimbGradient(trackPoints[index], climbs)
          return getColorFromGradient(gradient) + '33'
        },
        borderColor: (context: any) => {
          const index = context.dataIndex
          if (index === undefined) return NEUTRAL_COLOR
          const gradient = getPointClimbGradient(trackPoints[index], climbs)
          return getColorFromGradient(gradient)
        },
        borderWidth: 2,
        pointRadius: 0,
        segment: {
          backgroundColor: (ctx: any) => {
            const gradient = getPointClimbGradient(trackPoints[ctx.p0DataIndex], climbs)
            return getColorFromGradient(gradient) + '33'
          },
          borderColor: (ctx: any) => {
            const gradient = getPointClimbGradient(trackPoints[ctx.p0DataIndex], climbs)
            return getColorFromGradient(gradient)
          },
        },
      },
    ],
  }

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
          title: (items: TooltipItem<'line'>[]) => {
            if (items.length > 0) {
              const index = items[0].dataIndex
              const point = trackPoints[index]
              return `${(point.dist / 1000).toFixed(1)} km`
            }
            return ''
          },
          label: (item: TooltipItem<'line'>) => {
            const index = item.dataIndex
            const point = trackPoints[index]
            const gradient = getPointClimbGradient(point, climbs)

            let label = `${Math.round(item.parsed.y ?? 0)} m`
            if (gradient > 0) {
              label += ` (${gradient.toFixed(1)}%)`
            }
            return label
          },
          afterLabel: (item: TooltipItem<'line'>) => {
            const index = item.dataIndex
            const point = trackPoints[index]

            // Find if point is in a climb
            for (const climb of climbs) {
              if (point.dist >= climb.startDistance && point.dist <= climb.endDistance) {
                return [
                  '',
                  `${Math.round(climb.elevationGain)}m / ${((climb.endDistance - climb.startDistance) / 1000).toFixed(1)}km`,
                  `Avg: ${climb.averageGradient.toFixed(1)}% | Max: ${climb.maxGradient.toFixed(1)}%`,
                ]
              }
            }
            return ''
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
    onHover: (_event: any, activeElements: any[]) => {
      if (activeElements.length > 0) {
        const index = activeElements[0].index
        setHoveredPointIndex(index)

        // Update crosshair position
        if (chartRef.current) {
          const chart = chartRef.current as any
          const meta = chart.getDatasetMeta(0)
          const point = meta.data[index]
          if (point) {
            chart.crosshair = { x: point.x }
            chart.draw()
          }
        }
      }
    },
  }

  // Update chart crosshair when hovering over map
  useEffect(() => {
    if (chartRef.current && hoveredPointIndex >= 0) {
      const chart = chartRef.current as any
      const meta = chart.getDatasetMeta(0)
      const point = meta.data[hoveredPointIndex]
      if (point) {
        chart.crosshair = { x: point.x }
        chart.draw()

        // Show tooltip
        chart.tooltip.setActiveElements([{ datasetIndex: 0, index: hoveredPointIndex }], {
          x: point.x,
          y: point.y,
        })
        chart.update()
      }
    } else if (chartRef.current) {
      const chart = chartRef.current as any
      chart.crosshair = null
      chart.tooltip.setActiveElements([], { x: 0, y: 0 })
      chart.update()
    }
  }, [hoveredPointIndex])

  if (trackPoints.length === 0) {
    return (
      <div className="w-full h-[500px] flex items-center justify-center bg-gray-100 rounded">
        <div className="text-gray-500">{t('map.noTrackData')}</div>
      </div>
    )
  }

  const defaultCenter: [number, number] = [trackPoints[0].lat, trackPoints[0].lng]
  const hoveredPoint = hoveredPointIndex >= 0 ? trackPoints[hoveredPointIndex] : null

  return (
    <div className="border rounded overflow-hidden">
      {/* Map container */}
      <div className="relative w-full h-[500px] z-0">
        <MapContainer
          center={defaultCenter}
          zoom={11}
          style={{ width: '100%', height: '100%' }}
          zoomControl={true}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {/* Render colored polyline segments */}
          {polylineSegments.map((segment, index) => (
            <Polyline
              key={index}
              positions={segment.positions}
              color={segment.color}
              weight={8}
              opacity={0.8}
            />
          ))}

          {/* Start marker */}
          <Marker position={[trackPoints[0].lat, trackPoints[0].lng]} icon={createStartIcon()} />

          {/* End marker */}
          <Marker
            position={[
              trackPoints[trackPoints.length - 1].lat,
              trackPoints[trackPoints.length - 1].lng,
            ]}
            icon={createEndIcon()}
          />

          {/* Waypoint markers */}
          {waypoints.map(
            (waypoint, index) =>
              waypoint.lat &&
              waypoint.lon && (
                <Marker
                  key={`waypoint-${index}`}
                  position={[waypoint.lat, waypoint.lon]}
                  icon={createWaypointIcon(waypoint.name)}
                />
              )
          )}

          {/* Hovered point marker */}
          {hoveredPoint && (
            <Marker position={[hoveredPoint.lat, hoveredPoint.lng]} icon={createHoverIcon()} />
          )}

          <MapBoundsSetter trackPoints={trackPoints} />
          <MapInteractionHandler trackPoints={trackPoints} onPointHover={setHoveredPointIndex} />
        </MapContainer>

        {/* Elevation chart overlay */}
        <div className="absolute bottom-0 left-0 right-0 h-[200px] bg-white/95 shadow-lg z-[1000] pointer-events-auto">
          <Line
            ref={chartRef}
            data={chartData}
            options={chartOptions}
            plugins={[crosshairPlugin]}
          />
        </div>
      </div>
    </div>
  )
}
