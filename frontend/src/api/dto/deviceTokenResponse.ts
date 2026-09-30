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
  /** Refresh token, to keep in place of the one presented: it rotates at every refresh. Absent when the refresh came within the grace of a rotation made by another one — keep the token already held. */
  refreshToken?: string
}
