import type { BoundsDto } from './boundsDto.ts'

/**
 * Bounding box of the routes matching a filter set
 */
export interface RouteBoundsResponse {
  /** Bounding box, or null when no route matches */
  bounds?: BoundsDto
}
