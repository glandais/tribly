import type { MediaDto } from './mediaDto'
import type { TeamRequestGeometry } from './teamRequestGeometry'
import type { Visibility } from './visibility'

/**
 * Team creation request
 */
export interface TeamRequest {
  /**
   * Team name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Media */
  media: MediaDto
  /** Team visibility */
  visibility: Visibility
  /** Trips enabled for team */
  enableTrips: boolean
  /** Ads enabled for team */
  enableAds: boolean
  /** Posts enabled for team */
  enablePosts: boolean
  /** Rides enabled for team */
  enableRides: boolean
  /** Routes enabled for team */
  enableRoutes: boolean
  /** Team location coordinates [longitude, latitude] */
  geometry?: TeamRequestGeometry
}
