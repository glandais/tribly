import type { Instant } from './instant.ts'

/**
 * Ride participation information
 */
export interface RideParticipationDto {
  /** Participation ID (TSID) */
  id: string
  /** User ID (TSID) */
  userId: string
  /** Registration timestamp */
  registeredAt?: Instant
}
