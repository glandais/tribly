import type { Instant } from './instant'
import type { PublicUserDto } from './publicUserDto'
import type { TeamPublicationDto } from './teamPublicationDto'
import type { TeamRole } from './teamRole'

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
  /** Member role */
  role: TeamRole
  /** When the user joined the team */
  joinedAt?: Instant
}
