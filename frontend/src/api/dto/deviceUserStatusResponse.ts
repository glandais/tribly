import type { GpsServiceType } from './gpsServiceType.ts'

/**
 * Response containing user's device connection status
 */
export interface DeviceUserStatusResponse {
  /** List of connected GPS services */
  connectedGpsServices: GpsServiceType[]
}
