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
   * Search by display name. Also matches the e-mail address, for administrators only.
   */
  search?: string
  /**
   * Page size
   */
  size?: number
}
