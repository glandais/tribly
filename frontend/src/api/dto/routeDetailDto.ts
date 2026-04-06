import type { GeoJsonPoint } from './geoJsonPoint'
import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { PublicUserDto } from './publicUserDto'
import type { SurfaceType } from './surfaceType'
import type { TeamPublicationDto } from './teamPublicationDto'
import type { TrackDto } from './trackDto'
import type { Visibility } from './visibility'
import type { WaypointDto } from './waypointDto'

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
