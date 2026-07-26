import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { PostDtoType } from './postDtoType.ts'
import type { Status } from './status.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { Visibility } from './visibility.ts'

/**
 * Post summary data
 */
export interface PostDto {
  type: PostDtoType
  /** Team */
  team: TeamPublicationDto
  /** Publication ID (TSID) */
  id: string
  /** Publication URL slug */
  slug: string
  /** Publication name */
  name: string
  /** Publication media */
  media: MediaDto
  /** Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter. */
  excerpt?: string
  /** URL template of the post's first image, the one a card shows. Saves a compact row from carrying media.assets just to find a picture. */
  thumbnailUrl?: string
  /** Publication date/time */
  dateTime: Instant
  /** Publication status */
  status: Status
  /** Visibility level */
  visibility: Visibility
  /** Publication timestamp */
  publishAt?: Instant
  /** Creation timestamp */
  createdAt?: Instant
  /** Whether the post is soft-deleted */
  deleted: boolean
  /** Number of comments, replies included. Absent when the caller may not read the comments of this post — comments are members-only, so an outsider is told nothing, not even zero. */
  commentCount?: number
}
