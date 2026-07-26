/**
 * One point of an elevation profile
 */
export interface ElevationPointDto {
  /** Cumulative distance from the start of the route, in meters */
  distance: number
  /** Elevation above sea level, in meters */
  elevation: number
  /** Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment. */
  grade: number
}
