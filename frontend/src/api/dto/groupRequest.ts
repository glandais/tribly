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
  /** ID (TSID) of the member who leads this group. Must belong to the team owning the ride. Omit or send null for no designated leader — clients then show no leader at all rather than falling back on the ride's creator. */
  leaderId?: string
}
