import type { Instant } from './instant'

/**
 * Trip participation information
 */
export interface TripParticipationDto {
  /** Participation ID (TSID) */
  id: string
  /** User ID (TSID) */
  userId: string
  /** Registration timestamp */
  registeredAt?: Instant
}
