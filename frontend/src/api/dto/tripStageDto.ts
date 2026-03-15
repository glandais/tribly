import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { PlaceDetailDto } from './placeDetailDto'

/**
 * Trip stage information
 */
export interface TripStageDto {
  /** Stage ID (TSID) */
  id: string
  /** Stage slug */
  slug: string
  /** Stage name */
  name: string
  /** Stage date/time */
  dateTime: Instant
  /** Route slug */
  routeSlug?: string
  /** Start place */
  startPlace?: PlaceDetailDto
  /** End place */
  endPlace?: PlaceDetailDto
  /** Stage media */
  media: MediaDto
  /** Sort order */
  sortOrder: number
}
