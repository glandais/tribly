import type { AdminTeamDto } from './adminTeamDto.ts'

/**
 * Paginated admin team list response
 */
export interface AdminTeamListResponse {
  /** List of teams */
  teams: AdminTeamDto[]
  /** Total number of teams */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
