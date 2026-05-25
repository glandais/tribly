import type { GpsServiceType } from './gpsServiceType.ts'
import type { Instant } from './instant.ts'

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
