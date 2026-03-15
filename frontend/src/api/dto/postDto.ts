import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { PostDtoType } from './postDtoType'
import type { Status } from './status'
import type { TeamPublicationDto } from './teamPublicationDto'
import type { Visibility } from './visibility'

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
}
