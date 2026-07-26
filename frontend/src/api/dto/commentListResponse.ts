import type { CommentDto } from './commentDto.ts'

/**
 * List of comments response
 */
export interface CommentListResponse {
  /** List of comments (top-level only, with nested replies) */
  items: CommentDto[]
  /** Total count including replies */
  total: number
  /** How many items exist in the mode that was asked for: top-level comments normally, or replies of the requested parentId. This is what page/size iterate over. */
  itemTotal: number
  /** Page number of items (0-indexed) */
  page: number
  /** Page size applied to items. Equals itemTotal when the caller passed no pagination parameter, since the whole tree is then returned. */
  size: number
}
