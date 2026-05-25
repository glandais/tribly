import type { TeamDetailDto } from './teamDetailDto.ts'

/**
 * Paginated team list response
 */
export interface TeamListResponse {
  /** List of teams */
  teams: TeamDetailDto[]
  /** Total number of teams */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
