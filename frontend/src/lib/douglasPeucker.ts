/**
 * Douglas-Peucker line simplification algorithm
 * Returns indices of points to keep (always includes first and last)
 */

interface Point {
  lng: number
  lat: number
}

/**
 * Calculate perpendicular distance from a point to a line defined by two points
 */
function perpendicularDistance(point: Point, lineStart: Point, lineEnd: Point): number {
  const dx = lineEnd.lng - lineStart.lng
  const dy = lineEnd.lat - lineStart.lat

  // If line is a point, return distance to that point
  const lineLengthSq = dx * dx + dy * dy
  if (lineLengthSq === 0) {
    const pdx = point.lng - lineStart.lng
    const pdy = point.lat - lineStart.lat
    return Math.sqrt(pdx * pdx + pdy * pdy)
  }

  // Calculate perpendicular distance using cross product
  const numerator = Math.abs(
    dy * point.lng - dx * point.lat + lineEnd.lng * lineStart.lat - lineEnd.lat * lineStart.lng
  )
  const denominator = Math.sqrt(lineLengthSq)

  return numerator / denominator
}

/**
 * Douglas-Peucker algorithm implementation
 * @param points Array of points with lng/lat coordinates
 * @param epsilon Tolerance distance (in degrees, ~0.0005 ≈ 50m)
 * @returns Array of indices of points to keep
 */
export function douglasPeucker(points: Point[], epsilon: number): number[] {
  if (points.length <= 2) {
    return points.map((_, i) => i)
  }

  // Find the point with maximum distance from the line between first and last
  let maxDist = 0
  let maxIndex = 0

  for (let i = 1; i < points.length - 1; i++) {
    const dist = perpendicularDistance(points[i], points[0], points[points.length - 1])
    if (dist > maxDist) {
      maxDist = dist
      maxIndex = i
    }
  }

  // If max distance is greater than epsilon, recursively simplify
  if (maxDist > epsilon) {
    // Recursively simplify left and right segments
    const leftIndices = douglasPeucker(points.slice(0, maxIndex + 1), epsilon)
    const rightIndices = douglasPeucker(points.slice(maxIndex), epsilon)

    // Combine results, avoiding duplicate at maxIndex
    return [...leftIndices.slice(0, -1), ...rightIndices.map((i) => i + maxIndex)]
  }

  // If no point exceeds epsilon, keep only endpoints
  return [0, points.length - 1]
}

/**
 * Simplify a track using Douglas-Peucker algorithm
 * @param coords Array of [lng, lat, ...] coordinates
 * @param epsilon Tolerance in degrees (default ~50m)
 * @returns Array of indices to keep
 */
export function simplifyTrack(coords: number[][], epsilon: number = 0.0005): number[] {
  const points = coords.map((c) => ({ lng: c[0], lat: c[1] }))
  return douglasPeucker(points, epsilon)
}
