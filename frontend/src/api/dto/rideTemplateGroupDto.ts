import type { LocalTime } from './localTime.ts'

/**
 * Ride template group information
 */
export interface RideTemplateGroupDto {
  /** Group ID (TSID) */
  id: string
  /** Group name */
  name: string
  time?: LocalTime
  /** Average speed in km/h */
  averageSpeed?: number
  /** Maximum participants */
  maxParticipants?: number
  /** Sort order */
  sortOrder: number
}
