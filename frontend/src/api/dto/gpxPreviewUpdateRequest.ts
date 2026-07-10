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
  /** Points from frontend routing */
  points?: GeoPoint[]
}
