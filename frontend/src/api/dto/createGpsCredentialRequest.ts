import type { GpsOAuthVersion } from './gpsOAuthVersion.ts'
import type { GpsServiceType } from './gpsServiceType.ts'

/**
 * Request to create a new GPS credential
 */
export interface CreateGpsCredentialRequest {
  /** GPS service type */
  serviceType: GpsServiceType
  /**
   * OAuth client ID
   * @maxLength 255
   * @pattern \S
   */
  clientId: string
  /**
   * OAuth client secret, or consumer secret for OAuth 1.0a (required then)
   * @maxLength 500
   */
  clientSecret?: string
  /** Whether credential is active */
  active?: boolean
  /** OAuth protocol of the client ID and secret (null = OAUTH2). OAUTH1 is accepted for GARMIN only; the client ID is then the consumer key */
  oauthVersion?: GpsOAuthVersion
}
