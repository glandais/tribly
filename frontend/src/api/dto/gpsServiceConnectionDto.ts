import type { GpsServiceType } from './gpsServiceType'
import type { Instant } from './instant'

/**
 * GPS service connection information
 */
export interface GpsServiceConnectionDto {
  /** Service type identifier */
  serviceType: GpsServiceType
  /** Display name of the service */
  displayName: string
  /** When the service was connected */
  connectedAt: Instant
}
