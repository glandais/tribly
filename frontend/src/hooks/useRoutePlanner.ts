import { useState, useCallback, useEffect, useRef, useMemo } from 'react'
import type { LngLatBounds } from 'maplibre-gl'
import {
  add,
  computeRoute,
  createRoute,
  geojson,
  insert,
  Point,
  remove,
  Route,
  RoutePoint,
} from '@/lib/planner'
import { RouterProfile } from '@/api/dto'

export interface UseRoutePlannerOptions {
  initialTrack?: number[][] // [lng, lat, ele, dist][] from existing route
}

interface UseRoutePlannerReturn {
  routeGeoJson: GeoJSON.LineString | null
  route: Route
  controlPoints: RoutePoint[]
  isLoading: boolean
  error: string | null
  canUndo: boolean
  canRedo: boolean
  addControlPoint: (p: Point, direct?: boolean) => void
  insertControlPoint: (
    start: RoutePoint | undefined,
    p: Point,
    end: RoutePoint | undefined,
    direct?: boolean
  ) => void
  updateControlPoint: (
    start: RoutePoint | undefined,
    p: Point,
    end: RoutePoint | undefined,
    direct?: boolean
  ) => void
  removeControlPoint: (index: number, direct?: boolean, bounds?: LngLatBounds | null) => void
  clearRoute: () => void
  undo: () => void
  redo: () => void
}

/**
 * Find the start boundary for a bbox-scoped operation.
 * Walks backward through route.points from fromRouteIdx,
 * returns the first route point still inside the bbox.
 * @param fromRouteIdx - route index to walk backward from (defaults to the control point's route index)
 */
export function findBboxStartPoint(
  route: Route,
  fromRouteIdx: number,
  bounds: LngLatBounds | null | undefined
): RoutePoint | undefined {
  if (fromRouteIdx <= 0 || !bounds) return undefined

  for (let i = fromRouteIdx - 1; i >= 0; i--) {
    const p = route.points[i]
    if (!bounds.contains([p.lng, p.lat])) {
      return route.points[Math.min(i + 1, fromRouteIdx - 1)]
    }
    if (p.manual) {
      return p
    }
  }

  return route.points[0]
}

/**
 * Find the end boundary for a bbox-scoped operation.
 * Walks forward through route.points from fromRouteIdx,
 * returns the last route point still inside the bbox.
 * @param fromRouteIdx - route index to walk forward from (defaults to the control point's route index)
 */
export function findBboxEndPoint(
  route: Route,
  fromRouteIdx: number,
  bounds: LngLatBounds | null | undefined
): RoutePoint | undefined {
  if (fromRouteIdx >= route.points.length - 1 || !bounds) return undefined

  for (let i = fromRouteIdx + 1; i < route.points.length; i++) {
    const p = route.points[i]
    if (!bounds.contains([p.lng, p.lat])) {
      return route.points[Math.max(i - 1, fromRouteIdx + 1)]
    }
    if (p.manual) {
      return p
    }
  }

  return route.points[route.points.length - 1]
}

export function useRoutePlanner(options?: UseRoutePlannerOptions): UseRoutePlannerReturn {
  const { initialTrack } = options || {}
  const routerProfile = RouterProfile.FASTBIKE

  const [route, setRoute] = useState<Route>(
    initialTrack ? createRoute(initialTrack) : computeRoute([])
  )
  const [history, setHistory] = useState<Route[]>([])
  const [future, setFuture] = useState<Route[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const canUndo = history.length > 0
  const canRedo = future.length > 0

  const controlPoints = route.points.filter(
    (point, i) => i === 0 || i === route.points.length - 1 || point.manual
  )

  const routeRef = useRef(route)
  routeRef.current = route

  // Generic executor for all route operations
  const executeRouteOperation = useCallback(async (operation: (route: Route) => Promise<Route>) => {
    setIsLoading(true)
    setError(null)

    // Snapshot before the operation mutates the current route's points
    const snapshot: Route = {
      ...routeRef.current,
      points: routeRef.current.points.map((p) => ({ ...p })),
    }

    try {
      const newRoute = await operation(routeRef.current)
      setHistory((prev) => [...prev, snapshot])
      setFuture([])
      setRoute(newRoute)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed')
    } finally {
      setIsLoading(false)
    }
  }, [])

  const addControlPoint = useCallback(
    (p: Point, direct = false) => {
      executeRouteOperation((route) => add(route, p, routerProfile, direct))
    },
    [executeRouteOperation, routerProfile]
  )

  // Insert a new control point after the given index (returns the new point for immediate dragging)
  const insertControlPoint = useCallback(
    (start: RoutePoint | undefined, p: Point, end: RoutePoint | undefined, direct?: boolean) => {
      executeRouteOperation((route) => insert(route, start, p, end, routerProfile, direct))
    },
    [executeRouteOperation, routerProfile]
  )

  const updateControlPoint = useCallback(
    (start: RoutePoint | undefined, p: Point, end: RoutePoint | undefined, direct?: boolean) => {
      executeRouteOperation((route) => insert(route, start, p, end, routerProfile, direct))
    },
    [executeRouteOperation, routerProfile]
  )

  const removeControlPoint = useCallback(
    (index: number, direct = false, bounds?: LngLatBounds | null) => {
      const start = findBboxStartPoint(route, index, bounds)
      const end = findBboxEndPoint(route, index, bounds)
      executeRouteOperation((route) => remove(route, start, end, routerProfile, direct))
    },
    [executeRouteOperation, route, routerProfile]
  )

  const clearRoute = useCallback(() => {
    const snapshot: Route = {
      ...routeRef.current,
      points: routeRef.current.points.map((p) => ({ ...p })),
    }
    setHistory((prev) => [...prev, snapshot])
    setFuture([])
    setRoute(computeRoute([]))
    setError(null)
  }, [])

  const undo = useCallback(() => {
    if (history.length === 0) return
    setFuture((prev) => [routeRef.current, ...prev])
    const prev = history[history.length - 1]
    setHistory((h) => h.slice(0, -1))
    setRoute(prev)
  }, [history])

  const redo = useCallback(() => {
    if (future.length === 0) return
    setHistory((prev) => [...prev, routeRef.current])
    const next = future[0]
    setFuture((f) => f.slice(1))
    setRoute(next)
  }, [future])

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
    canUndo,
    canRedo,
    undo,
    redo,
  }
}
