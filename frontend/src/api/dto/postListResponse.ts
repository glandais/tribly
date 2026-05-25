import type { PostDto } from './postDto.ts'

/**
 * Paginated post list response
 */
export interface PostListResponse {
  /** List of posts */
  posts: PostDto[]
  /** Total number of posts */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
