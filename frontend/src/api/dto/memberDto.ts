import type { Instant } from './instant.ts'
import type { PublicUserDto } from './publicUserDto.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { TeamRole } from './teamRole.ts'

/**
 * Team member information
 */
export interface MemberDto {
  /** Team */
  team: TeamPublicationDto
  /** Membership ID (TSID) */
  id: string
  /** User */
  user: PublicUserDto
  /** Member role. Null when the caller is not entitled to it: an organiser reading the roster of a team that has not opened its member directory gets the names and nothing else. */
  role?: TeamRole
  /** When the user joined the team */
  joinedAt?: Instant
}
