import * as zod from 'zod'

/**
 * @summary List trip comments
 */
export const ListTripCommentsParams = zod.object({
  entitySlug: zod.string().describe('Trip URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const ListTripCommentsResponse = zod
  .object({
    items: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Comment ID (TSID)'),
            content: zod.string().describe('Comment content'),
            author: zod
              .object({
                id: zod.string().describe('User ID (TSID)'),
                displayName: zod.string().describe('User display name'),
                avatarUrl: zod.string().optional().describe('User avatar URL'),
              })
              .describe('Comment author'),
            createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
            parentId: zod.string().optional().describe('Parent comment ID (for replies)'),
            replies: zod.array(zod.unknown()).describe('Replies to this comment'),
          })
          .describe('Comment data')
      )
      .describe('List of comments (top-level only, with nested replies)'),
    total: zod.number().describe('Total count including replies'),
  })
  .describe('List of comments response')

/**
 * @summary Create trip comment
 */
export const CreateTripCommentParams = zod.object({
  entitySlug: zod.string().describe('Trip URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createTripCommentBodyContentMax = 5000

export const createTripCommentBodyContentRegExp = new RegExp('\\S')

export const CreateTripCommentBody = zod
  .object({
    content: zod
      .string()
      .max(createTripCommentBodyContentMax)
      .regex(createTripCommentBodyContentRegExp)
      .describe('Comment content'),
    parentId: zod.string().optional().describe('Parent comment ID for replies (optional)'),
  })
  .describe('Comment creation request')

/**
 * @summary Delete trip comment
 */
export const DeleteTripCommentParams = zod.object({
  commentId: zod.string().describe('Comment ID'),
  entitySlug: zod.string().describe('Trip URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})
