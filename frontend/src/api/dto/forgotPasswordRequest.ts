/**
 * Forgot password request
 */
export interface ForgotPasswordRequest {
  /**
   * Email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
}
