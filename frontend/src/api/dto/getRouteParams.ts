export type GetRouteParams = {
  /**
   * Maximum number of track points to return. The points kept are those deviating most from the simplified line — corners and elevation extrema survive, straight flat stretches are dropped — and the first and last points are always kept. Applied after 'simplify' when both are given. Absent, zero or a value larger than the stored track means no decimation.
   */
  points?: number
  /**
   * Douglas-Peucker tolerance in meters: drop every track point lying closer than this to the line joining the points kept around it. The returned line stays within that many meters of the stored one, and its first and last points are always kept. Capped at 1000; absent or zero means no simplification.
   */
  simplify?: number
}
