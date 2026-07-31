import type { Instant } from './instant.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { TeamRole } from './teamRole.ts'

/**
 * An invitation waiting for the current user
 */
export interface MyInvitationDto {
  /** Invitation ID (TSID) */
  id: string
  /** Team being offered */
  team: TeamPublicationDto
  /** Display name of whoever sent it */
  inviterName: string
  /** Role granted on acceptance */
  role: TeamRole
  /** When it stops being redeemable */
  expiresAt: Instant
}
