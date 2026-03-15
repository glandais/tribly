import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { StageRequest } from './stageRequest'
import type { Status } from './status'
import type { Visibility } from './visibility'

/**
 * Trip request
 */
export interface TripRequest {
  /**
   * Trip name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Trip media */
  media: MediaDto
  /** Trip start date/time */
  dateTime: Instant
  /** Trip status */
  status: Status
  /** Visibility level */
  visibility: Visibility
  /** Overall route slug for the trip */
  routeSlug?: string
  /** Publication timestamp (for scheduled publishing) */
  publishAt?: Instant
  /** Trip stages to create */
  stages: StageRequest[]
}
