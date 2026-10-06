import type { Instant } from './instant.ts'

/**
 * Route entry within a ride for device applications
 */
export interface DeviceRideEntryDto {
  /** Route slug */
  routeSlug: string
  /** Route name */
  routeName: string
  /** Group name (null for ride-level route) */
  groupName?: string
  /** Distance in meters */
  distance: number
  /** Elevation gain in meters */
  elevationGain: number
  /** Start latitude */
  startLat?: number
  /** Start longitude */
  startLon?: number
  /** When this entry leaves, as an absolute instant (UTC): the group's time read at the ride's departure point local time, on the ride's local date; the ride's own startDateTime for the ride-level route and for a group without a time. Devices render it in their own zone. */
  startDateTime: Instant
}
