import type { TeamRole } from './teamRole.ts'

export type GetMembersParams = {
  /**
   * Page number
   */
  page?: number
  /**
   * Filter by role
   */
  role?: TeamRole
  /**
   * Search by name or email
   */
  search?: string
  /**
   * Page size
   */
  size?: number
}
