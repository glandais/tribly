import type { Instant } from './instant.ts'
import type { PublicUserDto } from './publicUserDto.ts'

/**
 * Comment data
 */
export interface CommentDto {
  /** Comment ID (TSID) */
  id: string
  /** Comment content */
  content: string
  /** Comment author */
  author: PublicUserDto
  /** Creation timestamp */
  createdAt: Instant
  /** Parent comment ID (for replies) */
  parentId?: string
  /** Replies to this comment */
  replies: CommentDto[]
  /** How many replies this comment has. Equal to replies.size() when the whole thread is embedded; a client that loads threads on demand uses it to decide whether ?parentId= is worth a call. Always 0 on a reply — threading is one level deep. */
  replyCount: number
}
