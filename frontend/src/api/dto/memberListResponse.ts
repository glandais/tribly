import type { MemberDto } from './memberDto.ts'

/**
 * Paginated member list response
 */
export interface MemberListResponse {
  /** List of members */
  members: MemberDto[]
  /** Total number of members */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
