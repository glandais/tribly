import type { RideDto } from './rideDto.ts'

/**
 * Paginated ride list response
 */
export interface RideListResponse {
  /** List of rides */
  rides: RideDto[]
  /** Total number of rides */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
