import type { TeamRole } from './teamRole.ts'

/**
 * Request to update a member's role
 */
export interface UpdateMemberRoleRequest {
  /** New role */
  role: TeamRole
}
