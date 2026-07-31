import type { InvitationStatus } from './invitationStatus.ts'
import type { TeamRole } from './teamRole.ts'

/**
 * What an invitation says, readable by whoever holds its token
 */
export interface InvitationPreviewDto {
  /** Team name */
  teamName: string
  /** Team URL slug */
  teamSlug: string
  /** Display name of whoever sent the invitation */
  inviterName: string
  /** Invited address, masked (a***@example.com) */
  maskedEmail: string
  /** Role granted on acceptance */
  role: TeamRole
  /** Where the invitation stands */
  status: InvitationStatus
  /** Whether the invitation can still be redeemed. False for anything but a live PENDING one. */
  redeemable: boolean
}
