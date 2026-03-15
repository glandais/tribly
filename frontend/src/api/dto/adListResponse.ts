import type { AdDto } from './adDto'

/**
 * Paginated ad list response
 */
export interface AdListResponse {
  /** List of ads */
  ads: AdDto[]
  /** Total number of ads */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
