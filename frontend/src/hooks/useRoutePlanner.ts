import { useState, useCallback, useEffect, useRef } from 'react'
import { calculateSegment, createControlPoint, ControlPoint } from '../lib/brouterClient'

export type { ControlPoint } from '../lib/brouterClient'

interface CachedSegment {
  fromId: string
  toId: string
  fromCoords: { lng: number; lat: number }
  toCoords: { lng: number; lat: number }
  geojson: GeoJSON.FeatureCollection
}

interface UseRoutePlannerReturn {
  controlPoints: ControlPoint[]
  routeGeoJson: RouteFeatureCollection | null
  isLoading: boolean
  error: string | null
  addControlPoint: (lng: number, lat: number) => void
  insertControlPoint: (afterIndex: number, lng: number, lat: number) => ControlPoint
  updateControlPoint: (index: number, lng: number, lat: number) => void
  removeControlPoint: (index: number) => void
  clearRoute: () => void
}

function getSegmentKey(fromId: string, toId: string): string {
  return `${fromId}-${toId}`
}

export interface RouteFeatureCollection extends GeoJSON.FeatureCollection {
  properties: {
    'track-length': number
    'plain-ascend': number
  }
}

function mergeSegments(segments: GeoJSON.FeatureCollection[]): RouteFeatureCollection {
  if (segments.length === 0) {
    return {
      type: 'FeatureCollection',
      properties: { 'track-length': 0, 'plain-ascend': 0 },
      features: [],
    }
  }

  // Collect all features and compute totals
  let totalLength = 0
  let totalAscend = 0
  const features: GeoJSON.Feature[] = []

  for (let segmentIndex = 0; segmentIndex < segments.length; segmentIndex++) {
    const segment = segments[segmentIndex]
    const feature = segment.features[0]
    if (feature) {
      const props = feature.properties || {}
      totalLength += (props['track-length'] as number) || 0
      totalAscend += (props['plain-ascend'] as number) || 0
      // Add segmentIndex to feature properties for hover/insert detection
      features.push({
        ...feature,
        properties: {
          ...props,
          segmentIndex,
        },
      })
    }
  }

  return {
    type: 'FeatureCollection',
    properties: {
      'track-length': totalLength,
      'plain-ascend': totalAscend,
    },
    features,
  }
}

export function useRoutePlanner(): UseRoutePlannerReturn {
  const [controlPoints, setControlPoints] = useState<ControlPoint[]>([])
  const [routeGeoJson, setRouteGeoJson] = useState<RouteFeatureCollection | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Cache of calculated segments
  const segmentCacheRef = useRef<Map<string, CachedSegment>>(new Map())

  // Track request ID for race condition handling
  const requestIdRef = useRef(0)

  const addControlPoint = useCallback((lng: number, lat: number) => {
    setControlPoints((prev) => [...prev, createControlPoint(lng, lat)])
  }, [])

  // Insert a new control point after the given index (returns the new point for immediate dragging)
  const insertControlPoint = useCallback((afterIndex: number, lng: number, lat: number) => {
    const newPoint = createControlPoint(lng, lat)
    setControlPoints((prev) => {
      const newPoints = [...prev]
      newPoints.splice(afterIndex + 1, 0, newPoint)
      return newPoints
    })
    return newPoint
  }, [])

  const updateControlPoint = useCallback((index: number, lng: number, lat: number) => {
    setControlPoints((prev) =>
      prev.map((point, i) => (i === index ? { ...point, lng, lat } : point))
    )
  }, [])

  const removeControlPoint = useCallback((index: number) => {
    setControlPoints((prev) => prev.filter((_, i) => i !== index))
  }, [])

  const clearRoute = useCallback(() => {
    setControlPoints([])
    setRouteGeoJson(null)
    setError(null)
    segmentCacheRef.current.clear()
  }, [])

  // Calculate route with segment caching
  useEffect(() => {
    if (controlPoints.length < 2) {
      setRouteGeoJson(null)
      setError(null)
      return
    }

    const currentRequestId = ++requestIdRef.current
    const cache = segmentCacheRef.current

    const calculateWithCache = async () => {
      setIsLoading(true)
      setError(null)

      try {
        // Determine which segments need calculation
        const segmentsToFetch: { index: number; from: ControlPoint; to: ControlPoint }[] = []
        const validSegmentKeys: string[] = []

        for (let i = 0; i < controlPoints.length - 1; i++) {
          const from = controlPoints[i]
          const to = controlPoints[i + 1]
          const key = getSegmentKey(from.id, to.id)
          validSegmentKeys.push(key)

          const cached = cache.get(key)

          // Check if cached segment is still valid (same coordinates)
          if (
            cached &&
            cached.fromCoords.lng === from.lng &&
            cached.fromCoords.lat === from.lat &&
            cached.toCoords.lng === to.lng &&
            cached.toCoords.lat === to.lat
          ) {
            // Cache hit - segment is still valid
            continue
          }

          // Need to fetch this segment
          segmentsToFetch.push({ index: i, from, to })
        }

        // Clean up old cache entries that are no longer in the route
        for (const key of cache.keys()) {
          if (!validSegmentKeys.includes(key)) {
            cache.delete(key)
          }
        }

        // Fetch missing segments in parallel
        if (segmentsToFetch.length > 0) {
          const fetchPromises = segmentsToFetch.map(async ({ from, to }) => {
            const geojson = await calculateSegment(from, to)
            return { from, to, geojson }
          })

          const results = await Promise.all(fetchPromises)

          // Check if request is still current
          if (currentRequestId !== requestIdRef.current) return

          // Update cache with new segments
          for (const { from, to, geojson } of results) {
            const key = getSegmentKey(from.id, to.id)
            cache.set(key, {
              fromId: from.id,
              toId: to.id,
              fromCoords: { lng: from.lng, lat: from.lat },
              toCoords: { lng: to.lng, lat: to.lat },
              geojson,
            })
          }
        }

        // Check if request is still current
        if (currentRequestId !== requestIdRef.current) return

        // Merge all segments in order
        const orderedSegments: GeoJSON.FeatureCollection[] = []
        for (let i = 0; i < controlPoints.length - 1; i++) {
          const from = controlPoints[i]
          const to = controlPoints[i + 1]
          const key = getSegmentKey(from.id, to.id)
          const cached = cache.get(key)
          if (cached) {
            orderedSegments.push(cached.geojson)
          }
        }

        const mergedRoute = mergeSegments(orderedSegments)
        setRouteGeoJson(mergedRoute)
      } catch (err) {
        if (currentRequestId === requestIdRef.current) {
          setError(err instanceof Error ? err.message : 'Failed to calculate route')
          setRouteGeoJson(null)
        }
      } finally {
        if (currentRequestId === requestIdRef.current) {
          setIsLoading(false)
        }
      }
    }

    calculateWithCache()
  }, [controlPoints])

  return {
    controlPoints,
    routeGeoJson,
    isLoading,
    error,
    addControlPoint,
    insertControlPoint,
    updateControlPoint,
    removeControlPoint,
    clearRoute,
  }
}
