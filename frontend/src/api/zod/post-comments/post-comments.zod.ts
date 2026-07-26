import * as zod from 'zod'

/**
 * Top-level comments with their replies. Passing neither page nor size returns the whole tree, as before this endpoint took parameters; passing either paginates the top-level comments. parentId switches to listing the replies of a single comment.
 * @summary List post comments
 */
export const ListPostCommentsParams = zod.object({
  entitySlug: zod.string().describe('Post URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const ListPostCommentsQueryParams = zod.object({
  page: zod
    .number()
    .optional()
    .describe(
      'Page number (0-indexed). Omit both page and size to get the whole comment tree, as before this endpoint took parameters.'
    ),
  parentId: zod
    .string()
    .optional()
    .describe(
      'Load only the replies of this comment (TSID) instead of the top-level comments. Use it to expand one thread without re-reading the others.'
    ),
  size: zod.number().optional().describe('Page size, capped at 100 (default 20)'),
  sort: zod
    .enum(['ASC', 'DESC'])
    .optional()
    .describe('Sort by creation date: ASC (oldest first, default) or DESC'),
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
            createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
            parentId: zod.string().optional().describe('Parent comment ID (for replies)'),
            replies: zod.array(zod.unknown()).describe('Replies to this comment'),
            replyCount: zod
              .number()
              .describe(
                'How many replies this comment has. Equal to replies.size() when the whole thread is embedded; a client that loads threads on demand uses it to decide whether ?parentId= is worth a call. Always 0 on a reply — threading is one level deep.'
              ),
          })
          .describe('Comment data')
      )
      .describe('List of comments (top-level only, with nested replies)'),
    total: zod.number().describe('Total count including replies'),
    itemTotal: zod
      .number()
      .describe(
        'How many items exist in the mode that was asked for: top-level comments normally, or replies of the requested parentId. This is what page\/size iterate over.'
      ),
    page: zod.number().describe('Page number of items (0-indexed)'),
    size: zod
      .number()
      .describe(
        'Page size applied to items. Equals itemTotal when the caller passed no pagination parameter, since the whole tree is then returned.'
      ),
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

export const CreatePostCommentResponse = zod
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
    replyCount: zod
      .number()
      .describe(
        'How many replies this comment has. Equal to replies.size() when the whole thread is embedded; a client that loads threads on demand uses it to decide whether ?parentId= is worth a call. Always 0 on a reply — threading is one level deep.'
      ),
  })
  .describe('Comment data')

/**
 * @summary Delete post comment
 */
export const DeletePostCommentParams = zod.object({
  commentId: zod.string().describe('Comment ID'),
  entitySlug: zod.string().describe('Post URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DeletePostCommentResponse = zod.void()
