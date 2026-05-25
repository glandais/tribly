import type { LocalTime } from './localTime.ts'

/**
 * Ride group creation request
 */
export interface GroupRequest {
  /** id */
  id?: string
  /**
   * Group name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  time?: LocalTime
  /**
   * Average speed in km/h
   * @exclusiveMinimum 0
   */
  averageSpeed?: number
  /**
   * Maximum participants
   * @exclusiveMinimum 0
   */
  maxParticipants?: number
  /** Route slug for this group */
  routeSlug?: string
}
