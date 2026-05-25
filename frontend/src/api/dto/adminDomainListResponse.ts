import type { AdminDomainDto } from './adminDomainDto.ts'

/**
 * Paginated admin domain list response
 */
export interface AdminDomainListResponse {
  /** List of domains */
  domains: AdminDomainDto[]
  /** Total number of domains */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
