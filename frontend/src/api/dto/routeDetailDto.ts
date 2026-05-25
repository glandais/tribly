import type { GeoJsonPoint } from './geoJsonPoint.ts'
import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { PublicUserDto } from './publicUserDto.ts'
import type { SurfaceType } from './surfaceType.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { TrackDto } from './trackDto.ts'
import type { Visibility } from './visibility.ts'
import type { WaypointDto } from './waypointDto.ts'

/**
 * Detailed route information
 */
export interface RouteDetailDto {
  /** Route ID (TSID) */
  id: string
  /** Route slug */
  slug: string
  /** Team */
  team: TeamPublicationDto
  /** Route name */
  name: string
  /** Media */
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
  start?: GeoJsonPoint
  end?: GeoJsonPoint
  /** Creator user */
  createdBy: PublicUserDto
  /** Creation timestamp */
  createdAt: Instant
  /** Last update timestamp */
  updatedAt: Instant
  /** Tracks */
  tracks: TrackDto[]
  /** Waypoints */
  waypoints: WaypointDto[]
  /** Whether the route is soft-deleted */
  deleted: boolean
}
