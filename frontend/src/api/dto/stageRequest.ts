import type { EventDateTime } from './eventDateTime.ts'
import type { MediaDto } from './mediaDto.ts'

/**
 * Trip stage creation request
 */
export interface StageRequest {
  /** Stage ID (for updates) */
  id?: string
  /**
   * Stage name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Stage date/time: a wall time without offset, read in the stage's zone (start place, else route, else the previous stage's, else the trip route's, else the team's). */
  dateTime: EventDateTime
  /**
   * Average speed in km/h
   * @exclusiveMinimum 0
   */
  averageSpeed?: number
  /** Route slug for this stage */
  routeSlug?: string
  /** Start place ID (TSID) */
  startPlaceId?: string
  /** End place ID (TSID) */
  endPlaceId?: string
  /** Stage media */
  media: MediaDto
}
