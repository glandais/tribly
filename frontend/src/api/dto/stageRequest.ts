import type { Instant } from './instant.ts'
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
  /** Stage date/time */
  dateTime: Instant
  /** Route slug for this stage */
  routeSlug?: string
  /** Start place ID (TSID) */
  startPlaceId?: string
  /** End place ID (TSID) */
  endPlaceId?: string
  /** Stage media */
  media: MediaDto
}
