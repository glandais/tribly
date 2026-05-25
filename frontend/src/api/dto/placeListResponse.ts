import type { PlaceDetailDto } from './placeDetailDto.ts'

/**
 * Paginated place list response
 */
export interface PlaceListResponse {
  /** List of places */
  places: PlaceDetailDto[]
  /** Total number of places */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
