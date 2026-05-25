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
}
