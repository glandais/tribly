import type { TeamInvitationDto } from './teamInvitationDto.ts'

/**
 * Paginated list of a team's invitations
 */
export interface TeamInvitationListResponse {
  /** Invitations */
  invitations: TeamInvitationDto[]
  /** Total number of invitations */
  total: number
  /** Current page (0-indexed) */
  page: number
  /** Page size */
  size: number
}
