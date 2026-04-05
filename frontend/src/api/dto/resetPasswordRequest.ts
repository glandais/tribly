/**
 * Reset password request
 */
export interface ResetPasswordRequest {
  /**
   * Email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
  /**
   * 6-digit OTP code
   * @pattern ^\d{6}$
   */
  code: string
  /**
   * New password (min 8 chars)
   * @minLength 8
   * @maxLength 100
   * @pattern \S
   */
  newPassword: string
}
