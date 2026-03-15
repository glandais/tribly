import type { GroupRequest } from './groupRequest'
import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { Status } from './status'
import type { Visibility } from './visibility'

/**
 * Ride request
 */
export interface RideRequest {
  /**
   * Ride name
   * @minLength 3
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Ride media */
  media: MediaDto
  /** Ride date/time */
  dateTime: Instant
  /** Ride status */
  status: Status
  /** Visibility level */
  visibility: Visibility
  /** Route slug */
  routeSlug?: string
  /** Start place ID (TSID) */
  startPlaceId?: string
  /** End place ID (TSID) */
  endPlaceId?: string
  /** Publication timestamp (for scheduled publishing) */
  publishAt?: Instant
  /** Ride groups to create */
  groups: GroupRequest[]
}
