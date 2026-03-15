/**
 * Token verification request
 */
export interface VerifyTokenRequest {
  /**
   * Verification token
   * @maxLength 100
   * @pattern \S
   */
  token: string
}
