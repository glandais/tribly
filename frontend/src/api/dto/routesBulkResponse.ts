import type { BoundsDto } from './boundsDto.ts'
import type { RouteDetailDto } from './routeDetailDto.ts'

/**
 * Detail of several routes fetched at once, plus their combined extent
 */
export interface RoutesBulkResponse {
  /** Route details, one per readable requested slug */
  routes: RouteDetailDto[]
  /** Bounding box of every returned route, or null when routes is empty */
  extent?: BoundsDto
}
