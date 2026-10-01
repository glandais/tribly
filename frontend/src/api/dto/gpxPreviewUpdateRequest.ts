import type { GeoPoint } from './geoPoint.ts'

/**
 * GPX preview update request
 */
export interface GpxPreviewUpdateRequest {
  /**
   * Preview name
   * @minLength 3
   * @maxLength 250
   * @pattern \S
   */
  name: string
  /**
   * Points from frontend routing
   * @maxItems 100000
   */
  points?: GeoPoint[]
}
