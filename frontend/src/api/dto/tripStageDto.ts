import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { PlaceDetailDto } from './placeDetailDto.ts'

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
