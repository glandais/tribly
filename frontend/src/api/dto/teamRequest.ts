import type { MediaDto } from './mediaDto.ts'
import type { TeamRequestGeometry } from './teamRequestGeometry.ts'
import type { Visibility } from './visibility.ts'

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
  /** Member directory readable by every member, not just administrators. Organisers always see the directory; what this flag adds for them is the role and join date of each member. */
  enableMemberDirectory: boolean
  /** Team location coordinates [longitude, latitude] */
  geometry?: TeamRequestGeometry
}
