import type { GpsServiceType } from './gpsServiceType.ts'
import type { Instant } from './instant.ts'

/**
 * Admin GPS credential view (without secret)
 */
export interface AdminGpsCredentialDto {
  /** Credential ID (TSID) */
  id: string
  /** GPS service type */
  serviceType: GpsServiceType
  /** OAuth client ID */
  clientId: string
  /** Whether credential is active */
  active: boolean
  /** Credential creation timestamp */
  createdAt: Instant
}
