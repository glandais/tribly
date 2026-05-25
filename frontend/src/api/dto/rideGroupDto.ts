import type { LocalTime } from './localTime.ts'
import type { PublicUserDto } from './publicUserDto.ts'

/**
 * Ride group information
 */
export interface RideGroupDto {
  /** Group ID (TSID) */
  id: string
  /** Group name */
  name: string
  time?: LocalTime
  /** Route slug */
  routeSlug?: string
  /** Average speed in km/h */
  averageSpeed?: number
  /** Maximum participants */
  maxParticipants?: number
  /** Current number of participants */
  countParticipants: number
  /** Participants, empty if not access */
  participants: PublicUserDto[]
  /** Sort order */
  sortOrder: number
}
