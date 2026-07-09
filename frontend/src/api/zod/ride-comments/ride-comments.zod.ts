import * as zod from 'zod'

/**
 * @summary List ride comments
 */
export const ListRideCommentsParams = zod.object({
  entitySlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const ListRideCommentsResponse = zod
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
 * @summary Create ride comment
 */
export const CreateRideCommentParams = zod.object({
  entitySlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createRideCommentBodyContentMax = 5000

export const createRideCommentBodyContentRegExp = new RegExp('\\S')

export const CreateRideCommentBody = zod
  .object({
    content: zod
      .string()
      .max(createRideCommentBodyContentMax)
      .regex(createRideCommentBodyContentRegExp)
      .describe('Comment content'),
    parentId: zod.string().optional().describe('Parent comment ID for replies (optional)'),
  })
  .describe('Comment creation request')

export const CreateRideCommentResponse = zod
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

/**
 * @summary Delete ride comment
 */
export const DeleteRideCommentParams = zod.object({
  commentId: zod.string().describe('Comment ID'),
  entitySlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DeleteRideCommentResponse = zod.void()
