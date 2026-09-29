import type { TeamRole } from './teamRole.ts'

export type GetMembersParams = {
  /**
   * Page number
   */
  page?: number
  /**
   * Filter by role. Only for a caller who gets the roles (an administrator, or anyone once the directory is open): 403 otherwise.
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
