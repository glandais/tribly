import type { Instant } from './instant.ts'
import type { InvitationStatus } from './invitationStatus.ts'
import type { PublicUserDto } from './publicUserDto.ts'
import type { TeamRole } from './teamRole.ts'

/**
 * A pending or settled invitation to join a team
 */
export interface TeamInvitationDto {
  /** Invitation ID (TSID) */
  id: string
  /** Invited e-mail address */
  email: string
  /** Role granted on acceptance */
  role: TeamRole
  /** Where the invitation stands */
  status: InvitationStatus
  /** When the invitation stops being redeemable */
  expiresAt: Instant
  /** When the invitation was sent */
  createdAt: Instant
  /** Who sent it */
  invitedBy: PublicUserDto
  /** When it was accepted, if it was */
  acceptedAt?: Instant
}
