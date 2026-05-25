import type { TripDto } from './tripDto.ts'

/**
 * Paginated trip list response
 */
export interface TripListResponse {
  /** List of trips */
  trips: TripDto[]
  /** Total number of trips */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
