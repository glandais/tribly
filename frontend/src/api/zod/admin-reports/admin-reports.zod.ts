import * as zod from 'zod'

/**
 * Reports of every team of the domain, grouped by target, with the reporters. OPEN lists every target waiting for a decision; RESOLVED the 100 most recently decided.
 * @summary List the platform moderation queue
 */
export const ListAdminReportsQueryParams = zod.object({
  status: zod.enum(['OPEN', 'RESOLVED']).optional().describe('OPEN (default) or RESOLVED'),
})

export const ListAdminReportsResponse = zod
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
 * @summary Decide about a reported target, in any team
 */
export const resolveAdminReportsBodyTargetIdRegExp = new RegExp('\\S')

export const ResolveAdminReportsBody = zod
  .object({
    targetType: zod
      .enum(['COMMENT', 'POST', 'AD', 'RIDE', 'TRIP', 'ROUTE', 'MEMBER'])
      .describe('Type of the reported target'),
    targetId: zod
      .string()
      .regex(resolveAdminReportsBodyTargetIdRegExp)
      .describe('ID (TSID) of the reported target'),
    action: zod
      .enum(['REMOVE_CONTENT', 'DISMISS'])
      .describe(
        'REMOVE_CONTENT deletes the content (not allowed on a MEMBER); DISMISS keeps it and shows it again if reports had hidden it'
      ),
  })
  .describe("A moderator's decision, applied to every open report of one target")

export const ResolveAdminReportsResponse = zod.void()
