/**
 * Number of members of the team per role. The three figures add up to the team's memberCount.
 */
export interface MemberCountByRoleDto {
  /** Members with the ADMIN role */
  admins: number
  /** Members with the ORGANIZER role */
  organizers: number
  /** Members with the MEMBER role */
  members: number
}
