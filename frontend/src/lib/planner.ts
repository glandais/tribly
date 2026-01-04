import { distance } from '@/components/map/mapUtils'
import { route as routerRoute } from '@/api/endpoints/router/router'
import KDBush from 'kdbush'

export interface Point {
  lng: number
  lat: number
}

export interface RoutePoint {
  id: string
  idx: number
  lng: number
  lat: number
  manual: boolean
  anchor: boolean
  zoom?: number
}

export interface Route {
  points: RoutePoint[]
  dist: number
  index: KDBush
}

let pointIdCounter = 0

export function createRoute(coords: number[][]): Route {
  const points = createPoints(coords)
  return computeRoute(points)
}

function getDist(points: RoutePoint[]): number {
  let dist = 0
  for (let i = 1; i < points.length; i++) {
    const p1 = points[i - 1]
    const p2 = points[i]
    dist = dist + distance(p1.lng, p1.lat, p2.lng, p2.lat)
  }
  return dist
}

function createPoint(lng: number, lat: number): RoutePoint {
  return {
    id: `p-${++pointIdCounter}`,
    idx: 0,
    lng,
    lat,
    manual: false,
    anchor: false,
  }
}

function createPoints(coords: number[][]): RoutePoint[] {
  return coords.map((coord) => createPoint(coord[0], coord[1]))
}

export function computeRoute(input: RoutePoint[]): Route {
  const points = ramerDouglasPeucker(input, 5)
  points.forEach((p, idx) => {
    p.idx = idx
    p.anchor = p.manual
  })
  ramerDouglasPeucker(points, 20)
  const index = new KDBush(points.length)
  for (const { lng, lat } of points) index.add(lng, lat)
  return {
    points,
    dist: getDist(points),
    index: index.finish(),
  }
}

export function findPreviousControlPointIndex(idx: number, controlPoints: RoutePoint[]): number {
  for (let i = 1; i < controlPoints.length; i++) {
    if (idx < controlPoints[i].idx) {
      return i - 1
    }
  }
  return controlPoints.length - 1
}

// add points between route end and p
export async function add(route: Route, p: Point, profile: string, direct = false): Promise<Route> {
  if (route.points.length === 0) {
    const point = createPoint(p.lng, p.lat)
    point.manual = true
    return computeRoute([point])
  }

  const last = route.points[route.points.length - 1]
  last.manual = true

  const segment = await router(last, p, profile, direct)
  if (segment.length > 0) {
    route.points.push(...segment)
  }
  route.points[route.points.length - 1].manual = true

  return computeRoute(route.points)
}

// keep route up to startPoint, compute points between startPoint and p, compute points between p and endPoint, keep route after endPoint
export async function insert(
  route: Route,
  startPoint: RoutePoint | undefined,
  p: Point,
  endPoint: RoutePoint | undefined,
  profile: string,
  direct = false
): Promise<Route> {
  const startIdx = startPoint ? route.points.findIndex((pt) => pt.idx === startPoint.idx) : -1
  const endIdx = endPoint
    ? route.points.findIndex((pt) => pt.idx === endPoint.idx)
    : route.points.length

  // Keep points up to and including start
  const before = route.points.slice(0, startIdx + 1)

  // Route segments (parallel if both needed)
  const [segmentToP, segmentToEnd] = await Promise.all([
    startPoint ? router(startPoint, p, profile, direct) : Promise.resolve([]),
    endPoint ? router(p, endPoint, profile, direct) : Promise.resolve([]),
  ])

  const manualPoint = createPoint(p.lng, p.lat)
  manualPoint.manual = true

  // Keep points after and including end
  const after = route.points.slice(endIdx)

  // Build new points array
  route.points = [...before, ...segmentToP, ...[manualPoint], ...segmentToEnd, ...after]
  return computeRoute(route.points)
}

// keep route up to startPoint, compute points between startPoint and endPoint, keep route after endPoint
export async function remove(
  route: Route,
  startPoint: RoutePoint | undefined,
  endPoint: RoutePoint | undefined,
  profile: string,
  direct = false
): Promise<Route> {
  // Removing first point: just keep from endPoint onwards
  if (!startPoint && endPoint) {
    const endIdx = route.points.findIndex((pt) => pt.idx === endPoint.idx)
    route.points = route.points.slice(endIdx)
    return computeRoute(route.points)
  }

  // Removing last point: just keep up to startPoint
  if (startPoint && !endPoint) {
    const startIdx = route.points.findIndex((pt) => pt.idx === startPoint.idx)
    route.points = route.points.slice(0, startIdx + 1)
    return computeRoute(route.points)
  }

  // Both undefined: clear route
  if (!startPoint && !endPoint) {
    return computeRoute([])
  }

  // Normal case: reroute around removed point
  const startIdx = route.points.findIndex((pt) => pt.idx === startPoint!.idx)
  const endIdx = route.points.findIndex((pt) => pt.idx === endPoint!.idx)

  const before = route.points.slice(0, startIdx + 1)
  const segment = await router(startPoint!, endPoint!, profile, direct)
  const after = route.points.slice(endIdx)

  return computeRoute([...before, ...segment, ...after])
}

