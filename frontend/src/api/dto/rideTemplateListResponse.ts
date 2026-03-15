import type { RideTemplateDto } from './rideTemplateDto'

/**
 * Paginated ride template list response
 */
export interface RideTemplateListResponse {
  /** List of templates */
  templates: RideTemplateDto[]
  /** Total number of templates */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
