import type { MemberListResponse } from './memberListResponse.ts'
import type { TeamWebhookDto } from './teamWebhookDto.ts'

/**
 * The administration panel of a team dashboard. The split of the members per role is team.memberCountByRole, the enabled modules the team's enable* flags.
 */
export interface TeamDashboardAdminDto {
  /** The newest members, latest joined first (at most 3), with role and joinedAt. total is the member count. */
  newestMembers: MemberListResponse
  /** The team's webhook: configured, kind, enabled, lastStatus, lastAttemptAt. The URL only comes masked. */
  webhook: TeamWebhookDto
}
