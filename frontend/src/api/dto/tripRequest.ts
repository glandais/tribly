import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { StageRequest } from './stageRequest.ts'
import type { Status } from './status.ts'
import type { Visibility } from './visibility.ts'

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
