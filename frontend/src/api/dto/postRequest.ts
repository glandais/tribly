import type { EventDateTime } from './eventDateTime.ts'
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
  /** Post date/time: a wall time without offset, read in the team's zone. */
  dateTime: EventDateTime
  /** Post status */
  status: Status
  /** Visibility level */
  visibility: Visibility
  /** Publication time (for scheduled publishing), a wall time in the team's zone like dateTime. */
  publishAt?: EventDateTime
  /** Sign the post as the team rather than as its author. Omitted: on creation, the team's postsAsTeamByDefault; on an update, left as it is. */
  signedAsTeam?: boolean
  /** IDs (TSID) of the team's POST tags the post carries, replacing the whole set — at most 10, each a tag of this team and of kind POST, else 400 (TAG_INVALID, TOO_MANY_TAGS). An empty list removes them all. Omitted: none on a creation, left as they are on an update. */
  tagIds?: string[]
}
