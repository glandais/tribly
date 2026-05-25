import type { TeamRole } from './teamRole.ts'

/**
 * Request to add a member to the team
 */
export interface AddMemberRequest {
  /** User ID (TSID) to add */
  userId: string
  /** Role to assign (defaults to MEMBER) */
  role?: TeamRole
}
