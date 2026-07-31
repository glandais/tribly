import type { TeamRole } from './teamRole.ts'

/**
 * Invite an e-mail address to join a team
 */
export interface CreateInvitationRequest {
  /**
   * Address to invite. The response is the same whether or not an account already exists for it — only the wording of the e-mail differs. Answering differently would turn this into an account-existence oracle for anyone who administers a team.
   * @maxLength 250
   * @pattern \S
   */
  email: string
  /** Role to grant on acceptance. Defaults to MEMBER. */
  role?: TeamRole
}
