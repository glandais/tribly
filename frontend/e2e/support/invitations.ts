import type {
  AcceptInvitationRequest,
  CreateInvitationRequest,
  InvitationPreviewDto,
  MemberDto,
  MemberListResponse,
  TeamInvitationDto,
} from '../../src/api/dto'
import { apiContext, expectOk, type AuthResponse } from './api'
import { linkTokenIn, mailbox, waitForNewMail } from './mailhog'

/**
 * Invitation journey helpers: invitations sent through the API with the token read back from
 * mailhog, and the reads a test needs to check membership. A team whose admins may invite is
 * `newTeam(owner, name, { addMemberAllowed: true })` (support/data.ts).
 */

/** Invites `email` through the API and returns the invitation plus the token its mail carried. */
export async function inviteByApi(
  inviter: AuthResponse,
  teamSlug: string,
  email: string
): Promise<{ invitation: TeamInvitationDto; token: string; mail: string }> {
  const api = await apiContext(inviter.accessToken)
  try {
    const seen = await mailbox(email)
    const request: CreateInvitationRequest = { email, role: 'MEMBER' }
    const invitation = await expectOk<TeamInvitationDto>(
      await api.post(`/api/teams/${teamSlug}/invitations`, { data: request })
    )
    const mail = await waitForNewMail(email, seen)
    return { invitation, token: invitationTokenIn(mail), mail }
  } finally {
    await api.dispose()
  }
}

/** The token of the /invitation?token= link in an invitation mail. */
export function invitationTokenIn(mail: string): string {
  if (!/\/invitation\?token=/.test(mail)) throw new Error(`no /invitation link in mail:\n${mail}`)
  return linkTokenIn(mail)
}

/** The public preview of an invitation, as the acceptance page reads it. */
export async function previewInvitation(token: string): Promise<InvitationPreviewDto> {
  const api = await apiContext()
  try {
    const request: AcceptInvitationRequest = { token }
    return await expectOk<InvitationPreviewDto>(
      await api.post('/api/invitations/preview', { data: request })
    )
  } finally {
    await api.dispose()
  }
}

/** The team's members whose account id is `userId` — as the team's owner sees the roster. */
export async function membershipsOf(
  owner: AuthResponse,
  teamSlug: string,
  userId: string
): Promise<MemberDto[]> {
  const api = await apiContext(owner.accessToken)
  try {
    const list = await expectOk<MemberListResponse>(
      await api.get(`/api/teams/${teamSlug}/members`, { params: { size: 100 } })
    )
    return list.members.filter((m) => m.user.id === userId)
  } finally {
    await api.dispose()
  }
}
