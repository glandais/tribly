import type { PublicUserDto } from './publicUserDto.ts'

/**
 * Paginated list of the people registered to a ride, a ride group or a trip
 */
export interface ParticipantListResponse {
  /** Participants of this page, in registration order (earliest first) */
  participants: PublicUserDto[]
  /** Number of participants matching the search, over every page — the M of « N of M » */
  total: number
  /** Current page number (0-based) */
  page: number
  /** Page size actually applied */
  size: number
}
