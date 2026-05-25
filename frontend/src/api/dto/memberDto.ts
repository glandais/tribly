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
  /** Member role */
  role: TeamRole
  /** When the user joined the team */
  joinedAt?: Instant
}
