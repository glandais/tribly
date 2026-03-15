/**
 * Device OAuth token response
 */
export interface DeviceTokenResponse {
  /** Access token */
  accessToken: string
  /** Token type (always 'Bearer') */
  tokenType: string
  /** Token expiry in seconds */
  expiresIn: number
  /** Refresh token */
  refreshToken?: string
}
