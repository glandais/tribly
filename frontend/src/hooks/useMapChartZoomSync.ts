import { useCallback, useEffect, useMemo, useRef } from 'react'
import type { MapRef } from 'react-map-gl/maplibre'
import { calculateBounds } from '../components/map/mapUtils'
import type { ElevationChartHandle } from '../components/route/ElevationChart'
import type { MappableRoute } from '../components/route/RouteMapView'

/** Minimum chart x-range (meters) so very short routes/gestures never collapse to a sliver. */
const MIN_RANGE_METERS = 1000
/** Delay before releasing the chart-induced-move guard, giving the map's moveend time to fire. */
const CHART_MOVING_RELEASE_MS = 400

export interface MapChartZoomSync {
  mapRef: React.RefObject<MapRef | null>
  chartRef: React.RefObject<ElevationChartHandle | null>
  /** True while the map viewport is being dragged/zoomed (guards feedback loops + hover). */
  mapMoving: React.RefObject<boolean>
  /** True while the chart is panned/zoomed, or briefly after it drives the map (guards loops). */
  chartMoving: React.RefObject<boolean>
  onMapMoveStart: () => void
  onMapMoveEnd: () => void
  onChartMoveStart: () => void
  onChartMoveComplete: (min: number, max: number) => void
}

/**
 * Bidirectional zoom/pan sync between the track map viewport and the elevation chart x-range,
 * mirroring biketeam's `leaflet-custom.js` guard-flag mechanics:
 *
 * - Map `moveend` → narrow the chart x-range to the cumulative-distance span of the points still
 *   inside the map viewport (full extent when the viewport shows no track).
 * - Chart pan/zoom complete → fit the map to the points inside the new chart x-range.
 *
 * `mapMoving`/`chartMoving` refs suppress the induced counterpart movement (and let callers pause
 * hover updates mid-gesture); the chart guard is released on a short timer so the map's own
 * `moveend` — which fires during the induced `fitBounds` — doesn't bounce back into the chart.
 */
export function useMapChartZoomSync(route: MappableRoute): MapChartZoomSync {
  const mapRef = useRef<MapRef | null>(null)
  const chartRef = useRef<ElevationChartHandle | null>(null)
  const mapMoving = useRef(false)
  const chartMoving = useRef(false)
  const releaseTimer = useRef<ReturnType<typeof setTimeout> | null>(null)

  const trackPoints = useMemo(
    () => route.tracks.flatMap((track) => track.line.coordinates),
    [route.tracks]
  )
  const totalDistance = route.distance

  const clearReleaseTimer = useCallback(() => {
    if (releaseTimer.current !== null) {
      clearTimeout(releaseTimer.current)
      releaseTimer.current = null
    }
  }, [])

  // Map viewport changed → drive the chart x-range from the visible track span.
  const syncChartToMap = useCallback(() => {
    const map = mapRef.current
    const chart = chartRef.current
    if (!map || !chart) return

    const bounds = map.getBounds()
    const sw = bounds.getSouthWest()
    const ne = bounds.getNorthEast()

    let min = Infinity
    let max = -Infinity
    for (const p of trackPoints) {
      if (p[0] >= sw.lng && p[0] <= ne.lng && p[1] >= sw.lat && p[1] <= ne.lat) {
        if (p[3] < min) min = p[3]
        if (p[3] > max) max = p[3]
      }
    }

    // No track in view → show the full profile.
    if (min === Infinity || max === -Infinity) {
      chart.setXRange(0, totalDistance)
      return
    }

    // Keep a readable minimum window on tiny spans, clamped to the route extent.
    if (max - min < MIN_RANGE_METERS) {
      const center = (min + max) / 2
      min = Math.max(0, center - MIN_RANGE_METERS / 2)
      max = Math.min(totalDistance, center + MIN_RANGE_METERS / 2)
    }

    chart.setXRange(min, max)
  }, [trackPoints, totalDistance])

  const onMapMoveStart = useCallback(() => {
    mapMoving.current = true
  }, [])

  const onMapMoveEnd = useCallback(() => {
    mapMoving.current = false
    // Skip the sync when this move was induced by the chart, to avoid ping-pong.
    if (chartMoving.current) return
    syncChartToMap()
  }, [syncChartToMap])

  const onChartMoveStart = useCallback(() => {
    chartMoving.current = true
    clearReleaseTimer()
  }, [clearReleaseTimer])

  const onChartMoveComplete = useCallback(
    (min: number, max: number) => {
      const map = mapRef.current
      if (map) {
        const pointsInRange = trackPoints.filter((p) => p[3] >= min && p[3] <= max)
        if (pointsInRange.length > 0) {
          map.fitBounds(calculateBounds(pointsInRange), { padding: 50, duration: 300 })
        }
      }
      // Release the guard after the induced map moveend has had time to fire.
      clearReleaseTimer()
      releaseTimer.current = setTimeout(() => {
        chartMoving.current = false
        releaseTimer.current = null
      }, CHART_MOVING_RELEASE_MS)
    },
    [trackPoints, clearReleaseTimer]
  )

  useEffect(() => clearReleaseTimer, [clearReleaseTimer])

  return {
    mapRef,
    chartRef,
    mapMoving,
    chartMoving,
    onMapMoveStart,
    onMapMoveEnd,
    onChartMoveStart,
    onChartMoveComplete,
  }
}
