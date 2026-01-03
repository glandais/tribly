/**
 * Douglas-Peucker line simplification algorithm
 * Returns indices of points to keep (always includes first and last)
 */

interface Point {
  lng: number
  lat: number
}

interface ProjectedPoint {
  x: number // meters
  y: number // meters
}

// Earth's radius in meters
const EARTH_RADIUS = 6371000

/**
 * Convert lat/lng to projected coordinates (meters) using equirectangular approximation
 * This is accurate enough for local distances and much faster than Haversine
 */
function projectPoint(point: Point, centerLat: number): ProjectedPoint {
  const latRad = (point.lat * Math.PI) / 180
  const lngRad = (point.lng * Math.PI) / 180
  const centerLatRad = (centerLat * Math.PI) / 180

  return {
    x: lngRad * Math.cos(centerLatRad) * EARTH_RADIUS,
    y: latRad * EARTH_RADIUS,
  }
}

/**
 * Calculate perpendicular distance from a point to a line defined by two points
 * Returns distance in meters
 */
function perpendicularDistance(
  point: Point,
  lineStart: Point,
  lineEnd: Point,
  centerLat: number
): number {
  // Project all points to approximate meters
  const p = projectPoint(point, centerLat)
  const ls = projectPoint(lineStart, centerLat)
  const le = projectPoint(lineEnd, centerLat)

  const dx = le.x - ls.x
  const dy = le.y - ls.y

  // If line is a point, return distance to that point
  const lineLengthSq = dx * dx + dy * dy
  if (lineLengthSq === 0) {
    const pdx = p.x - ls.x
    const pdy = p.y - ls.y
    return Math.sqrt(pdx * pdx + pdy * pdy)
  }

  // Calculate perpendicular distance using cross product
  const numerator = Math.abs(dy * p.x - dx * p.y + le.x * ls.y - le.y * ls.x)
  const denominator = Math.sqrt(lineLengthSq)

  return numerator / denominator
}

/**
 * Douglas-Peucker algorithm implementation
 * @param points Array of points with lng/lat coordinates
 * @param epsilon Tolerance distance in meters
 * @param centerLat Center latitude for projection (undefined = calculate from points)
 * @returns Array of indices of points to keep
 */
export function douglasPeucker(
  points: Point[],
  epsilon: number,
  centerLat?: number
): number[] {
  if (points.length <= 2) {
    return points.map((_, i) => i)
  }

  // Calculate center latitude if not provided (for projection accuracy)
  if (centerLat === undefined) {
    centerLat = (points[0].lat + points[points.length - 1].lat) / 2
  }

  // Find the point with maximum distance from the line between first and last
  let maxDist = 0
  let maxIndex = 0

  for (let i = 1; i < points.length - 1; i++) {
    const dist = perpendicularDistance(points[i], points[0], points[points.length - 1], centerLat)
    if (dist > maxDist) {
      maxDist = dist
      maxIndex = i
    }
  }

  // If max distance is greater than epsilon, recursively simplify
  if (maxDist > epsilon) {
    // Recursively simplify left and right segments
    const leftIndices = douglasPeucker(points.slice(0, maxIndex + 1), epsilon, centerLat)
    const rightIndices = douglasPeucker(points.slice(maxIndex), epsilon, centerLat)

    // Combine results, avoiding duplicate at maxIndex
    return [...leftIndices.slice(0, -1), ...rightIndices.map((i) => i + maxIndex)]
  }

  // If no point exceeds epsilon, keep only endpoints
  return [0, points.length - 1]
}

/**
 * Simplify a track using Douglas-Peucker algorithm
 * @param coords Array of [lng, lat, ...] coordinates
 * @param epsilon Tolerance in meters (default 50m)
 * @returns Array of indices to keep
 */
export function simplifyTrack(coords: number[][], epsilon: number = 50): number[] {
  const points = coords.map((c) => ({ lng: c[0], lat: c[1] }))
  return douglasPeucker(points, epsilon)
}
