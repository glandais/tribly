import type { Instant } from './instant.ts'
import type { TrackDto } from './trackDto.ts'
import type { WaypointDto } from './waypointDto.ts'

/**
 * Analysed GPX file, not attached to any team
 */
export interface GpxPreviewDto {
  /** Public identifier used in URLs */
  id: string
  /** Track name */
  name: string
  /** Distance in meters */
  distance: number
  /** Total elevation gain in meters */
  elevationGain: number
  /** Total elevation loss in meters */
  elevationLoss: number
  /** Elevation gain per kilometer */
  hilliness: number
  /** Whether the current user created this preview */
  owned: boolean
  /** Creation timestamp */
  createdAt: Instant
  /** Tracks */
  tracks: TrackDto[]
  /** Waypoints */
  waypoints: WaypointDto[]
}
