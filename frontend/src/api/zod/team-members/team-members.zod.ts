import * as zod from 'zod'

/**
 * Get paginated list of team members
 * @summary Get team members
 */
export const GetMembersParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const getMembersQueryPageDefault = 0
export const getMembersQuerySizeDefault = 50

export const GetMembersQueryParams = zod.object({
  page: zod.number().default(getMembersQueryPageDefault).describe('Page number'),
  size: zod.number().default(getMembersQuerySizeDefault).describe('Page size'),
})

export const GetMembersResponse = zod
  .object({
    members: zod
      .array(
        zod
          .object({
            team: zod
              .object({
                id: zod.string().describe('Team ID (TSID)'),
                name: zod.string().describe('Team name'),
                slug: zod.string().describe('Team URL slug'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Whether the team is public'),
              })
              .describe('Team'),
            id: zod.string().describe('Membership ID (TSID)'),
            user: zod
              .object({
                id: zod.string().describe('User ID (TSID)'),
                displayName: zod.string().describe('User display name'),
                avatarUrl: zod.string().optional().describe('User avatar URL'),
              })
              .describe('User'),
            role: zod.enum(['MEMBER', 'ORGANIZER', 'ADMIN']).describe('Member role'),
            joinedAt: zod.iso.datetime({}).optional().describe('When the user joined the team'),
          })
          .describe('Team member information')
      )
      .describe('List of members'),
    total: zod.number().describe('Total number of members'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated member list response')

/**
 * Add a member to the team. Requires ADMIN role on team.
 * @summary Add team member
 */
export const AddMemberParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const AddMemberBody = zod
  .object({
    userId: zod.string().describe('User ID (TSID) to add'),
    role: zod
      .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
      .optional()
      .describe('Role to assign (defaults to MEMBER)'),
  })
  .describe('Request to add a member to the team')

/**
 * Request to join a team
 * @summary Join team
 */
export const JoinTeamParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

/**
 * Leave a team
 * @summary Leave team
 */
export const LeaveTeamParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

/**
 * Update a team member's role. Requires ADMIN role.
 * @summary Update member role
 */
export const UpdateMemberRoleParams = zod.object({
  memberId: zod.string().describe('Member user ID (TSID)'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const UpdateMemberRoleBody = zod
  .object({
    role: zod.enum(['MEMBER', 'ORGANIZER', 'ADMIN']).describe('New role'),
  })
  .describe("Request to update a member's role")

export const UpdateMemberRoleResponse = zod
  .object({
    team: zod
      .object({
        id: zod.string().describe('Team ID (TSID)'),
        name: zod.string().describe('Team name'),
        slug: zod.string().describe('Team URL slug'),
        visibility: zod
          .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
          .describe('Whether the team is public'),
      })
      .describe('Team'),
    id: zod.string().describe('Membership ID (TSID)'),
    user: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
      })
      .describe('User'),
    role: zod.enum(['MEMBER', 'ORGANIZER', 'ADMIN']).describe('Member role'),
    joinedAt: zod.iso.datetime({}).optional().describe('When the user joined the team'),
  })
  .describe('Team member information')

/**
 * Remove a member from the team. Requires ADMIN role.
 * @summary Remove team member
 */
export const RemoveMemberParams = zod.object({
  memberId: zod.string().describe('Member user ID (TSID)'),
  teamSlug: zod.string().describe('Team URL slug'),
})
