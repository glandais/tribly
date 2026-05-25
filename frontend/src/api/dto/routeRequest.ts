import type { GeoPoint } from './geoPoint.ts'
import type { MediaDto } from './mediaDto.ts'
import type { SurfaceType } from './surfaceType.ts'
import type { Visibility } from './visibility.ts'

/**
 * Route update request
 */
export interface RouteRequest {
  /**
   * Route name
   * @minLength 3
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Media */
  media: MediaDto
  /** Surface type */
  surfaceType: SurfaceType
  /** Whether the route is publicly visible */
  visibility: Visibility
  /** Points from frontend routing */
  points?: GeoPoint[]
}
