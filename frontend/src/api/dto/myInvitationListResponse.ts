import type { MyInvitationDto } from './myInvitationDto.ts'

/**
 * The invitations waiting for the current user
 */
export interface MyInvitationListResponse {
  /** Invitations, newest first */
  invitations: MyInvitationDto[]
}
