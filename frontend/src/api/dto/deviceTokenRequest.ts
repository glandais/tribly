/**
 * Device OAuth token request
 */
export interface DeviceTokenRequest {
  /**
   * Grant type: 'urn:ietf:params:oauth:grant-type:device_code' or 'refresh_token'
   * @pattern \S
   */
  grantType: string
  /** Device code (for device_code grant) */
  deviceCode?: string
  /** Refresh token (for refresh_token grant) */
  refreshToken?: string
}
