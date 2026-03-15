/**
 * Request for passkey authentication options
 */
export interface PasskeyAuthenticationRequest {
  /**
   * Optional email to filter passkeys
   * @maxLength 250
   */
  email?: string
}
