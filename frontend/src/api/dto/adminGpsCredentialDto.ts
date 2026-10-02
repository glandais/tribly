import type { GpsOAuthVersion } from './gpsOAuthVersion.ts'
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
  /** OAuth client ID (consumer key for OAuth 1.0a) */
  clientId: string
  /** OAuth protocol of the client ID and secret */
  oauthVersion: GpsOAuthVersion
  /** Whether credential is active */
  active: boolean
  /** Credential creation timestamp */
  createdAt: Instant
}
