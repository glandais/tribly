import type { ElevationPointDto } from './elevationPointDto.ts'

/**
 * Sampled elevation profile of a route, ready to draw
 */
export interface ElevationProfileDto {
  /** Route ID (TSID) */
  routeId: string
  /** Route slug */
  slug: string
  /** Distance covered by the profile, in meters */
  distance: number
  /** Lowest elevation of the profile, in meters */
  minElevation: number
  /** Highest elevation of the profile, in meters */
  maxElevation: number
  /** Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled. */
  samples: number
  /** Profile points, by increasing distance */
  points: ElevationPointDto[]
}
