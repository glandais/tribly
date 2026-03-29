import * as zod from 'zod'

/**
 * @summary List post comments
 */
export const ListPostCommentsParams = zod.object({
  entitySlug: zod.string().describe('Post URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const ListPostCommentsResponse = zod
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
            createdAt: zod.iso.datetime({}).describe('Creation timestamp'),
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
 * @summary Create post comment
 */
export const CreatePostCommentParams = zod.object({
  entitySlug: zod.string().describe('Post URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createPostCommentBodyContentMax = 5000

export const createPostCommentBodyContentRegExp = new RegExp('\\S')

export const CreatePostCommentBody = zod
  .object({
    content: zod
      .string()
      .max(createPostCommentBodyContentMax)
      .regex(createPostCommentBodyContentRegExp)
      .describe('Comment content'),
    parentId: zod.string().optional().describe('Parent comment ID for replies (optional)'),
  })
  .describe('Comment creation request')

/**
 * @summary Delete post comment
 */
export const DeletePostCommentParams = zod.object({
  commentId: zod.string().describe('Comment ID'),
  entitySlug: zod.string().describe('Post URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})
