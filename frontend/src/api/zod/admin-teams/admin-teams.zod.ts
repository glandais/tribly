import * as zod from 'zod'

/**
 * Get a paginated list of all teams
 * @summary List all teams
 */
export const adminListTeamsQueryPageDefault = 0
export const adminListTeamsQuerySizeDefault = 20

export const AdminListTeamsQueryParams = zod.object({
  domainId: zod.string().optional().describe('Filter by domain ID'),
  page: zod.number().default(adminListTeamsQueryPageDefault).describe('Page number (0-indexed)'),
  size: zod.number().default(adminListTeamsQuerySizeDefault).describe('Page size'),
})

export const AdminListTeamsResponse = zod
  .object({
    teams: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Team ID (TSID)'),
            name: zod.string().describe('Team name'),
            slug: zod.string().describe('Team URL slug'),
            domainId: zod.string().describe('Domain ID this team belongs to'),
            domainName: zod.string().describe('Domain hostname'),
            visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Team visibility'),
            visibilityEditable: zod
              .boolean()
              .describe('Whether visibility is editable by team admins'),
            joinable: zod.boolean().describe('Whether any domain user can join this team'),
            addMemberAllowed: zod.boolean().describe('Whether team admins can add members'),
            deleted: zod.boolean().describe('Is team soft-deleted'),
            memberCount: zod.number().describe('Number of members'),
            createdAt: zod.iso.datetime({}).describe('Team creation timestamp'),
          })
          .describe('Admin team view with domain info')
      )
      .describe('List of teams'),
    total: zod.number().describe('Total number of teams'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated admin team list response')

/**
 * Get detailed team information
 * @summary Get team details
 */
export const AdminGetTeamParams = zod.object({
  teamId: zod.string().describe('Team ID'),
})

export const AdminGetTeamResponse = zod
  .object({
    id: zod.string().describe('Team ID (TSID)'),
    name: zod.string().describe('Team name'),
    slug: zod.string().describe('Team URL slug'),
    domainId: zod.string().describe('Domain ID this team belongs to'),
    domainName: zod.string().describe('Domain hostname'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Team visibility'),
    visibilityEditable: zod.boolean().describe('Whether visibility is editable by team admins'),
    joinable: zod.boolean().describe('Whether any domain user can join this team'),
    addMemberAllowed: zod.boolean().describe('Whether team admins can add members'),
    deleted: zod.boolean().describe('Is team soft-deleted'),
    memberCount: zod.number().describe('Number of members'),
    createdAt: zod.iso.datetime({}).describe('Team creation timestamp'),
  })
  .describe('Admin team view with domain info')

/**
 * Update platform-controlled team governance attributes
 * @summary Update team governance attributes
 */
export const AdminUpdateTeamAttributesParams = zod.object({
  teamId: zod.string().describe('Team ID'),
})

export const AdminUpdateTeamAttributesBody = zod
  .object({
    visibilityEditable: zod.boolean().describe('Whether team admins can change visibility'),
    joinable: zod.boolean().describe('Whether any domain user can join this public team'),
    addMemberAllowed: zod.boolean().describe('Whether team admins can add members'),
  })
  .describe('Platform admin request to update team governance attributes')

export const AdminUpdateTeamAttributesResponse = zod
  .object({
    id: zod.string().describe('Team ID (TSID)'),
    name: zod.string().describe('Team name'),
    slug: zod.string().describe('Team URL slug'),
    domainId: zod.string().describe('Domain ID this team belongs to'),
    domainName: zod.string().describe('Domain hostname'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Team visibility'),
    visibilityEditable: zod.boolean().describe('Whether visibility is editable by team admins'),
    joinable: zod.boolean().describe('Whether any domain user can join this team'),
    addMemberAllowed: zod.boolean().describe('Whether team admins can add members'),
    deleted: zod.boolean().describe('Is team soft-deleted'),
    memberCount: zod.number().describe('Number of members'),
    createdAt: zod.iso.datetime({}).describe('Team creation timestamp'),
  })
  .describe('Admin team view with domain info')

/**
 * Toggle team soft-delete status (archive/restore)
 * @summary Toggle team deleted
 */
export const AdminToggleTeamDeletedParams = zod.object({
  teamId: zod.string().describe('Team ID'),
})

export const AdminToggleTeamDeletedResponse = zod
  .object({
    id: zod.string().describe('Team ID (TSID)'),
    name: zod.string().describe('Team name'),
    slug: zod.string().describe('Team URL slug'),
    domainId: zod.string().describe('Domain ID this team belongs to'),
    domainName: zod.string().describe('Domain hostname'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Team visibility'),
    visibilityEditable: zod.boolean().describe('Whether visibility is editable by team admins'),
    joinable: zod.boolean().describe('Whether any domain user can join this team'),
    addMemberAllowed: zod.boolean().describe('Whether team admins can add members'),
    deleted: zod.boolean().describe('Is team soft-deleted'),
    memberCount: zod.number().describe('Number of members'),
    createdAt: zod.iso.datetime({}).describe('Team creation timestamp'),
  })
  .describe('Admin team view with domain info')
