import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { SurfaceType } from './surfaceType.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { Visibility } from './visibility.ts'

/**
 * Route summary data
 */
export interface RouteDto {
  /** Route ID (TSID) */
  id: string
  /** Route slug */
  slug: string
  /** Team */
  team: TeamPublicationDto
  /** Route name */
  name: string
  /** Route description */
  media: MediaDto
  /** Distance in meters */
  distance: number
  /** Total elevation gain in meters */
  elevationGain: number
  /** Total elevation loss in meters */
  elevationLoss: number
  /** Surface type */
  surfaceType: SurfaceType
  /** Whether the route is public */
  visibility: Visibility
  /** Creation timestamp */
  createdAt: Instant
  /** Whether the route is soft-deleted */
  deleted: boolean
}