export function geojson(route: Route): GeoJSON.LineString {
  return {
    type: 'LineString',
    coordinates: route.points.map((p) => [p.lng, p.lat]),
  }
}

async function router(
  from: Point,
  to: Point,
  profile: string,
  direct: boolean
): Promise<RoutePoint[]> {
  if (direct || distance(from.lng, from.lat, to.lng, to.lat) < 0.005) {
    return [createPoint(to.lng, to.lat)]
  }
  const response = await routerRoute({
    from,
    to,
    profile,
  })
  return createPoints(response.route.coordinates)
}

const earthRadius = 6371008.8

function getZoomLevelForDistance(latitude: number, distance?: number): number {
  if (distance === undefined) {
    return 0
  }

  const rad = Math.PI / 180
  const lat = latitude * rad

  return Math.min(22, Math.max(0, Math.log2((earthRadius * Math.cos(lat)) / distance)))
}

function ramerDouglasPeucker(points: RoutePoint[], epsilon: number = 50): RoutePoint[] {
  if (points.length === 0) {
    return []
  } else if (points.length == 1) {
    return points
  }

  points[0].anchor = true
  points[0].manual = true
  points[0].zoom = 0
  const filtered = [points[0]]
  ramerDouglasPeuckerRecursive(points, filtered, epsilon, 0, points.length - 1)
  points[points.length - 1].anchor = true
  points[points.length - 1].manual = true
  points[points.length - 1].zoom = 0
  filtered.push(points[points.length - 1])
  return filtered
}

function ramerDouglasPeuckerRecursive(
  points: RoutePoint[],
  filtered: RoutePoint[],
  epsilon: number,
  start: number,
  end: number
) {
  const largest = {
    index: 0,
    distance: 0,
  }

  for (let i = start + 1; i < end; i++) {
    const p = points[i]
    const distance = p.manual ? 100000 : crossarc(points[start], points[end], p)
    if (distance > largest.distance) {
      largest.index = i
      largest.distance = distance
    }
  }

  if (largest.distance > epsilon && largest.index != 0) {
    ramerDouglasPeuckerRecursive(points, filtered, epsilon, start, largest.index)
    points[largest.index].anchor = true
    points[largest.index].zoom = getZoomLevelForDistance(
      points[largest.index].lat,
      largest.distance
    )
    filtered.push(points[largest.index])
    ramerDouglasPeuckerRecursive(points, filtered, epsilon, largest.index, end)
  }
}

const metersPerLatitudeDegree = 111320

function getMetersPerLongitudeDegree(latitude: number): number {
  return Math.cos((latitude * Math.PI) / 180) * metersPerLatitudeDegree
}

function crossarc(coord1: RoutePoint, coord2: RoutePoint, coord3: RoutePoint): number {
  // Calculates the perpendicular distance in meters
  // between a line segment (defined by p1 and p2) and a third point, p3.
  // Uses simple planar geometry (ignores earth curvature).

  // Convert to meters using approximate scaling
  const metersPerLongitudeDegree = getMetersPerLongitudeDegree(coord1.lat)

  const x1 = coord1.lng * metersPerLongitudeDegree
  const y1 = coord1.lat * metersPerLatitudeDegree
  const x2 = coord2.lng * metersPerLongitudeDegree
  const y2 = coord2.lat * metersPerLatitudeDegree
  const x3 = coord3.lng * metersPerLongitudeDegree
  const y3 = coord3.lat * metersPerLatitudeDegree

  const dx = x2 - x1
  const dy = y2 - y1
  const segmentLengthSquared = dx * dx + dy * dy

  if (segmentLengthSquared === 0) {
    // p1 and p2 are the same point
    return Math.sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1))
  }

  // Project p3 onto the line defined by p1-p2
  const t = Math.max(0, Math.min(1, ((x3 - x1) * dx + (y3 - y1) * dy) / segmentLengthSquared))

  // Find the closest point on the segment
  const projX = x1 + t * dx
  const projY = y1 + t * dy

  // Return distance from p3 to the projected point
  return Math.sqrt((x3 - projX) * (x3 - projX) + (y3 - projY) * (y3 - projY))
}
