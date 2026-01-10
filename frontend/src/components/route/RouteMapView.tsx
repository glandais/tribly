import { useEffect, useState, useRef, useMemo, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
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
  ChartEvent,
  ActiveElement,
  TooltipItem,
  Plugin,
  ScriptableContext,
  ScriptableLineSegmentContext,
} from 'chart.js'
import { Box, Center, Paper, Text } from '@mantine/core'
import type { RouteDetailDto } from '@/api/dto'
import { StartMarker, EndMarker, HoverMarker, WaypointMarker } from '../map/MapMarkers'
import {
  NEUTRAL_COLOR,
  getColorFromGradient,
  getPointClimbGradient,
  calculateBounds,
  distance,
  findNearestPoint,
  createGradientLineFeatures,
} from '../map/mapUtils'
import { MapStyleSwitcher } from '../map/MapStyleSwitcher'
import { useMapStyle } from '../../hooks/useMapStyle'
import 'maplibre-gl/dist/maplibre-gl.css'

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Filler)

// Extended chart type with crosshair property
type ChartWithCrosshair = ChartJS<'line'> & { crosshair?: { x: number } | null }

// Crosshair plugin for Chart.js
const crosshairPlugin: Plugin<'line'> = {
  id: 'crosshair',
  afterDraw: (chart) => {
    const chartWithCrosshair = chart as ChartWithCrosshair
    if (chartWithCrosshair.crosshair?.x) {
      const ctx = chart.ctx
      const x = chartWithCrosshair.crosshair.x
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

interface RouteMapViewProps {
  route: RouteDetailDto
}

export function RouteMapView({ route }: RouteMapViewProps) {
  const { t } = useTranslation()
  const { styleId, setStyleId, style } = useMapStyle()
  const [hoveredPointIndex, setHoveredPointIndex] = useState<number>(-1)
  const mapRef = useRef<MapRef>(null)
  const chartRef = useRef<ChartJS<'line'>>(null)

  // Flatten all track points and climbs from multiple tracks
  const trackPoints = useMemo(
    () => route.tracks.flatMap((track) => track.line.coordinates) || [],
    [route.tracks]
  )
  const climbs = useMemo(() => route.tracks.flatMap((track) => track.climbs), [route.tracks])
  const waypoints = route.waypoints || []

  // Create gradient line GeoJSON
  const lineFeatures = useMemo(() => createGradientLineFeatures(route.tracks), [route.tracks])

  // Fit bounds when map is loaded (with extra bottom padding for chart overlay)
  const handleMapLoad = useCallback(() => {
    if (mapRef.current && trackPoints.length > 0) {
      const bounds = calculateBounds(trackPoints)
      // Chart overlay is 200px at bottom, add padding to keep route visible
      mapRef.current.fitBounds(bounds, {
        padding: { top: 50, bottom: 220, left: 50, right: 50 },
        duration: 0,
      })
    }
  }, [trackPoints])

  // Handle mouse move for nearest point detection
  const handleMouseMove = useCallback(
    (event: MapMouseEvent) => {
      if (!mapRef.current || trackPoints.length === 0) return

      const bounds = mapRef.current.getBounds()
      const sw = bounds.getSouthWest()
      const ne = bounds.getNorthEast()
      const mapViewSize = distance(sw.lng, sw.lat, ne.lng, ne.lat)

      const nearestIndex = findNearestPoint(
        trackPoints,
        event.lngLat.lat,
        event.lngLat.lng,
        mapViewSize / 20.0
      )

      if (nearestIndex >= 0) {
        setHoveredPointIndex(nearestIndex)
      }
    },
    [trackPoints]
  )

  const handleMouseLeave = useCallback(() => {
    setHoveredPointIndex(-1)
  }, [])

  // Prepare chart data with climb coloring
  const chartData: ChartData<'line'> = useMemo(
    () => ({
      labels: trackPoints.map((p) => (p[3] / 1000).toFixed(1)),
      datasets: [
        {
          label: 'Elevation',
          data: trackPoints.map((p) => p[2]),
          fill: true,
          // backgroundColor: (context: ScriptableContext<'line'>) => {
          //   const index = context.dataIndex
          //   if (index === undefined) return NEUTRAL_COLOR
          //   const gradient = getPointClimbGradient(trackPoints[index], climbs)
          //   return getColorFromGradient(gradient) + '33'
          // },
          borderColor: (context: ScriptableContext<'line'>) => {
            const index = context.dataIndex
            if (index === undefined) return NEUTRAL_COLOR
            const gradient = getPointClimbGradient(trackPoints[index], climbs)
            return getColorFromGradient(gradient)
          },
          borderWidth: 2,
          pointRadius: 0,
          segment: {
            // backgroundColor: (ctx: ScriptableLineSegmentContext) => {
            //   const gradient = getPointClimbGradient(trackPoints[ctx.p0DataIndex], climbs)
            //   return getColorFromGradient(gradient) + '33'
            // },
            borderColor: (ctx: ScriptableLineSegmentContext) => {
              const gradient = getPointClimbGradient(trackPoints[ctx.p0DataIndex], climbs)
              return getColorFromGradient(gradient)
            },
          },
        },
      ],
    }),
    [trackPoints, climbs]
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
          callbacks: {
            title: (items: TooltipItem<'line'>[]) => {
              if (items.length > 0) {
                const index = items[0].dataIndex
                const point = trackPoints[index]
                return `${(point[3] / 1000).toFixed(1)} km`
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
                if (point[3] >= climb.startDistance && point[3] <= climb.endDistance) {
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
      onHover: (_event: ChartEvent, activeElements: ActiveElement[]) => {
        if (activeElements.length > 0) {
          const index = activeElements[0].index
          setHoveredPointIndex(index)

          // Update crosshair position
          if (chartRef.current) {
            const chart = chartRef.current as ChartWithCrosshair
            const meta = chart.getDatasetMeta(0)
            const point = meta.data[index]
            if (point) {
              chart.crosshair = { x: point.x }
              chart.draw()
            }
          }
        }
      },
      animation: { duration: 0 },
    }),
    [trackPoints, climbs]
  )

  // Update chart crosshair when hovering over map
  useEffect(() => {
    if (chartRef.current && hoveredPointIndex >= 0) {
      const chart = chartRef.current as ChartWithCrosshair
      const meta = chart.getDatasetMeta(0)
      const point = meta.data[hoveredPointIndex]
      if (point) {
        chart.crosshair = { x: point.x }
        chart.draw()

        // Show tooltip
        chart.tooltip?.setActiveElements([{ datasetIndex: 0, index: hoveredPointIndex }], {
          x: point.x,
          y: point.y,
        })
        chart.update()
      }
    } else if (chartRef.current) {
      const chart = chartRef.current as ChartWithCrosshair
      chart.crosshair = null
      chart.tooltip?.setActiveElements([], { x: 0, y: 0 })
      chart.update()
    }
  }, [hoveredPointIndex])

  if (trackPoints.length === 0) {
    return (
      <Center h={700} bg="gray.1" style={{ borderRadius: 'var(--mantine-radius-default)' }}>
        <Text c="dimmed">{t('map.noTrackData')}</Text>
      </Center>
    )
  }

  const hoveredPoint = hoveredPointIndex >= 0 ? trackPoints[hoveredPointIndex] : null

  return (
    <Paper withBorder style={{ overflow: 'hidden' }}>
      {/* Map container */}
      <Box pos="relative" w="100%" h={700} style={{ zIndex: 0 }}>
        <Map
          ref={mapRef}
          mapLib={maplibregl}
          mapStyle={style}
          initialViewState={{
            longitude: trackPoints[0][0],
            latitude: trackPoints[0][1],
            zoom: 11,
          }}
          style={{ width: '100%', height: '100%' }}
          onLoad={handleMapLoad}
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
        >
          <NavigationControl position="top-left" />
          <MapStyleSwitcher
            position="top-right"
            currentStyleId={styleId}
            onStyleChange={setStyleId}
          />
          {/* Gradient-colored route line */}
          <Source id="route-segments" type="geojson" data={lineFeatures}>
            <Layer
              id="route-line"
              type="line"
              paint={{
                'line-color': ['get', 'color'],
                'line-width': 8,
                'line-opacity': 0.8,
              }}
            />
          </Source>

          {/* Start marker */}
          <StartMarker longitude={trackPoints[0][0]} latitude={trackPoints[0][1]} />

          {/* End marker */}
          <EndMarker
            longitude={trackPoints[trackPoints.length - 1][0]}
            latitude={trackPoints[trackPoints.length - 1][1]}
          />

          {/* Waypoints */}
          {waypoints.map(
            (waypoint, index) =>
              waypoint.geometry.coordinates[0] &&
              waypoint.geometry.coordinates[1] && (
                <WaypointMarker
                  key={`waypoint-${index}`}
                  longitude={waypoint.geometry.coordinates[0]}
                  latitude={waypoint.geometry.coordinates[1]}
                  name={waypoint.name}
                />
              )
          )}

          {/* Hover marker */}
          {hoveredPoint && <HoverMarker longitude={hoveredPoint[0]} latitude={hoveredPoint[1]} />}
        </Map>

        {/* Elevation chart overlay */}
        <Box
          pos="absolute"
          bottom={0}
          left={0}
          right={0}
          h={200}
          bg="rgba(255, 255, 255, 0.95)"
          style={{ zIndex: 1000, pointerEvents: 'auto', boxShadow: 'var(--mantine-shadow-lg)' }}
        >
          <Line
            ref={chartRef}
            data={chartData}
            options={chartOptions}
            plugins={[crosshairPlugin]}
          />
        </Box>
      </Box>
    </Paper>
  )
}
