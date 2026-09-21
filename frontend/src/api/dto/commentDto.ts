import type { Instant } from './instant.ts'
import type { PublicUserDto } from './publicUserDto.ts'

/**
 * Comment data
 */
export interface CommentDto {
  /** Comment ID (TSID) */
  id: string
  /** Comment content. Empty when the comment is deleted — see the deleted flag. */
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
  /** True for the comment of a deleted account that others had answered. It stays only to carry its replies: the content is empty, and clients render a placeholder with neither author nor actions. */
  deleted: boolean
}
