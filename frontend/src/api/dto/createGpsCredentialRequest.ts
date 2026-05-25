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
   * OAuth client secret (nullable for PKCE services)
   * @maxLength 500
   */
  clientSecret?: string
  /** Whether credential is active */
  active?: boolean
}
