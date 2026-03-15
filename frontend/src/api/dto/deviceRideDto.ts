import type { DeviceRideEntryDto } from './deviceRideEntryDto'
import type { Instant } from './instant'

/**
 * Ride information for device applications
 */
export interface DeviceRideDto {
  /** Team slug */
  teamSlug: string
  /** Ride slug */
  rideSlug: string
  /** Ride name */
  rideName: string
  /** Start date/time */
  startDateTime?: Instant
  /** Route entries for this ride */
  entries: DeviceRideEntryDto[]
}
