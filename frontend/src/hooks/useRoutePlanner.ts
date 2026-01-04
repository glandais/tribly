import { useState, useCallback, useEffect, useRef, useMemo } from 'react'
import {
  add,
  computeRoute,
  createRoute,
  findPreviousControlPointIndex,
  geojson,
  insert,
  Point,
  remove,
  Route,
  RoutePoint,
} from '@/lib/planner'

export interface UseRoutePlannerOptions {
  initialTrack?: number[][] // [lng, lat, ele, dist][] from existing route
}

interface UseRoutePlannerReturn {
  routeGeoJson: GeoJSON.LineString | null
  route: Route
  controlPoints: RoutePoint[]
  isLoading: boolean
  error: string | null
  addControlPoint: (p: Point, direct?: boolean) => void
  insertControlPoint: (afterIndex: number, p: Point, direct?: boolean) => void
  updateControlPoint: (index: number, p: Point, direct?: boolean) => void
  removeControlPoint: (index: number, direct?: boolean) => void
  clearRoute: () => void
  updateZoom: (zoom: number) => void
}

export function useRoutePlanner(options?: UseRoutePlannerOptions): UseRoutePlannerReturn {
  const { initialTrack } = options || {}

  const [route, setRoute] = useState<Route>(
    initialTrack ? createRoute(initialTrack) : computeRoute([])
  )
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [zoom, setZoom] = useState<number>(0)

  const controlPoints = route.points.filter(
    (point, i) =>
      i === 0 ||
      i === route.points.length - 1 ||
      point.manual ||
      (point.anchor && (point.zoom === undefined || zoom > point.zoom))
  )

  const updateZoom = useCallback(
    (zoom: number) => {
      setZoom(zoom)
    },
    [setZoom]
  )

  const routeRef = useRef(route)
  routeRef.current = route

  // Generic executor for all route operations
  const executeRouteOperation = useCallback(async (operation: (route: Route) => Promise<Route>) => {
    setIsLoading(true)
    setError(null)

    try {
      const newRoute = await operation(routeRef.current)
      setRoute(newRoute)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed')
    } finally {
      setIsLoading(false)
    }
  }, [])

  const addControlPoint = useCallback(
    (p: Point, direct = false) => {
      executeRouteOperation((route) => add(route, p, 'fastbike', direct))
    },
    [executeRouteOperation]
  )

  // Insert a new control point after the given index (returns the new point for immediate dragging)
  const insertControlPoint = useCallback(
    (idx: number, p: Point, direct = false) => {
      const cpIdx = findPreviousControlPointIndex(idx, controlPoints)
      const start = controlPoints[cpIdx]
      const end = controlPoints[cpIdx + 1]
      executeRouteOperation((route) => insert(route, start, p, end, 'fastbike', direct))
    },
    [executeRouteOperation, controlPoints]
  )

  const updateControlPoint = useCallback(
    (index: number, p: Point, direct = false) => {
      const start = index === 0 ? undefined : controlPoints[index - 1]
      const end = index === controlPoints.length - 1 ? undefined : controlPoints[index + 1]
      executeRouteOperation((route) => insert(route, start, p, end, 'fastbike', direct))
    },
    [executeRouteOperation, controlPoints]
  )

  const removeControlPoint = useCallback(
    (index: number, direct = false) => {
      const start = index === 0 ? undefined : controlPoints[index - 1]
      const end = index === controlPoints.length - 1 ? undefined : controlPoints[index + 1]
      executeRouteOperation((route) => remove(route, start, end, 'fastbike', direct))
    },
    [executeRouteOperation, controlPoints]
  )

  const clearRoute = useCallback(() => {
    setRoute(computeRoute([]))
    setError(null)
  }, [])

  const routeGeoJson = useMemo(() => {
    if (route.points.length < 2) return null
    return geojson(route)
  }, [route])

  // Calculate route with segment caching
  useEffect(() => {
    if (route.points.length < 2) {
      setError(null)
      return
    }
  }, [route])

  return {
    route,
    controlPoints,
    routeGeoJson,
    isLoading,
    error,
    addControlPoint,
    insertControlPoint,
    updateControlPoint,
    removeControlPoint,
    clearRoute,
    updateZoom,
  }
}
