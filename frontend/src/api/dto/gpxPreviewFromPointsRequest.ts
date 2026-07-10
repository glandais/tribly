import type { GeoPoint } from './geoPoint.ts'

/**
 * GPX preview creation request from planner points
 */
export interface GpxPreviewFromPointsRequest {
  /**
   * Preview name
   * @minLength 3
   * @maxLength 250
   * @pattern \S
   */
  name: string
  /**
   * Points from frontend routing
   * @minItems 1
   */
  points: GeoPoint[]
}
