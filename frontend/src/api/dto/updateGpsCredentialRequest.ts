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
}
