import * as zod from 'zod'

/**
 * Administrators only. This is where a mistyped address shows up: since the creation call cannot tell the admin that an address is unknown, the pending list is what makes a typo visible and revocable.
 * @summary List a team's invitations
 */
export const ListInvitationsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const listInvitationsQueryPageDefault = 0
export const listInvitationsQuerySizeDefault = 20

export const ListInvitationsQueryParams = zod.object({
  page: zod.number().default(listInvitationsQueryPageDefault).describe('Page number'),
  size: zod.number().default(listInvitationsQuerySizeDefault).describe('Page size'),
  status: zod
    .enum(['PENDING', 'ACCEPTED', 'REVOKED', 'EXPIRED'])
    .optional()
    .describe('Filter by status'),
})

export const ListInvitationsResponse = zod
  .object({
    invitations: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Invitation ID (TSID)'),
            email: zod.string().describe('Invited e-mail address'),
            role: zod.enum(['MEMBER', 'ORGANIZER', 'ADMIN']).describe('Role granted on acceptance'),
            status: zod
              .enum(['PENDING', 'ACCEPTED', 'REVOKED', 'EXPIRED'])
              .describe('Where the invitation stands'),
            expiresAt: zod.iso
              .datetime({ offset: true })
              .describe('When the invitation stops being redeemable'),
            createdAt: zod.iso.datetime({ offset: true }).describe('When the invitation was sent'),
            invitedBy: zod
              .object({
                id: zod.string().describe('User ID (TSID)'),
                displayName: zod.string().describe('User display name'),
                avatarUrl: zod.string().optional().describe('User avatar URL'),
              })
              .describe('Who sent it'),
            acceptedAt: zod.iso
              .datetime({ offset: true })
              .optional()
              .describe('When it was accepted, if it was'),
          })
          .describe('A pending or settled invitation to join a team')
      )
      .describe('Invitations'),
    total: zod.number().describe('Total number of invitations'),
    page: zod.number().describe('Current page (0-indexed)'),
    size: zod.number().describe('Page size'),
  })
  .describe("Paginated list of a team's invitations")

/**
 * Sends an invitation to join the team. The response is the same whether or not an account already exists for the address — only the wording of the e-mail differs — because answering differently would give anyone who administers a team an account-existence probe for the whole domain. Re-inviting the same address is not an error: the previous pending invitation is revoked and a fresh one issued, which is how 'resend' works. Nobody becomes a member until they accept.
 * @summary Invite an e-mail address
 */
export const InviteParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const inviteBodyEmailMax = 250

export const inviteBodyEmailRegExp = new RegExp('\\S')

export const InviteBody = zod
  .object({
    email: zod
      .string()
      .max(inviteBodyEmailMax)
      .regex(inviteBodyEmailRegExp)
      .describe(
        'Address to invite. The response is the same whether or not an account already exists for it — only the wording of the e-mail differs. Answering differently would turn this into an account-existence oracle for anyone who administers a team.'
      ),
    role: zod
      .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
      .optional()
      .describe('Role to grant on acceptance. Defaults to MEMBER.'),
  })
  .describe('Invite an e-mail address to join a team')

export const InviteResponse = zod
  .object({
    id: zod.string().describe('Invitation ID (TSID)'),
    email: zod.string().describe('Invited e-mail address'),
    role: zod.enum(['MEMBER', 'ORGANIZER', 'ADMIN']).describe('Role granted on acceptance'),
    status: zod
      .enum(['PENDING', 'ACCEPTED', 'REVOKED', 'EXPIRED'])
      .describe('Where the invitation stands'),
    expiresAt: zod.iso
      .datetime({ offset: true })
      .describe('When the invitation stops being redeemable'),
    createdAt: zod.iso.datetime({ offset: true }).describe('When the invitation was sent'),
    invitedBy: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
      })
      .describe('Who sent it'),
    acceptedAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When it was accepted, if it was'),
  })
  .describe('A pending or settled invitation to join a team')

/**
 * Withdraws a pending invitation. This is also the recourse when an invitation should no longer stand — the inviter having left the team, say — since acceptance does not re-check who sent it.
 * @summary Revoke an invitation
 */
export const RevokeParams = zod.object({
  invitationId: zod.string().describe('Invitation ID (TSID)'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const RevokeResponse = zod.void()
