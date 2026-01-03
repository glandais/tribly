import { useState, useCallback, useEffect, useRef } from 'react'
import { simplifyTrack } from '../lib/douglasPeucker'
import { routerApi, unwrapResponse } from '../lib/apiClient'

export interface ControlPoint {
  id: string
  lng: number
  lat: number
}

let pointIdCounter = 0

export function createControlPoint(lng: number, lat: number): ControlPoint {
  return {
    id: `cp-${++pointIdCounter}`,
    lng,
    lat,
  }
}

interface CachedSegment {
  fromId: string
  toId: string
  fromCoords: { lng: number; lat: number }
  toCoords: { lng: number; lat: number }
  geojson: GeoJSON.FeatureCollection
}

export interface UseRoutePlannerOptions {
  initialTrack?: number[][] // [lng, lat, ele, dist][] from existing route
  epsilon?: number // Douglas-Peucker tolerance in meters (default: 200m)
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

/**
 * Convert a slice of track coordinates to a GeoJSON FeatureCollection
 * Compatible with BRouter output format
 */
function trackSliceToGeoJson(coords: number[][]): GeoJSON.FeatureCollection {
  // Calculate track-length from distance values (last - first)
  const trackLength =
    coords.length >= 2 ? (coords[coords.length - 1][3] || 0) - (coords[0][3] || 0) : 0

  // Calculate plain-ascend (positive elevation changes)
  let plainAscend = 0
  for (let i = 1; i < coords.length; i++) {
    const elevDiff = (coords[i][2] || 0) - (coords[i - 1][2] || 0)
    if (elevDiff > 0) {
      plainAscend += elevDiff
    }
  }

  return {
    type: 'FeatureCollection',
    features: [
      {
        type: 'Feature',
        properties: {
          'track-length': trackLength,
          'plain-ascend': plainAscend,
        },
        geometry: {
          type: 'LineString',
          coordinates: coords.map((c) => [c[0], c[1]]), // [lng, lat] only
        },
      },
    ],
  }
}

export function useRoutePlanner(options?: UseRoutePlannerOptions): UseRoutePlannerReturn {
  const { initialTrack, epsilon = 200 } = options || {}

  const [controlPoints, setControlPoints] = useState<ControlPoint[]>([])
  const [routeGeoJson, setRouteGeoJson] = useState<RouteFeatureCollection | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Cache of calculated segments
  const segmentCacheRef = useRef<Map<string, CachedSegment>>(new Map())

  // Track request ID for race condition handling
  const requestIdRef = useRef(0)

  // Track if we've initialized from initial track
  const initializedRef = useRef(false)

  // Initialize from existing track (runs once on mount if initialTrack provided)
  useEffect(() => {
    if (!initialTrack || initialTrack.length < 2 || initializedRef.current) {
      return
    }

    initializedRef.current = true

    // Apply Douglas-Peucker to get simplified point indices
    const indices = simplifyTrack(initialTrack, epsilon)

    if (indices.length < 2) {
      return
    }

    // Create control points from simplified indices
    const points: ControlPoint[] = indices.map((idx) => {
      const coord = initialTrack[idx]
      return createControlPoint(coord[0], coord[1])
    })

    // Pre-populate cache with segments extracted from original track
    const cache = segmentCacheRef.current
    for (let i = 0; i < indices.length - 1; i++) {
      const fromIdx = indices[i]
      const toIdx = indices[i + 1]
      const from = points[i]
      const to = points[i + 1]

      // Extract the slice of coordinates between these control points
      const sliceCoords = initialTrack.slice(fromIdx, toIdx + 1)
      const geojson = trackSliceToGeoJson(sliceCoords)

      const key = getSegmentKey(from.id, to.id)
      cache.set(key, {
        fromId: from.id,
        toId: to.id,
        fromCoords: { lng: from.lng, lat: from.lat },
        toCoords: { lng: to.lng, lat: to.lat },
        geojson,
      })
    }

    // Set control points (this will trigger the main effect to merge segments)
    setControlPoints(points)
  }, [initialTrack, epsilon])

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
            const response = await unwrapResponse(
              routerApi.route({
                from: { lng: from.lng, lat: from.lat },
                to: { lng: to.lng, lat: to.lat },
              })
            )
            // Convert RouterResponse to GeoJSON FeatureCollection
            const geojson: GeoJSON.FeatureCollection = {
              type: 'FeatureCollection',
              features: [
                {
                  type: 'Feature',
                  properties: {
                    'track-length': response.dist ?? 0,
                    'plain-ascend': response.ascend ?? 0,
                  },
                  geometry: {
                    type: 'LineString',
                    coordinates: response.route.coordinates,
                  },
                },
              ],
            }
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
