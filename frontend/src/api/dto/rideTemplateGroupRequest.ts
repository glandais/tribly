import type { LocalTime } from './localTime'

/**
 * Ride template group request
 */
export interface RideTemplateGroupRequest {
  /** Group ID (TSID) - only for updates */
  id?: string
  /**
   * Group name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  time?: LocalTime
  /** Average speed in km/h */
  averageSpeed?: number
  /** Maximum participants */
  maxParticipants?: number
}
