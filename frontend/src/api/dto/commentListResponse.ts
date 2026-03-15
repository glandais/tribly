import type { CommentDto } from './commentDto'

/**
 * List of comments response
 */
export interface CommentListResponse {
  /** List of comments (top-level only, with nested replies) */
  items: CommentDto[]
  /** Total count including replies */
  total: number
}
