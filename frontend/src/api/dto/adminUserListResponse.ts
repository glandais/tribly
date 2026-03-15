import type { AdminUserDto } from './adminUserDto'

/**
 * Paginated admin user list response
 */
export interface AdminUserListResponse {
  /** List of users */
  users: AdminUserDto[]
  /** Total number of users */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
