import type { PublicationDto } from './publicationDto.ts'

/**
 * Paginated publication list response
 */
export interface PublicationListResponse {
  /** List of publications */
  publications: PublicationDto[]
  /** Total number of publications */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
