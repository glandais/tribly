import type { GpsOAuthVersion } from './gpsOAuthVersion.ts'

/**
 * Request to update a GPS credential
 */
export interface UpdateGpsCredentialRequest {
  /**
   * OAuth client ID
   * @maxLength 255
   * @pattern \S
   */
  clientId: string
  /**
   * OAuth client secret (null = keep current)
   * @maxLength 500
   */
  clientSecret?: string
  /** Whether credential is active */
  active?: boolean
  /** OAuth protocol of the client ID and secret (null = keep current). OAUTH1 is accepted for GARMIN only. Switching it leaves existing connections unusable: each is dropped at its next upload and must be reconnected */
  oauthVersion?: GpsOAuthVersion
}
