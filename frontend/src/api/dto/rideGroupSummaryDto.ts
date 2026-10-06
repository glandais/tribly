import type { LocalTime } from './localTime.ts'

/**
 * One group of a ride, as a list row shows it: name, pace and fill. Same figures as the matching entry of the detail's groups, without the participants nor the leader.
 */
export interface RideGroupSummaryDto {
  /** Group ID (TSID) */
  id: string
  /** Group name */
  name: string
  /** Start time of the group, when it differs from the ride's */
  time?: LocalTime
  /** Average speed in km/h */
  averageSpeed?: number
  /** Current number of participants */
  countParticipants: number
  /** Maximum participants, null when the group is uncapped */
  maxParticipants?: number
  /** Whether the group has reached maxParticipants. False when maxParticipants is not set. */
  full: boolean
  /** Slug of the group route, if it has one */
  routeSlug?: string
  /** Distance in meters of the group route, if it has one */
  distance?: number
  /** Total elevation gain in meters of the group route, if it has one */
  elevationGain?: number
  /** Sort order */
  sortOrder: number
}
