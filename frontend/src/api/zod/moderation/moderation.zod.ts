import * as zod from 'zod'

/**
 * Files a report in a team, about a comment, a publication, an ad, a route or a member the caller can read there. Idempotent: reporting the same target again changes nothing. The target disappears from the caller's lists at once; the team's organizers and administrators are notified, without the caller's name.
 * @summary Report content or a member
 */
export const reportContentBodyTeamSlugRegExp = new RegExp('\\S')
export const reportContentBodyTargetIdRegExp = new RegExp('\\S')
export const reportContentBodyMessageMax = 500

export const ReportContentBody = zod
  .object({
    teamSlug: zod
      .string()
      .regex(reportContentBodyTeamSlugRegExp)
      .describe(
        'The team the target belongs to. Its organizers and administrators moderate the report.'
      ),
    targetType: zod
      .enum(['COMMENT', 'POST', 'AD', 'RIDE', 'TRIP', 'ROUTE', 'MEMBER'])
      .describe('What is reported'),
    targetId: zod
      .string()
      .regex(reportContentBodyTargetIdRegExp)
      .describe('ID (TSID) of the comment, publication, ad or route — or of the user for a MEMBER'),
    reason: zod
      .enum([
        'SPAM',
        'HARASSMENT',
        'HATE',
        'SEXUAL',
        'VIOLENCE',
        'ILLEGAL',
        'INAPPROPRIATE_IMAGE',
        'OTHER',
      ])
      .describe('Why'),
    message: zod
      .string()
      .max(reportContentBodyMessageMax)
      .optional()
      .describe('Optional free text for the moderators, up to 500 characters'),
  })
  .describe('A report of a comment, a publication, an ad, a route or a member')

export const ReportContentResponse = zod.void()

/**
 * Reports grouped by target, without the reporters' identities. OPEN lists every target waiting for a decision; RESOLVED the 100 most recently decided. Reports about the caller — their content, or themselves — are left out.
 * @summary List the team's moderation queue
 */
export const ListTeamReportsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const ListTeamReportsQueryParams = zod.object({
  status: zod.enum(['OPEN', 'RESOLVED']).optional().describe('OPEN (default) or RESOLVED'),
})

export const ListTeamReportsResponse = zod
  .object({
    items: zod
      .array(
        zod
          .object({
            targetType: zod
              .enum(['COMMENT', 'POST', 'AD', 'RIDE', 'TRIP', 'ROUTE', 'MEMBER'])
              .describe('Type of the reported target'),
            targetId: zod.string().describe('ID (TSID) of the reported target'),
            teamSlug: zod.string().describe('Slug of the team the reports were filed in'),
            teamName: zod.string().describe('Name of that team'),
            targetUser: zod
              .object({
                id: zod.string().describe('User ID (TSID)'),
                displayName: zod.string().describe('User display name'),
                avatarUrl: zod.string().optional().describe('User avatar URL'),
              })
              .describe('Who the moderation is about: the author of the content, or the member'),
            contentName: zod
              .string()
              .optional()
              .describe(
                'Name of the publication — of the commented one for a comment. Null for a member, or when the content is gone.'
              ),
            contentType: zod
              .enum(['COMMENT', 'POST', 'AD', 'RIDE', 'TRIP', 'ROUTE', 'MEMBER'])
              .optional()
              .describe(
                'Type of the content to open: the publication itself, or the one a comment is on (POST, RIDE, TRIP, ROUTE or AD). Null for a member, or when the content is gone.'
              ),
            contentSlug: zod
              .string()
              .optional()
              .describe('Slug of the content to open, with contentType'),
            excerpt: zod
              .string()
              .optional()
              .describe(
                'The reported text as it was when first reported (comment text, name and start of the description, or member name)'
              ),
            reportCount: zod.int().describe('How many reports this target gathered'),
            reasons: zod
              .array(
                zod.enum([
                  'SPAM',
                  'HARASSMENT',
                  'HATE',
                  'SEXUAL',
                  'VIOLENCE',
                  'ILLEGAL',
                  'INAPPROPRIATE_IMAGE',
                  'OTHER',
                ])
              )
              .describe('The distinct reasons given'),
            messages: zod.array(zod.string()).describe('The non-empty free texts of the reports'),
            firstReportedAt: zod.iso
              .datetime({ offset: true })
              .describe('When the first report was filed'),
            lastReportedAt: zod.iso
              .datetime({ offset: true })
              .describe('When the last report was filed'),
            hidden: zod
              .boolean()
              .describe(
                'Whether the content is currently hidden from members, having gathered enough reports'
              ),
            status: zod
              .enum(['OPEN', 'REMOVED', 'DISMISSED'])
              .describe(
                'OPEN while waiting; REMOVED or DISMISSED once decided (the latest decision)'
              ),
            reporters: zod
              .array(
                zod
                  .object({
                    id: zod.string().describe('User ID (TSID)'),
                    displayName: zod.string().describe('User display name'),
                    avatarUrl: zod.string().optional().describe('User avatar URL'),
                  })
                  .describe('Public user information (limited fields)')
              )
              .optional()
              .describe(
                "Who reported. Only in the platform queue: always null in a team's queue, where reporters stay anonymous."
              ),
          })
          .describe('One reported target in a moderation queue, with all its reports grouped')
      )
      .describe('One item per reported target'),
    total: zod.int().describe('How many items'),
  })
  .describe(
    'A moderation queue: every open target, or the 100 most recently decided ones, one item per target'
  )

/**
 * Applies the decision to every open report of the target. REMOVE_CONTENT deletes the content (not allowed on a member); DISMISS keeps it, and shows it again if reports had hidden it.
 * @summary Decide about a reported target
 */
export const ResolveTeamReportsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const resolveTeamReportsBodyTargetIdRegExp = new RegExp('\\S')

export const ResolveTeamReportsBody = zod
  .object({
    targetType: zod
      .enum(['COMMENT', 'POST', 'AD', 'RIDE', 'TRIP', 'ROUTE', 'MEMBER'])
      .describe('Type of the reported target'),
    targetId: zod
      .string()
      .regex(resolveTeamReportsBodyTargetIdRegExp)
      .describe('ID (TSID) of the reported target'),
    action: zod
      .enum(['REMOVE_CONTENT', 'DISMISS'])
      .describe(
        'REMOVE_CONTENT deletes the content (not allowed on a MEMBER); DISMISS keeps it and shows it again if reports had hidden it'
      ),
  })
  .describe("A moderator's decision, applied to every open report of one target")

export const ResolveTeamReportsResponse = zod.void()

/**
 * Most recent first.
 * @summary List the members I blocked
 */
export const ListMyBlockedUsersResponse = zod
  .object({
    users: zod
      .array(
        zod
          .object({
            id: zod.string().describe('User ID (TSID)'),
            displayName: zod.string().describe('User display name'),
            avatarUrl: zod.string().optional().describe('User avatar URL'),
          })
          .describe('Public user information (limited fields)')
      )
      .describe('Blocked members'),
  })
  .describe('The members the current user blocked, most recent first')

/**
 * Idempotent.
 * @summary Unblock a member
 */
export const UnblockUserParams = zod.object({
  userId: zod.string().describe('User ID (TSID)'),
})

export const UnblockUserResponse = zod.void()

/**
 * Hides the member's comments, posts and ads from the caller, and the comment notifications they cause. Idempotent.
 * @summary Block a member
 */
export const BlockUserParams = zod.object({
  userId: zod.string().describe('User ID (TSID)'),
})

export const BlockUserResponse = zod.void()
