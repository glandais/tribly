import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { PlaceDetailDto } from './placeDetailDto.ts'
import type { RouteDto } from './routeDto.ts'

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
  /** Route */
  route?: RouteDto
  /** Start place */
  startPlace?: PlaceDetailDto
  /** End place */
  endPlace?: PlaceDetailDto
  /** Stage media */
  media: MediaDto
  /** Sort order */
  sortOrder: number
  /** Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print. */
  stageIndex: number
  /** How many live stages the trip has — the '/ 5' of 'Day 2 / 5'. */
  stageCount: number
}
