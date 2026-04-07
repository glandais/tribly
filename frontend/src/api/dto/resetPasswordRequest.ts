/**
 * Reset password request
 */
export interface ResetPasswordRequest {
  /**
   * Password reset token
   * @maxLength 100
   * @pattern \S
   */
  token: string
  /**
   * New password (min 8 chars)
   * @minLength 8
   * @maxLength 100
   * @pattern \S
   */
  newPassword: string
}
