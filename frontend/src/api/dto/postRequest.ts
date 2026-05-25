import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { Status } from './status.ts'
import type { Visibility } from './visibility.ts'

/**
 * Post request
 */
export interface PostRequest {
  /**
   * Post name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Post description */
  media: MediaDto
  /** Post date/time */
  dateTime: Instant
  /** Post status */
  status: Status
  /** Visibility level */
  visibility: Visibility
  /** Publication timestamp (for scheduled publishing) */
  publishAt?: Instant
}
